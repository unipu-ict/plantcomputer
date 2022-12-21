package hr.unipu.recipe;

import hr.unipu.plantcomputer.PlantComputerActionJava;
import hr.unipu.plantcomputer.PlantComputerCommandJava;
import hr.unipu.ui.UiWindowJava;

import java.util.Map;

/**
 * ArduinoHandler handles received recipe's desired setpoints with PID regulators
 * and sends actuator commands to Arduino.
 */
public class ArduinoHandlerJava {
    // air_temperature_controller_1
    private ControllerPIDJava pidTemperature = new ControllerPIDJava(1.0, 0.0, 1.0,
            1.0, -1.0, 1000.0, 0.5, "air_temperature");

    // air_humidity_controller_1
    private ControllerPIDJava pidHumidity = new ControllerPIDJava(1.0, 0.0, 0.0,
            0.5, 1000.0, 1.0, 0.0, "air_humidity");

    // water_potential_hydrogen_controller_1
    private ControllerPIDJava pidPh = new ControllerPIDJava(0.25, 0.25, 0.75,
            1.0, -1.0, 1000.0, 0.5, "water_potential_hydrogen");

    // light_controller_red_1
    private ControllerDirectJava controllerDirectLightRed = new ControllerDirectJava("light_intensity_red");
    // light_controller_blue_1
    private ControllerDirectJava controllerDirectLightBlue = new ControllerDirectJava("light_intensity_blue");
    // light_controller_white_1
    private ControllerDirectJava controllerDirectLightWhite = new ControllerDirectJava("light_intensity_white");

    // nutrient_flora_duo_a_controller_1
    private ControllerDirectJava controllerDirectNutrientFloraDuoA = new ControllerDirectJava("nutrient_flora_duo_a");
    // nutrient_flora_duo_b_controller_1
    private ControllerDirectJava controllerDirectNutrientFloraDuoB = new ControllerDirectJava("nutrient_flora_duo_b");
    // air_flush_controller_1
    private ControllerDirectJava controllerDirectAirFlush = new ControllerDirectJava("air_flush");

    // water_level_high_controller_1
    private ControllerLinearJava controllerLinearWaterLevelHigh = new ControllerLinearJava("water_level_high");


    // Set desired setpoints (with PID regulator).
    public void set_desired_setpoint(Map.Entry<String, Double> entry) {
        String variableName = entry.getKey();
        Double variableDesired = entry.getValue();
        Double variableMeasured = null;
        Double variableCommanded = null;
        switch (variableName) {
            case "air_temperature":
                pidTemperature.set_point_callback(variableDesired);
                variableMeasured = this.get_measured_variable(variableName);
                variableCommanded = pidTemperature.update(variableMeasured);
                break;
            case "water_potential_hydrogen":
                pidPh.set_point_callback(variableDesired);
                variableMeasured = this.get_measured_variable(variableName);
                variableCommanded = pidPh.update(variableMeasured);
                break;
            case "air_humidity":
                pidPh.set_point_callback(variableDesired);
                variableMeasured = this.get_measured_variable(variableName);
                variableCommanded = pidHumidity.update(variableMeasured);
                break;
            case "light_intensity_red":
                controllerDirectLightRed.set_point_callback(variableDesired);
                variableMeasured = this.get_measured_variable(variableName);
                variableCommanded = controllerDirectLightRed.update(variableMeasured);
                break;
            case "light_intensity_blue":
                controllerDirectLightBlue.set_point_callback(variableDesired);
                variableMeasured = this.get_measured_variable(variableName);
                variableCommanded = controllerDirectLightBlue.update(variableMeasured);
                break;
            case "light_intensity_white":
                controllerDirectLightWhite.set_point_callback(variableDesired);
                variableMeasured = this.get_measured_variable(variableName);
                variableCommanded = controllerDirectLightWhite.update(variableMeasured);
                break;
            case "nutrient_flora_duo_a":
                controllerDirectNutrientFloraDuoA.set_point_callback(variableDesired);
                variableMeasured = this.get_measured_variable(variableName);
                variableCommanded = controllerDirectNutrientFloraDuoA.update(variableMeasured);
                break;
            case "nutrient_flora_duo_b":
                controllerDirectNutrientFloraDuoB.set_point_callback(variableDesired);
                variableMeasured = this.get_measured_variable(variableName);
                variableCommanded = controllerDirectNutrientFloraDuoB.update(variableMeasured);
                break;
            case "air_flush":
                controllerDirectAirFlush.set_point_callback(variableDesired);
                variableMeasured = this.get_measured_variable(variableName);
                variableCommanded = controllerDirectAirFlush.update(variableMeasured);
                break;
            case "water_level_high":
                controllerLinearWaterLevelHigh.set_point_callback(variableDesired);
                variableMeasured = this.get_measured_variable(variableName);
                variableCommanded = controllerLinearWaterLevelHigh.update(variableMeasured);
                break;
            default:
                // code block
        }
        this.send_commanded_variable(variableName, variableCommanded);

    }

