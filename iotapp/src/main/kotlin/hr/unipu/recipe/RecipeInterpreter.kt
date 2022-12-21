package hr.unipu.recipe

import com.fasterxml.jackson.databind.ObjectMapper
import hr.unipu.plantcomputer.RecipeDTO
import hr.unipu.plantcomputer.RecipeDTO.Phases
import hr.unipu.ui.UiWindow
import java.io.File
import java.io.IOException
import java.net.URL
import java.nio.file.Paths
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * RecipeInterpreter interprets loaded recipe (simple and flex format).
 */
object RecipeInterpreter {
    // A threshold to compare time values in seconds.
    private const val THRESHOLD = 1

    // Number of millisecond from EPOCH (normally 1/01/1970 00:00 UTC)
    @JvmField
    var EPOCH = Date.from(Instant.ofEpochMilli(0))
    @JvmField
    var MIN_DATE : Long = 0     // For verifying time format.
    @JvmField
    var MAX_DATE: Long = 0
    @JvmField
    var recipe: RecipeDTO? = null

    /**
     * Initialization of:
     * - timezone;
     * - static variables;
     */
    @JvmStatic
    fun init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
        try {
            val date_min = SimpleDateFormat("MM-dd-yyyy HH:mm:ss").parse("06-11-2010 07:00:00")
            MIN_DATE = unix_time_seconds(date_min) //1.276.239.600 (seconds).
            val date_max = SimpleDateFormat("MM-dd-yyyy HH:mm:ss").parse("06-11-2035 07:00:00")
            MAX_DATE = unix_time_seconds(date_max) //2.065.158.000 (seconds).
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    /**
     * Defines unix_time_seconds(dt).
     */
    @JvmStatic
    fun unix_time_seconds(dt: Date): Long {
        val time = TimeUnit.SECONDS
        return time.toSeconds((dt.time - EPOCH.time) / 1000)
    }

    /**
     * Loading simple recipe.
     */
    fun loadSimpleRecipe(fileName: String): Map<*, *>? {
        val file = UiWindow.file
        var path: String? = ""
        if (file == null) {
            val loader = RecipeInterpreterJava::class.java.classLoader
            val pathURL: URL = loader.getResource("recipes/$fileName")
            path = Paths.get(pathURL.toURI()).toString()
        } else {
            path = file.path
        }
        val mapper = ObjectMapper()
        var recipe: Map<*, *>? = null
        recipe = mapper.readValue(Paths.get(path).toFile(), Map::class.java)

        return recipe
    }

    /**
     * Loading flex format recipe.
     */
    @JvmStatic
    fun loadFlexFormatRecipe(fileName: String): RecipeDTO? {
        val file = UiWindow.file
        var path: String? = ""
        if (file == null) {
            val loader: ClassLoader = RecipeInterpreterJava::class.java.classLoader
            val pathURL: URL = loader.getResource("recipes/$fileName")
            path = Paths.get(pathURL.toURI()).toString()
        } else {
            path = file.path
        }
        val mapper = ObjectMapper()
        recipe = null
        try {
            println("Path: " + Paths.get(path))
            recipe = mapper.readValue(Paths.get(path).toFile(), RecipeDTO::class.java)
            if (recipe?.format == "phased") {
                recipe?.mapOperations2Phases()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        println("Testing: air_temperature (should be 22) = " + recipe!!.phases[0].step.air_temperature?.get(0)?.value)
        return recipe
    }

    /**
     * Recipe interpreter (for simple recipe).
     * Produces a tuple of `(variable, value)` pairs by building up
     * a recipe state from walking through the recipe keyframes
     */
    fun interpret_simple_recipe(recipe: Map<*, *>, start_time: Long, now_time: Long): Map<*, *> {

        val _id = recipe["_id"] as String?
        val operations = recipe["operations"] as ArrayList<*>?
        println("_id = $_id")
        println("operations = " + operations.toString())
        val lastIndex = operations!!.size - 1
        val lastOperation = operations[lastIndex] as ArrayList<*>
        val end_time_relative: Long? = lastOperation[0] as Long?
        println("end_time_relative = $end_time_relative")
        System.out.printf("recipe_handler: interpret_simple_recipe end_time_relative=%s\n", end_time_relative)
        val end_time = start_time + end_time_relative!!

        // If start time is at some point in the future beyond the threshold.
        if (start_time - now_time > THRESHOLD) System.err.println("Recipes cannot be scheduled for the future.")

        // If there are no recipe operations, immediately start and stop.
        // The recipe.
        if (operations.size < 1) return java.util.Map.of(
            java.util.Map.of("recipe_start", _id),
            java.util.Map.of("recipe_end", _id)
        )
        if (now_time >= end_time + THRESHOLD) return java.util.Map.of("recipe_end", _id)
        if (Math.abs(now_time - start_time) < THRESHOLD) return java.util.Map.of("recipe_start", _id)
        val now_relative = now_time - start_time

        // Create a state object to accrue recipe setpoint values.
        val state: MutableMap<String, Double> = LinkedHashMap() // Keeps the keys in the order they were inserted.
        // Build up state up until now_time (inclusive).
        System.out.printf("recipe_handler: interpret_simple_recipe now=%s\n", now_relative)
        for (i in operations.indices) {
            val operation = operations[i] as ArrayList<*>
            val timestamp = operation[0] as Int
            val variable = operation[1] as String
            val value = operation[2].toString().toDouble()
            if (timestamp > now_relative) break
            state[variable] = value
            System.out.printf("recipe_handler: interpret_simple_recipe: %s %s %s\n", timestamp, variable, value)
        }
        return state
    }

    /**
     * Recipe Interpreter (for flex format recipes) should read a recipe, now_time, start_time, variable and return a value.
     * - Determine the time since the beginning of the recipe.
     * - Determine what is the current step
     * - Calculate the remaining time left in this step.
     * - Look up the value within that step for that variable.
     */
    @JvmStatic
    fun interpret_flexformat_recipe(recipe: RecipeDTO?, start_time: Long, now_time: Long): Map<*, *> {
        val _id = recipe!!._id
        println("_id = $_id")

        val phases: List<Phases> = recipe.phases
        println("phases = $phases")
        println("phases size = " + phases.size)

        verify_time_units(now_time)
        verify_time_units(start_time)

        // If start time is at some point in the future beyond the threshold.
        if (start_time - now_time > RecipeInterpreter.THRESHOLD) System.err.println("Recipes cannot be scheduled for the future.")

        // If there are no recipe operations, immediately start and stop.
        // The recipe.
        if (phases.size < 1) return java.util.Map.of(
            java.util.Map.of("recipe_start", _id),
            java.util.Map.of("recipe_end", _id)
        )
        if (Math.abs(now_time - start_time) < RecipeInterpreter.THRESHOLD) return java.util.Map.of(
            "recipe_start",
            _id
        )

        val time_units = RecipeInterpreter.verify_time_units_are_consistent(phases)
        println("time_units = $time_units")

        // Returns a list of the phases and step durations:
        val duration_of_phases_steps = RecipeInterpreter.calc_duration_of_phases_steps(phases)
        println("duration_of_phases_steps: $duration_of_phases_steps")

        val ans = RecipeInterpreter.calc_phase_and_time_remaining(
            duration_of_phases_steps,
            start_time,
            now_time,
            time_units
        )
        val current_phase_number = ans[0]
        val duration_in_step = ans[1]
        println("current_phase_number: $current_phase_number")
        println("duration_in_step: $duration_in_step")

        // Need to create a function to calculate the end time of the recipe
        val current_phase = phases[current_phase_number?.let { Math.toIntExact(it) }!!]

        // Create a state object to accrue recipe setpoint values.
        val state: MutableMap<String, Double> = LinkedHashMap()

        val items = current_phase.step.items()

        val size_of_step = items.size
        println("size_of_step (in current phase): $size_of_step")

        val start_time_variable = 0L
        val end_time_variable = 0L
        val value = 0.0
        val variable = ""

        for (i in 0 until size_of_step) {
            val size_of_item = items[i].size
            println("size_of_item (in current step): $size_of_item")
            val variable_step_data = items[i]
            val result = RecipeInterpreter.determine_value_for_step(
                variable_step_data,
                duration_in_step
            )
            state.putAll(result)
        }

        return state
    }

    /**
     * Verifies the units for incoming time variables are valid.
     */
    @JvmStatic
    fun verify_time_units(time_var: Long) {
        if (MIN_DATE < time_var && time_var < MAX_DATE) return else System.err.println("Variable time format is not correct. The value should be between {" + MIN_DATE + "} and {" + MAX_DATE + "}, but received: " + time_var)
    }

    /**
     * The time units are stored for each phase in the recipe rather than at the recipe level.
     */
    @JvmStatic
    fun verify_time_units_are_consistent(phases: List<Phases>): String {
        var time_units = ""
        for (phase in phases) {
            if (phase.time_units.isEmpty()) System.err.println("time_units is missing from the phase. Please check recipe format")
            if (time_units == "") {
                time_units = phase.time_units
            } else if (time_units != phase.time_units) {
                System.err.println("time_units are not consistent across each phase in the recipe. {" + time_units + "} != {" + phase.time_units + "}")
            }
        }
        return time_units
    }

    /**
     * Given the time within a step, what value is expected.
     */
    @JvmStatic
    fun determine_value_for_step(variable_step_data: List<*>, duration_in_step: Long?): Map<String, Double> {
        var start_time = 0L
        var end_time = 0L
        var variable = ""
        var value = 0.0
        val ans: MutableMap<String, Double> = LinkedHashMap()
        for (j in variable_step_data.indices) {
            val item = variable_step_data[j]!!
            if (item.javaClass == Phases.Step.AirTemperature::class.java) {
                start_time = (item as Phases.Step.AirTemperature).start_time
                end_time = item.end_time
                variable = item.getName()
                value = item.value
            }
            if (item.javaClass == Phases.Step.LightIntensityBlue::class.java) {
                start_time = (item as Phases.Step.LightIntensityBlue).start_time
                end_time = item.end_time
                variable = item.getName()
                value = item.value
            }
            if (item.javaClass == Phases.Step.LightIntensityRed::class.java) {
                start_time = (item as Phases.Step.LightIntensityRed).start_time
                end_time = item.end_time
                variable = item.getName()
                value = item.value
            }
            if (item.javaClass == Phases.Step.LightIlluminance::class.java) {
                start_time = (item as Phases.Step.LightIlluminance).start_time
                end_time = item.end_time
                variable = item.getName()
                value = item.value
            }
            if (item.javaClass == Phases.Step.Nutrient_FloraDuoA::class.java) {
                start_time = (item as Phases.Step.Nutrient_FloraDuoA).start_time
                end_time = item.end_time
                variable = item.getName()
                value = item.value
            }
            if (item.javaClass == Phases.Step.Nutrient_FloraDuoB::class.java) {
                start_time = (item as Phases.Step.Nutrient_FloraDuoB).start_time
                end_time = item.end_time
                variable = item.getName()
                value = item.value
            }
            if (start_time <= duration_in_step!! && duration_in_step <= end_time) {
                println("variable: $variable, value: $value, start_time: $start_time, end_time: $end_time")
                ans[variable] = value
                return ans
            }
        }
        return ans
    }

    /**
     * Determines the total duration of this step. Normally it is 24 hours.
     */
    @JvmStatic
    fun calculate_max_duration_from_step(step: Phases.Step): Long {
        var max_time = 0L
        val allMethods = step.javaClass.methods

        val items = step.items()
        val size_of_step = items.size
        println("size_of_step: $size_of_step")
        var start_time = 0L
        var end_time = 0L
        for (i in 0 until size_of_step) {
            val size_of_item = items[i].size
            for (j in 0 until size_of_item) {
                val item = items[i][j]!!
                if (item.javaClass == Phases.Step.AirTemperature::class.java) {
                    start_time = (item as Phases.Step.AirTemperature).start_time
                    end_time = item.end_time
                }
                if (item.javaClass == Phases.Step.LightIntensityBlue::class.java) {
                    start_time = (item as Phases.Step.LightIntensityBlue).start_time
                    end_time = item.end_time
                }
                if (item.javaClass == Phases.Step.LightIntensityRed::class.java) {
                    start_time = (item as Phases.Step.LightIntensityRed).start_time
                    end_time = item.end_time
                }
                if (item.javaClass == Phases.Step.LightIntensityRed::class.java) {
                    start_time = (item as Phases.Step.LightIntensityRed).start_time
                    end_time = item.end_time
                }
                if (item.javaClass == Phases.Step.Nutrient_FloraDuoA::class.java) {
                    start_time = (item as Phases.Step.Nutrient_FloraDuoA).start_time
                    end_time = item.end_time
                }
                if (item.javaClass == Phases.Step.Nutrient_FloraDuoB::class.java) {
                    start_time = (item as Phases.Step.Nutrient_FloraDuoB).start_time
                    end_time = item.end_time
                }
                if (start_time > end_time) System.err.println("Start_time is after end time.") else if (max_time < Math.max(
                        start_time,
                        end_time
                    )
                ) max_time = Math.max(start_time, end_time)
            }
        }
        println("max_time = $max_time")
        return max_time
    }

    /**
     * Returns a list with the duration of the step and the entire phase.
     */
    @JvmStatic
    fun calc_duration_of_phases_steps(phases: List<Phases>): List<List<Long>> {
        val duration_of_phases_steps: MutableList<List<Long>> = ArrayList()
        for (phase in phases) {
            val cycles = phase.cycles
            val max_duration = phase.step.let { calculate_max_duration_from_step(it) }
            duration_of_phases_steps.add(java.util.List.of(cycles * max_duration, max_duration))
        }
        return duration_of_phases_steps
    }

    /**
     * Converts a number duration from Seconds into the units specified in the options(units variable).
     */
    @JvmStatic
    fun convert_duration_units(duration: Long, units: String): Long {
        val divider =
            java.util.Map.of(
                "hours", 3600.0,
                "days", 3600 * 24.0,
                "milliseconds", 0.001,
                "ms", 0.001,
                "seconds", 1.0
            )
        if (divider["units"] == null) System.err.println("Error time_units in recipe are not available. Valid options are: days, hours, milliseconds, ms")
        return (duration / divider[units]!!).toLong()
    }

    /**
     * Calculates how far along the recipe is in progress given the start_time and the time it is now (aka now_time).
     */
    @JvmStatic
    fun calc_phase_and_time_remaining(
        duration_of_phases_steps: List<List<Long>>,
        start_time: Long,
        now_time: Long,
        time_units: String
    ): Array<Long?> {
        var current_phase_number = 0L
        var duration_in_phase = 0L
        val ans = arrayOfNulls<Long>(2)
        var time_elapsed = now_time - start_time
        println("time_elapsed [in seconds]: $time_elapsed")
        time_elapsed = convert_duration_units(time_elapsed, time_units)
        println("time_elapsed [in $time_units]: $time_elapsed")
        for (i in duration_of_phases_steps.indices) {
            val total_duration = duration_of_phases_steps[i][0]
            val step_duration = duration_of_phases_steps[i][1]
            println("total_duration: $total_duration")
            println("step_duration: $step_duration")
            if (time_elapsed > total_duration) time_elapsed -= total_duration else {
                duration_in_phase = time_elapsed % step_duration
                current_phase_number = i.toLong()
                break
            }
        }
        ans[0] = current_phase_number
        ans[1] = duration_in_phase
        return ans
    }
}