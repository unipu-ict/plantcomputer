package hr.unipu.plantcomputer

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.ArrayList
import java.util.LinkedList

/**
 * Recipe Data-Transfer-Object (DTO)
 * - for mapping JSON recipes as Java objects.
 */
class RecipeDTO {
    lateinit var _id: String
    lateinit var author: String
    lateinit var certified_by: List<String>
    lateinit var date_created: String
    var downloads: Int = 0
    lateinit var format: String
    lateinit var optimization: List<String>
    var phases: MutableList<Phases> = ArrayList()

    @JsonIgnoreProperties(ignoreUnknown = true)
    class Phases {
        var cycles: Int = 0
        var name: String = ""
        lateinit var step: Step

        class Step {
            var air_temperature: MutableList<AirTemperature>? = ArrayList()

            @JsonIgnoreProperties(ignoreUnknown = true)
            class AirTemperature {
                var end_time: Long = 0L
                var start_time: Long = 0L
                var value: Double = 0.0

                fun getName(): String {
                    return "air_temperature"
                }
            }

            var light_intensity_blue: MutableList<LightIntensityBlue>? = ArrayList()

            @JsonIgnoreProperties(ignoreUnknown = true)
            class LightIntensityBlue {
                var end_time: Long = 0L
                var start_time: Long = 0L
                var value: Double = 0.0

                fun getName(): String {
                    return "light_intensity_blue"
                }
            }

            var light_intensity_red: MutableList<LightIntensityRed>? = ArrayList()

            @JsonIgnoreProperties(ignoreUnknown = true)
            class LightIntensityRed {
                var end_time: Long = 0L
                var start_time: Long = 0L
                var value: Double = 0.0

                fun getName(): String {
                    return "light_intensity_red"
                }
            }

            val light_intensity_white: MutableList<LightIntensityWhite>? = ArrayList()

            @JsonIgnoreProperties(ignoreUnknown = true)
            class LightIntensityWhite {
                var end_time: Long = 0L
                var start_time: Long = 0L
                var value: Double = 0.0

                fun getName(): String {
                    return "light_intensity_white"
                }
            }

            var light_illuminance: List<LightIlluminance>? = ArrayList()

            @JsonIgnoreProperties(ignoreUnknown = true)
            class LightIlluminance {
                var end_time: Long = 0L
                var start_time: Long = 0L
                var value: Double = 0.0

                fun getName(): String {
                    return "light_illuminance"
                }
            }

            var nutrient_flora_duo_a: MutableList<Nutrient_FloraDuoA>? = ArrayList()

            @JsonIgnoreProperties(ignoreUnknown = true)
            class Nutrient_FloraDuoA {
                var end_time: Long = 0L
                var start_time: Long = 0L
                var value: Double = 0.0

                fun getName(): String {
                    return "nutrient_flora_duo_a"
                }
            }

            var nutrient_flora_duo_b: MutableList<Nutrient_FloraDuoB>? = ArrayList()

            @JsonIgnoreProperties(ignoreUnknown = true)
            class Nutrient_FloraDuoB {
                var end_time: Long = 0L
                var start_time: Long = 0L
                var value: Double = 0.0

                fun getName(): String {
                    return "nutrient_flora_duo_b"
                }
            }

            fun items(): List<List<*>> {
                val items: MutableList<List<*>> = LinkedList()
                if (air_temperature != null) items.add(air_temperature!!)
                if (light_intensity_blue != null) items.add(light_intensity_blue!!)
                if (light_intensity_red != null) items.add(light_intensity_red!!)
                if (light_illuminance != null) items.add(light_illuminance!!)
                if (nutrient_flora_duo_a != null) items.add(nutrient_flora_duo_a!!)
                if (nutrient_flora_duo_b != null) items.add(nutrient_flora_duo_b!!)
                return items
            }
        }

        var time_units = "hours" // default hours
        lateinit var variable_units: VariableUnits

        class VariableUnits {
            var air_temperature: String = "°C"
            var light_illuminance: String = "lux"
            var nutrient_flora_duo_a: String = "mL/h"
            var nutrient_flora_duo_b: String = "mL/h"
        }
    }

    lateinit var operations: List<Operations>

    @JsonIgnoreProperties(ignoreUnknown = true)
    class Operations {
        lateinit var name: String
        var cycles: Int = 0
        lateinit var day: DayNight
        lateinit var night: DayNight

        class DayNight {
            var hours: Long = 0L
            var air_temperature: Double = 0.0
            var nutrient_flora_duo_a: Double = 0.0
            var nutrient_flora_duo_b: Double = 0.0
            var light_intensity_red: Double = 0.0
            var light_intensity_white: Double = 0.0

            var light_intensity_blue: Double = 0.0
        }
    }

