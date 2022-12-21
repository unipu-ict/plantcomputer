package hr.unipu.recipe

import hr.unipu.ui.UiWindow
import java.util.LinkedHashMap
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import java.lang.InterruptedException
import hr.unipu.plantcomputer.RecipeDTO
import javafx.application.Platform
import javafx.util.Duration
import java.lang.Exception

/**
 * RecipeHandler is in charge of running recipes.
 * It provides a service `start_recipe` which takes as input a recipe ID and
 * starts the recipe. It also provides a service `stop_recipe`
 * which takes no inputs and stops the currently running recipe. It defines a
 * parameter `current_recipe` which stores the ID of the currently running recipe.
 */
object RecipeHandler {

    @JvmField
    var ENVIRONMENTAL_VARIABLES = VariableInfo.environment_variables
    @JvmField
    var RECIPE_VARIABLES = VariableInfo.recipe_variables
    var VALID_VARIABLES = ENVIRONMENTAL_VARIABLES

    var RECIPE_START = RECIPE_VARIABLES!![0].name
    var RECIPE_END = RECIPE_VARIABLES!![1].name

    var PUBLISHERS: Map<*, *> = LinkedHashMap<Any?, Any?>()
    var RECIPE_INTERPRETERS = java.util.Map.of(
        "simple", "interpret_simple_recipe",
        "flexformat", "interpret_flexformat_recipe"
    )
    private lateinit var recipe: RecipeDTO
    lateinit var setpoints: Map<String?, Double?>
    private var now_time: Long = 0
    private var start_time: Long = 0

    /**
     * Read recipe.
     */
    @JvmStatic
    fun get_recipe(recipeName: String) {
        recipe = RecipeInterpreter.loadFlexFormatRecipe(recipeName)!!
    }

    /**
     * Get the state-related variables of the currently running recipe.
     */
    @JvmStatic
    fun get_state() {
        val currentTime = Calendar.getInstance()
        val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm")
        dateFormatter.timeZone = TimeZone.getTimeZone("Europe/Berlin")
        val now_time_date = currentTime.time

        now_time = RecipeInterpreter.unix_time_seconds(now_time_date)

        // If start_time is not defined from previous recipe.
        if (start_time == 0L) {
            start_time = now_time - 1 // Test for recipe in process at first change (1 secs in)
        }
    }

    /**
     * Run recpie. Main entry point.
     * Starts up the node and then waits forever.
     *
     * @param recipeName
     */
    @JvmStatic
    fun run_recipe(recipeName: String) {

        // Initialization of: timezone; static variables date_min, date_max;
        RecipeInterpreter.init()

        // Set rate for WHILE LOOP:
        val samplingRate = TimeUnit.SECONDS
        val timeToSleep = 10L // Sampling rate for recipe state update.


        // WHILE LOOP:
        get_state()
        get_recipe(recipeName)

        // - If we have a recipe, process it. Running a recipe is a blocking
        //   operation, so the recipe will stay in this turn of the loop
        //   until it is finished.
        // - Interpret recipe.
        // - Get recipe state and publish it.

        println("start_time = " + start_time + " now_time = " + now_time)
        setpoints = RecipeInterpreter.interpret_flexformat_recipe(recipe, start_time, now_time) as Map<String?, Double?>

        // Calculate recipe duration:
        val phases = recipe.phases
        val duration_of_phases_steps = RecipeInterpreter.calc_duration_of_phases_steps(phases)
        println("duration_of_phases_steps: $duration_of_phases_steps")
        var recipe_duration = 0L
        for (duration_of_phases_step in duration_of_phases_steps) {
            recipe_duration += duration_of_phases_step[0]
        }
        println("Recipe duration: $recipe_duration")
        val countDownDuration = Duration.hours(recipe_duration.toDouble())
        Platform.runLater {
            // Update UI thread from here (if needed).
            UiWindow.init(countDownDuration)
            UiWindow.countdownTile.isVisible = true
            UiWindow.timer2.start()
        }


        // WHILE LOOP.
        val arduinoHandler = ArduinoHandler()
        while (UiWindow.isRecipeSelected) {
            println("In th WHILE LOOP")

            // FOR LOOP: publish any setpoints that we can.
            for (entry in setpoints.entries) {
                println(entry.key.toString() + " : " + entry.value)

                //Send desired setpoints to ArduinoHandler
                try {
                    arduinoHandler.set_desired_setpoint(entry)
                } catch (exception: Exception) {
                    println(exception.message)
                }
            }

            // Sleep in WHILE LOOP (before new time step).
            try {
                println(
                    "Going to sleep for "
                            + timeToSleep
                            + " seconds"
                )
                samplingRate.sleep(timeToSleep)
                println(
                    "Slept for "
                            + timeToSleep
                            + " seconds"
                )
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        println("Out of WHILE LOOP")
    }
}