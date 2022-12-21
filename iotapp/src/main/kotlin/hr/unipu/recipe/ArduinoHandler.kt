package hr.unipu.recipe

import hr.unipu.plantcomputer.PlantComputerAction
import hr.unipu.plantcomputer.PlantComputerCommand
import hr.unipu.ui.UiWindow

/**
 * ArduinoHandler handles received recipe's desired setpoints with PID regulators
 * and sends actuator commands to Arduino.
 */
class ArduinoHandler {
    // air_temperature_controller_1
    private val pidTemperature = ControllerPID(
        1.0, 0.0, 1.0,
        1.0, -1.0, 1000.0, 0.5, "air_temperature"
    )

    // air_humidity_controller_1
    private val pidHumidity = ControllerPID(
        1.0, 0.0, 0.0,
        0.5, 1000.0, 1.0, 0.0, "air_humidity"
    )

    // water_potential_hydrogen_controller_1
    private val pidPh = ControllerPID(
        0.25, 0.25, 0.75,
        1.0, -1.0, 1000.0, 0.5, "water_potential_hydrogen"
    )

    // light_controller_red_1
    private val controllerDirectLightRed = ControllerDirect("light_intensity_red")

    // light_controller_blue_1
    private val controllerDirectLightBlue = ControllerDirect("light_intensity_blue")

    // light_controller_white_1
    private val controllerDirectLightWhite = ControllerDirect("light_intensity_white")

    // nutrient_flora_duo_a_controller_1
    private val controllerDirectNutrientFloraDuoA = ControllerDirect("nutrient_flora_duo_a")

    // nutrient_flora_duo_b_controller_1
    private val controllerDirectNutrientFloraDuoB = ControllerDirect("nutrient_flora_duo_b")

    // air_flush_controller_1
    private val controllerDirectAirFlush = ControllerDirect("air_flush")

    // water_level_high_controller_1
    private val controllerLinearWaterLevelHigh = ControllerLinear("water_level_high")

    // Set desired setpoints (with PID regulator).
    fun set_desired_setpoint(entry: Map.Entry<String?, Double?>) {
        val variableName = entry.key.toString()
        val variableDesired: Double = entry.value?.toDouble()!!
        var variableMeasured: Double = 0.0
        var variableCommanded: Double = 0.0
        when (variableName) {
            "air_temperature" -> {
                pidTemperature.set_point_callback(variableDesired)
                variableMeasured = get_measured_variable(variableName)
                variableCommanded = pidTemperature.update(variableMeasured)
            }
            "water_potential_hydrogen" -> {
                pidPh.set_point_callback(variableDesired)
                variableMeasured = get_measured_variable(variableName)
                variableCommanded = pidPh.update(variableMeasured)
            }
            "air_humidity" -> {
                pidPh.set_point_callback(variableDesired)
                variableMeasured = get_measured_variable(variableName)
                variableCommanded = pidHumidity.update(variableMeasured)
            }
            "light_intensity_red" -> {
                controllerDirectLightRed.set_point_callback(variableDesired)
                variableMeasured = get_measured_variable(variableName)
                variableCommanded = controllerDirectLightRed.update(variableMeasured)
            }
            "light_intensity_blue" -> {
                controllerDirectLightBlue.set_point_callback(variableDesired)
                variableMeasured = get_measured_variable(variableName)
                variableCommanded = controllerDirectLightBlue.update(variableMeasured)
            }
            "light_intensity_white" -> {
                controllerDirectLightWhite.set_point_callback(variableDesired)
                variableMeasured = get_measured_variable(variableName)
                variableCommanded = controllerDirectLightWhite.update(variableMeasured)
            }
            "nutrient_flora_duo_a" -> {
                controllerDirectNutrientFloraDuoA.set_point_callback(variableDesired)
                variableMeasured = get_measured_variable(variableName)
                variableCommanded = controllerDirectNutrientFloraDuoA.update(variableMeasured)
            }
            "nutrient_flora_duo_b" -> {
                controllerDirectNutrientFloraDuoB.set_point_callback(variableDesired)
                variableMeasured = get_measured_variable(variableName)
                variableCommanded = controllerDirectNutrientFloraDuoB.update(variableMeasured)
            }
            "air_flush" -> {
                controllerDirectAirFlush.set_point_callback(variableDesired)
                variableMeasured = get_measured_variable(variableName)
                variableCommanded = controllerDirectAirFlush.update(variableMeasured)
            }
            "water_level_high" -> {
                controllerLinearWaterLevelHigh.set_point_callback(variableDesired)
                variableMeasured = get_measured_variable(variableName)
                variableCommanded = controllerLinearWaterLevelHigh.update(variableMeasured)
            }
            else -> {
            }
        }
        send_commanded_variable(variableName, variableCommanded)
    }