    fun mapOperations2Phases() {
        var time_counter = 0L
        for (operation in operations) {
            val phase = Phases()
            phase.name = operation.name
            phase.cycles = operation.cycles
            val step = Phases.Step()
            phase.step = step

            // AirTemperature
            var air_temperature: Phases.Step.AirTemperature
            var nutrient_flora_duo_a: Phases.Step.Nutrient_FloraDuoA
            var nutrient_flora_duo_b: Phases.Step.Nutrient_FloraDuoB
            var light_intensity_red: Phases.Step.LightIntensityRed
            var light_intensity_blue: Phases.Step.LightIntensityBlue
            var light_intensity_white: Phases.Step.LightIntensityWhite
            air_temperature = Phases.Step.AirTemperature()
            nutrient_flora_duo_a = Phases.Step.Nutrient_FloraDuoA()
            nutrient_flora_duo_b = Phases.Step.Nutrient_FloraDuoB()
            light_intensity_red = Phases.Step.LightIntensityRed()
            light_intensity_blue = Phases.Step.LightIntensityBlue()
            light_intensity_white = Phases.Step.LightIntensityWhite()

            // day
            air_temperature.start_time = time_counter
            nutrient_flora_duo_a.start_time = time_counter
            nutrient_flora_duo_b.start_time = time_counter
            light_intensity_red.start_time = time_counter
            light_intensity_blue.start_time = time_counter
            light_intensity_white.start_time = time_counter
            time_counter = time_counter + operation.day.hours
            air_temperature.end_time = time_counter
            nutrient_flora_duo_a.end_time = time_counter
            nutrient_flora_duo_b.end_time = time_counter
            light_intensity_red.end_time = time_counter
            light_intensity_blue.end_time = time_counter
            light_intensity_white.end_time = time_counter
            air_temperature.value = operation.day.air_temperature
            nutrient_flora_duo_a.value = operation.day.nutrient_flora_duo_a
            nutrient_flora_duo_b.value = operation.day.nutrient_flora_duo_b
            light_intensity_red.value = operation.day.light_intensity_red
            light_intensity_blue.value = operation.day.light_intensity_blue
            light_intensity_white.value = operation.day.light_intensity_white
            step.air_temperature!!.add(air_temperature)
            step.nutrient_flora_duo_a!!.add(nutrient_flora_duo_a)
            step.nutrient_flora_duo_b!!.add(nutrient_flora_duo_b)
            step.light_intensity_red!!.add(light_intensity_red)
            step.light_intensity_blue!!.add(light_intensity_blue)
            step.light_intensity_white!!.add(light_intensity_white)


            // AirTemperature (clear);
            air_temperature = Phases.Step.AirTemperature()
            nutrient_flora_duo_a = Phases.Step.Nutrient_FloraDuoA()
            nutrient_flora_duo_b = Phases.Step.Nutrient_FloraDuoB()
            light_intensity_red = Phases.Step.LightIntensityRed()
            light_intensity_blue = Phases.Step.LightIntensityBlue()
            light_intensity_white = Phases.Step.LightIntensityWhite()

            // night
            air_temperature.start_time = time_counter
            nutrient_flora_duo_a.start_time = time_counter
            nutrient_flora_duo_b.start_time = time_counter
            light_intensity_red.start_time = time_counter
            light_intensity_blue.start_time = time_counter
            light_intensity_white.start_time = time_counter
            time_counter = time_counter + operation.night.hours
            air_temperature.end_time = time_counter
            nutrient_flora_duo_a.end_time = time_counter
            nutrient_flora_duo_b.end_time = time_counter
            light_intensity_red.end_time = time_counter
            light_intensity_blue.end_time = time_counter
            light_intensity_white.end_time = time_counter
            air_temperature.value = operation.night.air_temperature
            nutrient_flora_duo_a.value = operation.night.nutrient_flora_duo_a
            nutrient_flora_duo_b.value = operation.night.nutrient_flora_duo_b
            light_intensity_red.value = operation.night.light_intensity_red
            light_intensity_blue.value = operation.night.light_intensity_blue
            light_intensity_white.value = operation.night.light_intensity_white
            step.air_temperature!!.add(air_temperature)
            step.nutrient_flora_duo_a!!.add(nutrient_flora_duo_a)
            step.nutrient_flora_duo_b!!.add(nutrient_flora_duo_b)
            step.light_intensity_red!!.add(light_intensity_red)
            step.light_intensity_blue!!.add(light_intensity_blue)
            step.light_intensity_white!!.add(light_intensity_white)
            phases.add(phase)
        }
    }

    lateinit var plant_type: List<String>
    lateinit var rating: String
    lateinit var seeds: List<String>
    lateinit var version: String

}