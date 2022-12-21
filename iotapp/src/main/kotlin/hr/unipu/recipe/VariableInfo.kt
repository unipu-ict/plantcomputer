package hr.unipu.recipe

import java.util.List

/**
 * Variables in recipes.
 */
object VariableInfo {
    // RECIPE VARIABLES
    private val recipe_start = RecipeVariable(
        "recipe_start",
        "Represents the start of a recipe",
        "String"
    )
    private val recipe_end = RecipeVariable(
        "recipe_end",
        "Represents the end of a recipe",
        "String"
    )
    @JvmField
    var recipe_variables = List.of(recipe_start, recipe_end)

    // ENVIRONMENT VARIABLES
    private val air_temperature = EnvironmentVariable(
        "air_temperature",
        "Temperature of the air in degrees Celcius",
        "degrees C",
        "Double"
    )
    private val air_humidity = EnvironmentVariable(
        "air_humidity",
        "A measure of the concentration of water in the air relative to the maximum concentration at the current temperature",
        "percent relative",
        "Double"
    )
    private val air_carbon_dioxide = EnvironmentVariable(
        "air_carbon_dioxide",
        "The amount of Carbon Dioxide in the air",
        "ppm",
        "Double"
    )
    private val air_oxygen = EnvironmentVariable(
        "air_oxygen",
        "Oxygen density in the air",
        "percent",
        "Double"
    )
    private val air_flush = EnvironmentVariable(
        "air_flush",
        "Air flush a specific volume",
        "CFM",
        "Double"
    )
    private val water_temperature = EnvironmentVariable(
        "water_temperature",
        "Temperature of the water in degrees Celcius",
        "degrees C",
        "Double"
    )
    private val water_potential_hydrogen = EnvironmentVariable(
        "water_potential_hydrogen",
        "Potential Hydrogen of the water",
        "pH",
        "Double"
    )
    private val water_electrical_conductivity = EnvironmentVariable(
        "water_electrical_conductivity",
        "Electrical conductivity of the water",
        "uS/cm",
        "Double"
    )
    private val water_oxidation_reduction_potential = EnvironmentVariable(
        "water_oxidation_reduction_potential",
        "Oxidation-reduction potential of the water",
        "mV",
        "Double"
    )
    private val water_dissolved_oxygen = EnvironmentVariable(
        "water_dissolved_oxygen",
        "A measure of the amount of oxygen in the water",
        "mg/L",
        "Double"
    )
    private val water_level_low = EnvironmentVariable(
        "water_level_low",
        "Is water below threshold?",
        "logical",
        "Double"
    )
    private val water_level_high = EnvironmentVariable(
        "water_level_high",
        "Is water above threshold?",
        "logical",
        "Double"
    )
    private val nutrient_flora_duo_a = EnvironmentVariable(
        "nutrient_flora_duo_a",
        "FloraDuo nutrient A volume",
        "mL/h",
        "Double"
    )
    private val nutrient_flora_duo_b = EnvironmentVariable(
        "nutrient_flora_duo_b",
        "FloraDuo nutrient B volume",
        "mL/h",
        "Double"
    )
    private val light_illuminance = EnvironmentVariable(
        "light_illuminance",
        "The intensity of light falling at the plants",
        "lux",
        "Double"
    )
    private val light_intensity_red = EnvironmentVariable(
        "light_intensity_red",
        "The intensity setting for light panel",
        "percent relative",
        "Double"
    )
    private val light_intensity_blue = EnvironmentVariable(
        "light_intensity_blue",
        "The intensity setting for light panel",
        "percent relative",
        "Double"
    )
    private val light_intensity_white = EnvironmentVariable(
        "light_intensity_white",
        "The intensity setting for light panel",
        "percent relative",
        "Double"
    )
    @JvmField
    var environment_variables = List.of(
        air_temperature,
        air_humidity,
        air_carbon_dioxide,
        air_oxygen,
        air_flush,
        water_temperature,
        water_potential_hydrogen,
        water_electrical_conductivity,
        water_oxidation_reduction_potential,
        water_dissolved_oxygen,
        water_level_low,
        water_level_high,
        nutrient_flora_duo_a,
        nutrient_flora_duo_b,
        light_illuminance,
        light_intensity_red,
        light_intensity_blue,
        light_intensity_white
    )

    // CAMERA VARIABLES
    private val aerial_image = CameraVariable(
        "aerial_image",
        "Image from above the tray looking down on the plants",
        "png"
    )
    private val frontal_image = CameraVariable(
        "frontal_image",
        "Image from in front of the tray looking towards the plants",
        "png"
    )
    var camera_variables = List.of(
        aerial_image,
        frontal_image
    )

    class RecipeVariable (
        var name: String, var description: String, var type: String
    )

    class EnvironmentVariable(
        var name: String, var description: String, var units: String, var type: String
    )

    class CameraVariable(
        var name: String, var description: String, var units: String
    )
}