    // Send setpoints to Arduino and setpoints' marks into UI.
    private fun send_commanded_variable(variableName: String, variableCommanded: Double) {
        when (variableName) {
            "air_temperature" -> air_temperature_callback(variableCommanded)
            "water_potential_hydrogen" -> water_potential_hydrogen_callback(variableCommanded)
            "air_humidity" -> air_humidity_callback(variableCommanded)
            "light_intensity_red" -> light_intensity_red_callback(variableCommanded)
            "light_intensity_blue" -> light_intensity_blue_callback(variableCommanded)
            "light_intensity_white" -> light_intensity_white_callback(variableCommanded)
            "nutrient_flora_duo_a" -> nutrient_flora_duo_a_callback(variableCommanded)
            "nutrient_flora_duo_b" -> nutrient_flora_duo_b_callback(variableCommanded)
            "air_flush" -> air_flush_callback(variableCommanded)
            "water_level_high" -> water_level_high_callback(variableCommanded)
            else -> {
            }
        }
        UiWindow.sendMessageForActuators()
    }

    // Get measurements for comparison with recipe.
    private fun get_measured_variable(variableName: String): Double {
        var variableMeasured: Double = 0.0
        when (variableName) {
            "air_temperature" -> variableMeasured = UiWindow.value1.value           // SATM 1
            "water_potential_hydrogen" -> variableMeasured = UiWindow.value4.value  // SWPH 1
            "air_humidity" -> variableMeasured = UiWindow.value2.value              // SAHU 1
            "light_intensity_red" -> variableMeasured = UiWindow.value6.value       // SLIN 1, red
            "light_intensity_blue" -> variableMeasured = UiWindow.value6.value      // SLIN 1, blue
            "light_intensity_white" -> variableMeasured = UiWindow.value6.value     // SLIN 1, white
            "nutrient_flora_duo_a" -> variableMeasured = UiWindow.value3.value      // SWEC 1
            "nutrient_flora_duo_b" -> variableMeasured = UiWindow.value3.value      // SWEC 1
            "air_flush" -> variableMeasured = PlantComputerAction.AIR_CO_STATE.actionValue.toDouble() // Default state.
            "water_level_high" -> variableMeasured = PlantComputerAction.WATER_LEVEL_HIGH_STATE.actionValue.toDouble() // Default state.
            else -> {
            }
        }
        return variableMeasured
    }