    // Send setpoints to Arduino and setpoints' marks into UI.
    private void send_commanded_variable(String variableName, Double variableCommanded) {
        switch (variableName) {
            case "air_temperature":
                this.air_temperature_callback(variableCommanded);
                break;
            case "water_potential_hydrogen":
                this.water_potential_hydrogen_callback(variableCommanded);
                break;
            case "air_humidity":
                this.air_humidity_callback(variableCommanded);
                break;
            case "light_intensity_red":
                this.light_intensity_red_callback(variableCommanded);
                break;
            case "light_intensity_blue":
                this.light_intensity_blue_callback(variableCommanded);
                break;
            case "light_intensity_white":
                this.light_intensity_white_callback(variableCommanded);
                break;
            case "nutrient_flora_duo_a":
                this.nutrient_flora_duo_a_callback(variableCommanded);
                break;
            case "nutrient_flora_duo_b":
                this.nutrient_flora_duo_b_callback(variableCommanded);
                break;
            case "air_flush":
                this.air_flush_callback(variableCommanded);
                break;
            case "water_level_high":
                this.water_level_high_callback(variableCommanded);
                break;
            default:
                // code block
        }
        UiWindowJava.sendMessageForActuators();
    }


    // Get measurements for comparison with recipe.
    private Double get_measured_variable(String variableName) {
        Double variableMeasured = null;
        switch (variableName) {
            case "air_temperature":
                variableMeasured = UiWindowJava.value1.getValue();  // SATM 1
                break;
            case "water_potential_hydrogen":
                variableMeasured = UiWindowJava.value4.getValue();  // SWPH 1
                break;
            case "air_humidity":
                variableMeasured = UiWindowJava.value2.getValue();  // SAHU 1
                break;
            case "light_intensity_red":
                variableMeasured = UiWindowJava.value6.getValue();  // SLIN 1, red
                break;
            case "light_intensity_blue":
                variableMeasured = UiWindowJava.value6.getValue();  // SLIN 1, blue
                break;
            case "light_intensity_white":
                variableMeasured = UiWindowJava.value6.getValue();  // SLIN 1, white
                break;
            case "nutrient_flora_duo_a":
                variableMeasured = UiWindowJava.value3.getValue();  // SWEC 1
                break;
            case "nutrient_flora_duo_b":
                variableMeasured = UiWindowJava.value3.getValue();  // SWEC 1
                break;
            case "air_flush":
                // Default state:
                variableMeasured = Double.parseDouble(PlantComputerActionJava.AIR_CO_STATE.getActionValue());
                break;
            case "water_level_high":
                // Default state:
                variableMeasured = Double.parseDouble(PlantComputerActionJava.WATER_LEVEL_HIGH_STATE.getActionValue());
                break;
            default:
                // code block
        }
        return variableMeasured;
    }


    // Set air_temperature_callback.
    private void air_temperature_callback(Double variableCommanded) {
        if (variableCommanded > 0.0) {
            UiWindowJava.listActuatorCommands.clear();
            UiWindowJava.listActuatorCommands.add(new PlantComputerCommandJava(PlantComputerActionJava.HEATER_TURN_ON));
            UiWindowJava.listActuatorCommands.add(new PlantComputerCommandJava(PlantComputerActionJava.CHAMBER_FAN_TURN_ON)); //always

        }
        if (variableCommanded < 0.0) {
            UiWindowJava.listActuatorCommands.clear();
            UiWindowJava.listActuatorCommands.add(new PlantComputerCommandJava(PlantComputerActionJava.HEATER_TURN_OFF));
            UiWindowJava.listActuatorCommands.add(new PlantComputerCommandJava(PlantComputerActionJava.CHILLER_FAN_TURN_ON));
            UiWindowJava.listActuatorCommands.add(new PlantComputerCommandJava(PlantComputerActionJava.WATER_PUMP_TURN_ON));
            UiWindowJava.listActuatorCommands.add(new PlantComputerCommandJava(PlantComputerActionJava.CHAMBER_FAN_TURN_ON));

        }
    }


