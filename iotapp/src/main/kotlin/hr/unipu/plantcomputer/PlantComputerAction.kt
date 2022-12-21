package hr.unipu.plantcomputer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.LinkedList

/**
 * The enumeration list of all available Plant Computer actions.
 * By using enums with variables, we can define here which UI-elements must be enabled when an action is selected.
 */
enum class PlantComputerAction(
    val id: String, val actionName: String, var actionValue: String
) {

    UNDEFINED("000", "undefined_key", "undefined_value"),
    TEST_ALL("010", "test", "all"),
    TEST_ACTUATORS("010", "test","actuators"),
    TEST_SENSORS("050", "test", "sensors"),

    /**
     * Actuators' (default) states:
     */
    LIGHT_STATE("100", "ALPN 1", "off"),  // Actuator Light PaNel
    HUMIDIFIER_STATE("110", "AAHU 1", "off"),  // Actuator Air HUmidifier
    COOLING_FAN_STATE("120", "AAVE 1", "off"),  // Actuator Air VEntilator
    CHAMBER_FAN_STATE("130", "AACR 1", "off"),  // Actuator Air ChambeR
    HEATER_TURN_STATE("140", "AAHE 1", "off"),  // Actuator Air HEater
    WATER_PUMP_STATE("150", "AWWP 1", "off"),  // Actuator Water Water Pump

    /**
     * Actuators' actions:
     */
    LIGHT_TURN_ON("101", "ALPN 1", "on"),       // Actuator Light PaNel - on
    LIGHT_TURN_OFF("102", "ALPN 1", "off"),     // Actuator Light PaNel - off
    LIGHT_TOGGLE("103", "ALPN 1", "toggle"),     // Actuator Light PaNel - toggle (for fun)

    LIGHT_RED_TURN_ON("201", "ALPR 1", "on"),       // light_intensity_red
    LIGHT_RED_TURN_OFF("202", "ALPR 1", "off"),
    LIGHT_BLUE_TURN_ON("211", "ALPB 1", "on"),      // light_intensity_blue
    LIGHT_BLUE_TURN_OFF("212", "ALPB 1", "off"),
    LIGHT_WHITE_TURN_ON("221", "ALPW 1", "on"),     // light_intensity_white
    LIGHT_WHITE_TURN_OFF("222", "ALPW 1", "off"),

    HUMIDIFIER_TURN_ON("111","AAHU 1","on"),        // (Actuator Air HUmidifier)
    HUMIDIFIER_TURN_OFF("112", "AAHU 1", "off"),
    CHILLER_FAN_TURN_ON("121", "AAVE 1", "on"),     // chiller_fan_1
    CHILLER_FAN_TURN_OFF("122", "AAVE 1", "off"),
    CHAMBER_FAN_TURN_ON("131", "AACR 1", "on"),     // chamber_fan_1
    CHAMBER_FAN_TURN_OFF("132", "AACR 1", "off"),
    HEATER_TURN_ON("141", "AAHE 1","on"),           // heater_core_1_1 | heater_core_2_1
    HEATER_TURN_OFF("142", "AAHE 1", "off"),
    WATER_PUMP_TURN_ON("151", "AWWP 1", "on"),      // ??? pump_5_water_1
    WATER_PUMP_TURN_OFF("152", "AWWP 1", "off"),

    WATER_AERATION_PUMP_TURN_ON("231", "AWWA 1","on"),      // water_aeration_pump_1
    WATER_AERATION_PUMP_TURN_OFF("232", "AWWA 1", "off"),
    WATER_CIRCULATION_PUMP_TURN_ON("231", "AWWC 1", "on"),  // water_circulation_pump_1
    WATER_CIRCULATION_PUMP_TURN_OFF("231", "AWWC 1", "off"),
    CHILLER_PUMP_TURN_ON("241", "AACP 1", "on"),            // chiller_pump_1
    CHILLER_PUMP_TURN_OFF("242", "AACP 1", "off"),
    CHILLER_COMPRESSOR_TURN_ON("251", "AACC 1", "on"),      // chiller_compressor_1
    CHILLER_COMPRESSOR_TURN_OFF("252", "AACC 1", "off"),

    AIR_FLUSH_TURN_ON("261", "AAAF 1", "on"),               // ??? air_flush_1
    AIR_FLUSH_TURN_OFF("261", "AAAF 1", "on"),

    PUMP_PH_UP_ON("161", "APHU 1", "on"),                   // pump_3_ph_up_1
    PUMP_PH_UP_OFF("162", "APHU 1", "off"),
    PUMP_PH_DOWN_ON("171", "APHD 1", "on"),                 // pump_4_ph_down_1
    PUMP_PH_DOWN_OFF("172", "APHD 1", "on"),

    PUMP_NUTRIENT_A_ON("181", "ANUA 1", "on"),              // "pump_1_nutrient_a_1"
    PUMP_NUTRIENT_A_OFF("182", "ANUA 1", "off"),
    PUMP_NUTRIENT_B_ON("191", "ANUB 1", "on"),              // "pump_2_nutrient_b_1"
    PUMP_NUTRIENT_B_OFF("192", "ANUB 1", "off"),

    /**
     * Actuators' read actions:
     */
    LIGHT_STATE_READ("105", "ALPN 1", "read"),          // Actuator Light PaNel
    HUMIDIFIER_STATE_READ("115", "AAHU 1", "read"),     // Actuator Air HUmidifier
    COOLING_FAN_STATE_READ("125", "AAVE 1", "read"),    // Actuator Air VEntilator
    CHAMBER_FAN_STATE_READ("135", "AACR 1", "read"),    // Actuator Air ChambeR
    HEATER_TURN_STATE_READ("145", "AAHE 1", "read"),    // Actuator Air HEater
    WATER_PUMP_STATE_READ("155", "AWWP 1", "read"),     // Actuator Water Water Pump

    /**
     * Sensors' (default) readings:
     */
    TEMPERATURE_AIR_STATE("500", "SATM 1", "27.0"),     // "air_temperature"
    HUMIDITY_AIR_STATE("510", "SAHU 1", "60.0"),        // "air_humidity"
    CONDUCTIVITY_WATER_STATE("520", "SWEC 1", "1.5"),   // "water_electrical_conductivity"
    PH_WATER_STATE("530", "SWPH 1", "6.4"),             // "water_potential_hydrogen"
    TEMPERATURE_WATER_STATE("540", "SWTM 1", "18.33"),  // "water_temperature"
    LIGHT_INTENSITY_STATE("550", "SLIN 1", "15000.0"),  // ("light intensity")

    AIR_CO_STATE("560", "SACO 1", "400"),               // "air_carbon_dioxide"
    WATER_LEVEL_LOW_STATE("570", "SWLL 1", "0"),        // "water_level_low"
    WATER_LEVEL_HIGH_STATE("570", "SWLH 1", "0"),       // "water_level_high"

    /**
     * Sensors' read actions:
     */
    TEMPERATURE_AIR_STATE_READ("505", "SATM 1", "read"),        // "air_temperature"
    HUMIDITY_AIR_STATE_READ("515", "SAHU 1", "read"),           // "air_humidity"
    CONDUCTIVITY_WATER_STATE_READ("525", "SWEC 1", "read"),     // "water_electrical_conductivity"
    PH_WATER_STATE_READ("535", "SWPH 1", "read"),               // "water_potential_hydrogen"
    TEMPERATURE_WATER_STATE_READ("545", "SWTM 1", "read"),      // "water_temperature"
    LIGHT_INTENSITY_STATE_READ("555", "SLIN 1", "read"),        // ("light intensity")

    AIR_CO_STATE_READ("565", "SACO 1", "read"),                 // "air_carbon_dioxide"
    WATER_LEVEL_LOW_STATE_READ("575", "SWLL 1", "read"),        // "water_level_low"
    WATER_LEVEL_HIGH_STATE_READ("575", "SWLH 1", "read");       // "water_level_high"

    fun setActionValue(actionValue: String): PlantComputerAction {
        this.actionValue = actionValue
        return this
    }

    companion object {
        @JvmStatic
        fun fromId(id: String): PlantComputerAction {
            for (plantComputerAction in values()) {
                if (plantComputerAction.id == id) {
                    return plantComputerAction
                }
            }
            return UNDEFINED
        }

        @JvmStatic
        fun fromNameAndValue(actionName: String, actionValue: String): PlantComputerAction {
            for (plantComputerAction in values()) {
                if (plantComputerAction.actionName == actionName && plantComputerAction.actionValue == actionValue) {
                    return plantComputerAction
                }
            }
            return UNDEFINED
        }
    }
}