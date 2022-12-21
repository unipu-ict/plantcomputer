package hr.unipu.recipe;

import java.util.List;

/**
 * Variables in recipes.
 */
public class VariableInfoJava {

    public static class RecipeVariable {
        private String name;
        private String description;
        private String type;

        // CONSTRUCTOR
        public RecipeVariable(String name, String description, String type) {
            this.name = name;
            this.description = description;
            this.type = type;
        }

        // GETTERS & SETTERS
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getDescription() {
            return description;
        }
        public void setDescription(String description) {
            this.description = description;
        }
        public String getType() {
            return type;
        }
        public void setType(String type) {
            this.type = type;
        }
    }
    public static class EnvironmentVariable {
        private String name;
        private String description;
        private String units;
        private String type;

        // CONSTRUCTOR
        public EnvironmentVariable(String name, String description, String units, String type) {
            this.name = name;
            this.description = description;
            this.units = units;
            this.type = type;
        }

        // GETTERS & SETTERS
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getDescription() {
            return description;
        }
        public void setDescription(String description) {
            this.description = description;
        }
        public String getUnits() { return units; }
        public void setUnits(String units) { this.units = units; }
        public String getType() {
            return type;
        }
        public void setType(String type) {
            this.type = type;
        }
    }
    public static class CameraVariable {
        private String name;
        private String description;
        private String units;

        // CONSTRUCTOR
        public CameraVariable(String name, String description, String units) {
            this.name = name;
            this.description = description;
            this.units = units;
        }

        // GETTERS & SETTERS
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getDescription() {
            return description;
        }
        public void setDescription(String description) {
            this.description = description;
        }
        public String getUnits() {
            return units;
        }
        public void setUnits(String type) {
            this.units = units;
        }
    }



    // RECIPE VARIABLES
    private static RecipeVariable recipe_start = new RecipeVariable(
            "recipe_start",
            "Represents the start of a recipe",
            "String"
    );
    private static RecipeVariable recipe_end = new RecipeVariable(
            "recipe_end",
            "Represents the end of a recipe",
            "String"
    );
    public static List<RecipeVariable> recipe_variables = List.of(recipe_start, recipe_end);


    // ENVIRONMENT VARIABLES
    private static EnvironmentVariable air_temperature = new EnvironmentVariable(
            "air_temperature",
            "Temperature of the air in degrees Celcius",
            "degrees C",
            "Double"
    );
    private static EnvironmentVariable air_humidity = new EnvironmentVariable(
            "air_humidity",
            "A measure of the concentration of water in the air relative to the maximum concentration at the current temperature",
            "percent relative",
            "Double"
    );
    private static EnvironmentVariable air_carbon_dioxide = new EnvironmentVariable(
            "air_carbon_dioxide",
            "The amount of Carbon Dioxide in the air",
            "ppm",
            "Double"
    );
    private static EnvironmentVariable air_oxygen = new EnvironmentVariable(
            "air_oxygen",
            "Oxygen density in the air",
            "percent",
            "Double"
    );
    private static EnvironmentVariable air_flush = new EnvironmentVariable(
            "air_flush",
            "Air flush a specific volume",
            "CFM",
            "Double"
    );
    private static EnvironmentVariable water_temperature = new EnvironmentVariable(
            "water_temperature",
            "Temperature of the water in degrees Celcius",
            "degrees C",
            "Double"
    );
    private static EnvironmentVariable water_potential_hydrogen = new EnvironmentVariable(
            "water_potential_hydrogen",
            "Potential Hydrogen of the water",
            "pH",
            "Double"
    );
    private static EnvironmentVariable water_electrical_conductivity = new EnvironmentVariable(
            "water_electrical_conductivity",
            "Electrical conductivity of the water",
            "uS/cm",
            "Double"
    );
    private static EnvironmentVariable water_oxidation_reduction_potential = new EnvironmentVariable(
            "water_oxidation_reduction_potential",
            "Oxidation-reduction potential of the water",
            "mV",
            "Double"
    );
    private static EnvironmentVariable water_dissolved_oxygen = new EnvironmentVariable(
            "water_dissolved_oxygen",
            "A measure of the amount of oxygen in the water",
            "mg/L",
            "Double"
    );
    private static EnvironmentVariable water_level_low = new EnvironmentVariable(
            "water_level_low",
            "Is water below threshold?",
            "logical",
            "Double"
    );
    private static EnvironmentVariable water_level_high = new EnvironmentVariable(
            "water_level_high",
            "Is water above threshold?",
            "logical",
            "Double"
    );
    private static EnvironmentVariable nutrient_flora_duo_a = new EnvironmentVariable(
            "nutrient_flora_duo_a",
            "FloraDuo nutrient A volume",
            "mL/h",
            "Double"
    );
    private static EnvironmentVariable nutrient_flora_duo_b = new EnvironmentVariable(
            "nutrient_flora_duo_b",
            "FloraDuo nutrient B volume",
            "mL/h",
            "Double"
    );
    private static EnvironmentVariable light_illuminance = new EnvironmentVariable(
            "light_illuminance",
            "The intensity of light falling at the plants",
            "lux",
            "Double"
    );
    private static EnvironmentVariable light_intensity_red = new EnvironmentVariable(
            "light_intensity_red",
            "The intensity setting for light panel",
            "percent relative",
            "Double"
    );
    private static EnvironmentVariable light_intensity_blue = new EnvironmentVariable(
            "light_intensity_blue",
            "The intensity setting for light panel",
            "percent relative",
            "Double"
    );
    private static EnvironmentVariable light_intensity_white = new EnvironmentVariable(
            "light_intensity_white",
            "The intensity setting for light panel",
            "percent relative",
            "Double"
    );


    public static List<EnvironmentVariable> environment_variables = List.of(
            air_temperature, air_humidity, air_carbon_dioxide, air_oxygen, air_flush,
            water_temperature, water_potential_hydrogen, water_electrical_conductivity, water_oxidation_reduction_potential, water_dissolved_oxygen,
            water_level_low, water_level_high,
            nutrient_flora_duo_a, nutrient_flora_duo_b,
            light_illuminance, light_intensity_red, light_intensity_blue, light_intensity_white
    );


    // CAMERA VARIABLES
    private static CameraVariable aerial_image = new CameraVariable(
      "aerial_image",
      "Image from above the tray looking down on the plants",
      "png"
    );
    private static CameraVariable frontal_image = new CameraVariable(
            "frontal_image",
            "Image from in front of the tray looking towards the plants",
            "png"
    );

    public static List<CameraVariable> camera_variables = List.of(
            aerial_image,
            frontal_image
    );

}