    // Set water_potential_hydrogen_callback.
    private void water_potential_hydrogen_callback(Double variableCommanded) {
        if (variableCommanded > 0.0) {
            UiWindowJava.listActuatorCommands.clear();
            UiWindowJava.listActuatorCommands.add(new PlantComputerCommandJava(PlantComputerActionJava.PUMP_PH_UP_ON));
            UiWindowJava.listActuatorCommands.add(new PlantComputerCommandJava(PlantComputerActionJava.PUMP_PH_DOWN_OFF));

        }
        if (variableCommanded < 0.0) {
            UiWindowJava.listActuatorCommands.clear();
            UiWindowJava.listActuatorCommands.add(new PlantComputerCommandJava(PlantComputerActionJava.PUMP_PH_DOWN_ON));
            UiWindowJava.listActuatorCommands.add(new PlantComputerCommandJava(PlantComputerActionJava.PUMP_PH_UP_OFF));

        }
    }


    // Set air_humidity_callback.
    private void air_humidity_callback(Double variableCommanded) {
        if (variableCommanded > 0.0) {
            UiWindowJava.listActuatorCommands.clear();
            UiWindowJava.listActuatorCommands.add(new PlantComputerCommandJava(PlantComputerActionJava.HUMIDIFIER_TURN_ON));
        }
        if (variableCommanded < 0.0) {
            UiWindowJava.listActuatorCommands.clear();
            UiWindowJava.listActuatorCommands.add(new PlantComputerCommandJava(PlantComputerActionJava.HUMIDIFIER_TURN_OFF));
        }
    }


    // Set nutrient_flora_duo_a_callback.
    private void nutrient_flora_duo_a_callback(Double variableCommanded) {
        if (variableCommanded > 0.0) {
            UiWindowJava.listActuatorCommands.clear();
            UiWindowJava.listActuatorCommands.add(new PlantComputerCommandJava(PlantComputerActionJava.PUMP_NUTRIENT_A_ON));
            UiWindowJava.listActuatorCommands.add(new PlantComputerCommandJava(PlantComputerActionJava.PUMP_NUTRIENT_B_OFF));

        }
        if (variableCommanded < 0.0) {
            UiWindowJava.listActuatorCommands.clear();
            UiWindowJava.listActuatorCommands.add(new PlantComputerCommandJava(PlantComputerActionJava.PUMP_NUTRIENT_A_OFF));
        }
    }


    // Set nutrient_flora_duo_b_callback.
    private void nutrient_flora_duo_b_callback(Double variableCommanded) {
        if (variableCommanded > 0.0) {
            UiWindowJava.listActuatorCommands.clear();
            UiWindowJava.listActuatorCommands.add(new PlantComputerCommandJava(PlantComputerActionJava.PUMP_NUTRIENT_B_ON));
            UiWindowJava.listActuatorCommands.add(new PlantComputerCommandJava(PlantComputerActionJava.PUMP_NUTRIENT_A_OFF));

        }
        if (variableCommanded < 0.0) {
            UiWindowJava.listActuatorCommands.clear();
            UiWindowJava.listActuatorCommands.add(new PlantComputerCommandJava(PlantComputerActionJava.PUMP_NUTRIENT_B_OFF));
        }
    }


    // Set air_flush_callback.
    private void air_flush_callback(Double variableCommanded) {
        if (variableCommanded > 0.0) {
            UiWindowJava.listActuatorCommands.clear();
            UiWindowJava.listActuatorCommands.add(new PlantComputerCommandJava(PlantComputerActionJava.AIR_FLUSH_TURN_ON));
        }
        if (variableCommanded < 0.0) {
            UiWindowJava.listActuatorCommands.clear();
            UiWindowJava.listActuatorCommands.add(new PlantComputerCommandJava(PlantComputerActionJava.AIR_FLUSH_TURN_OFF));
        }
    }