    // Set air_temperature_callback.
    private fun air_temperature_callback(variableCommanded: Double) {
        if (variableCommanded > 0.0) {
            UiWindow.listActuatorCommands.clear()
            UiWindow.listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.HEATER_TURN_ON))
            UiWindow.listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.CHAMBER_FAN_TURN_ON)) //always
        }
        if (variableCommanded < 0.0) {
            UiWindow.listActuatorCommands.clear()
            UiWindow.listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.HEATER_TURN_OFF))
            UiWindow.listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.CHILLER_FAN_TURN_ON))
            UiWindow.listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.WATER_PUMP_TURN_ON))
            UiWindow.listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.CHAMBER_FAN_TURN_ON))
        }
    }

    // Set water_potential_hydrogen_callback.
    private fun water_potential_hydrogen_callback(variableCommanded: Double) {
        if (variableCommanded > 0.0) {
            UiWindow.listActuatorCommands.clear()
            UiWindow.listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.PUMP_PH_UP_ON))
            UiWindow.listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.PUMP_PH_DOWN_OFF))
        }
        if (variableCommanded < 0.0) {
            UiWindow.listActuatorCommands.clear()
            UiWindow.listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.PUMP_PH_DOWN_ON))
            UiWindow.listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.PUMP_PH_UP_OFF))
        }
    }

    // Set air_humidity_callback.
    private fun air_humidity_callback(variableCommanded: Double) {
        if (variableCommanded > 0.0) {
            UiWindow.listActuatorCommands.clear()
            UiWindow.listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.HUMIDIFIER_TURN_ON))
        }
        if (variableCommanded < 0.0) {
            UiWindow.listActuatorCommands.clear()
            UiWindow.listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.HUMIDIFIER_TURN_OFF))
        }
    }

    // Set nutrient_flora_duo_a_callback.
    private fun nutrient_flora_duo_a_callback(variableCommanded: Double) {
        if (variableCommanded > 0.0) {
            UiWindow.listActuatorCommands.clear()
            UiWindow.listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.PUMP_NUTRIENT_A_ON))
            UiWindow.listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.PUMP_NUTRIENT_B_OFF))
        }
        if (variableCommanded < 0.0) {
            UiWindow.listActuatorCommands.clear()
            UiWindow.listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.PUMP_NUTRIENT_A_OFF))
        }
    }

    // Set nutrient_flora_duo_b_callback.
    private fun nutrient_flora_duo_b_callback(variableCommanded: Double) {
        if (variableCommanded > 0.0) {
            UiWindow.listActuatorCommands.clear()
            UiWindow.listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.PUMP_NUTRIENT_B_ON))
            UiWindow.listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.PUMP_NUTRIENT_A_OFF))
        }
        if (variableCommanded < 0.0) {
            UiWindow.listActuatorCommands.clear()
            UiWindow.listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.PUMP_NUTRIENT_B_OFF))
        }
    }

    // Set air_flush_callback.
    private fun air_flush_callback(variableCommanded: Double) {
        if (variableCommanded > 0.0) {
            UiWindow.listActuatorCommands.clear()
            UiWindow.listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.AIR_FLUSH_TURN_ON))
        }
        if (variableCommanded < 0.0) {
            UiWindow.listActuatorCommands.clear()
            UiWindow.listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.AIR_FLUSH_TURN_OFF))
        }
    }

    // Set light_intensity_red_callback.
    private fun light_intensity_red_callback(variableCommanded: Double) {
        if (variableCommanded > 0.0) {
            UiWindow.listActuatorCommands.clear()
            //UiWindow.listActuatorCommands.add(new FoodComputerCommand(FoodComputerAction.LIGHT_TURN_ON));
            UiWindow.listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.LIGHT_RED_TURN_ON))
            UiWindow.listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.LIGHT_BLUE_TURN_OFF))
            UiWindow.listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.LIGHT_WHITE_TURN_OFF))
        }
        if (variableCommanded < 0.0) {
            UiWindow.listActuatorCommands.clear()
            //UiWindow.listActuatorCommands.add(new FoodComputerCommand(FoodComputerAction.LIGHT_TURN_OFF));
            UiWindow.listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.LIGHT_RED_TURN_OFF))
        }
    }

    // Set light_intensity_blue_callback.
    private fun light_intensity_blue_callback(variableCommanded: Double) {
        if (variableCommanded > 0.0) {
            UiWindow.listActuatorCommands.clear()
            //UiWindow.listActuatorCommands.add(new FoodComputerCommand(FoodComputerAction.LIGHT_TURN_ON));
            UiWindow.listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.LIGHT_BLUE_TURN_ON))
            UiWindow.listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.LIGHT_RED_TURN_OFF))
            UiWindow.listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.LIGHT_WHITE_TURN_OFF))
        }
        if (variableCommanded < 0.0) {
            UiWindow.listActuatorCommands.clear()
            //UiWindow.listActuatorCommands.add(new FoodComputerCommand(FoodComputerAction.LIGHT_TURN_OFF));
            UiWindow.listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.LIGHT_BLUE_TURN_OFF))
        }
    }

    // Set light_intensity_white_callback.
    private fun light_intensity_white_callback(variableCommanded: Double) {
        if (variableCommanded > 0.0) {
            UiWindow.listActuatorCommands.clear()
            //UiWindow.listActuatorCommands.add(new FoodComputerCommand(FoodComputerAction.LIGHT_TURN_ON));
            UiWindow.listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.LIGHT_WHITE_TURN_ON))
            UiWindow.listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.LIGHT_RED_TURN_OFF))
            UiWindow.listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.LIGHT_BLUE_TURN_OFF))
        }
        if (variableCommanded < 0.0) {
            UiWindow.listActuatorCommands.clear()
            //UiWindow.listActuatorCommands.add(new FoodComputerCommand(FoodComputerAction.LIGHT_TURN_OFF));
            UiWindow.listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.LIGHT_WHITE_TURN_OFF))
        }
    }


    // Set water_level_high_callback.
    private fun water_level_high_callback(variableCommanded: Double) {
        if (variableCommanded > 0.0) {
            UiWindow.listActuatorCommands.clear()
            UiWindow.listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.WATER_PUMP_TURN_ON))
        }
        if (variableCommanded < 0.0) {
            UiWindow.listActuatorCommands.clear()
            UiWindow.listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.WATER_PUMP_TURN_OFF))
        }
    }

}