    // Set light_intensity_red_callback.
    private void light_intensity_red_callback(Double variableCommanded) {
        if (variableCommanded > 0.0) {
            UiWindowJava.listActuatorCommands.clear();
            //UiWindow.listActuatorCommands.add(new FoodComputerCommand(FoodComputerAction.LIGHT_TURN_ON));
            UiWindowJava.listActuatorCommands.add(new PlantComputerCommandJava(PlantComputerActionJava.LIGHT_RED_TURN_ON));
            UiWindowJava.listActuatorCommands.add(new PlantComputerCommandJava(PlantComputerActionJava.LIGHT_BLUE_TURN_OFF));
            UiWindowJava.listActuatorCommands.add(new PlantComputerCommandJava(PlantComputerActionJava.LIGHT_WHITE_TURN_OFF));

        }
        if (variableCommanded < 0.0) {
            UiWindowJava.listActuatorCommands.clear();
            //UiWindow.listActuatorCommands.add(new FoodComputerCommand(FoodComputerAction.LIGHT_TURN_OFF));
            UiWindowJava.listActuatorCommands.add(new PlantComputerCommandJava(PlantComputerActionJava.LIGHT_RED_TURN_OFF));
        }
    }


    // Set light_intensity_blue_callback.
    private void light_intensity_blue_callback(Double variableCommanded) {
        if (variableCommanded > 0.0) {
            UiWindowJava.listActuatorCommands.clear();
            //UiWindow.listActuatorCommands.add(new FoodComputerCommand(FoodComputerAction.LIGHT_TURN_ON));
            UiWindowJava.listActuatorCommands.add(new PlantComputerCommandJava(PlantComputerActionJava.LIGHT_BLUE_TURN_ON));
            UiWindowJava.listActuatorCommands.add(new PlantComputerCommandJava(PlantComputerActionJava.LIGHT_RED_TURN_OFF));
            UiWindowJava.listActuatorCommands.add(new PlantComputerCommandJava(PlantComputerActionJava.LIGHT_WHITE_TURN_OFF));

        }
        if (variableCommanded < 0.0) {
            UiWindowJava.listActuatorCommands.clear();
            //UiWindow.listActuatorCommands.add(new FoodComputerCommand(FoodComputerAction.LIGHT_TURN_OFF));
            UiWindowJava.listActuatorCommands.add(new PlantComputerCommandJava(PlantComputerActionJava.LIGHT_BLUE_TURN_OFF));
        }
    }


    // Set light_intensity_white_callback.
    private void light_intensity_white_callback(Double variableCommanded) {
        if (variableCommanded > 0.0) {
            UiWindowJava.listActuatorCommands.clear();
            //UiWindow.listActuatorCommands.add(new FoodComputerCommand(FoodComputerAction.LIGHT_TURN_ON));
            UiWindowJava.listActuatorCommands.add(new PlantComputerCommandJava(PlantComputerActionJava.LIGHT_WHITE_TURN_ON));
            UiWindowJava.listActuatorCommands.add(new PlantComputerCommandJava(PlantComputerActionJava.LIGHT_RED_TURN_OFF));
            UiWindowJava.listActuatorCommands.add(new PlantComputerCommandJava(PlantComputerActionJava.LIGHT_BLUE_TURN_OFF));

        }
        if (variableCommanded < 0.0) {
            UiWindowJava.listActuatorCommands.clear();
            //UiWindow.listActuatorCommands.add(new FoodComputerCommand(FoodComputerAction.LIGHT_TURN_OFF));
            UiWindowJava.listActuatorCommands.add(new PlantComputerCommandJava(PlantComputerActionJava.LIGHT_WHITE_TURN_OFF));
        }
    }


    // Set water_level_high_callback.
    private void water_level_high_callback(Double variableCommanded) {
        if (variableCommanded > 0.0) {
            UiWindowJava.listActuatorCommands.clear();
            UiWindowJava.listActuatorCommands.add(new PlantComputerCommandJava(PlantComputerActionJava.WATER_PUMP_TURN_ON));

        }
        if (variableCommanded < 0.0) {
            UiWindowJava.listActuatorCommands.clear();
            UiWindowJava.listActuatorCommands.add(new PlantComputerCommandJava(PlantComputerActionJava.WATER_PUMP_TURN_OFF));
        }
    }

}
