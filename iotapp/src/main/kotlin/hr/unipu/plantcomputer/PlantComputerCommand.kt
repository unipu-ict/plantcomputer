package hr.unipu.plantcomputer

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import java.util.LinkedList

/**
 * PlantComputerCommand as it is exchanged with the Arduino.
 * - creates Mosquitto command string;
 */
class PlantComputerCommand {
    var id: String
        private set
    var actionName: String
        private set
    var actionValue: String
        private set
    lateinit var plantComputerAction: PlantComputerAction
        private set

    /**
     * No argument constructor.
     */
    constructor() {
        plantComputerAction = PlantComputerAction.UNDEFINED
        id = plantComputerAction.id
        actionName = plantComputerAction.actionName
        actionValue = plantComputerAction.actionValue
    }

    /**
     * Constructor with arguments.
     */
    constructor(plantComputerAction: PlantComputerAction) {
        this.plantComputerAction = plantComputerAction
        id = plantComputerAction.id
        actionName = plantComputerAction.actionName
        actionValue = plantComputerAction.actionValue
    }

    /**
     * Initialize a [PlantComputerCommand] from a ":"-separated String as exchanged via Mosquitto.
     * Example: 100                 // id for turning actuator (for light) on
     * 100 : ALPN 1 : on
     * ALPN 1 : on         // turn actuator (for light) on
     * SWTM 1 : read       // read sensor (for temperature)
     * @param command [String]
     */
    constructor(command: String) {
        var plantComputerAction = PlantComputerAction.UNDEFINED
        val parts = command.split(":".toRegex()).toTypedArray()

        // e.g. 100
        plantComputerAction =
            if (parts.size == 1) PlantComputerAction.Companion.fromId(parts[0].trim { it <= ' ' }) else PlantComputerAction.UNDEFINED

        // e.g. ALPN 1 : on
        if (parts.size == 2) {
            val containsOnlyNumbers = !parts[0].trim { it <= ' ' }.contains("[a-zA-Z]+")
            if (containsOnlyNumbers) {
                plantComputerAction = PlantComputerAction.UNDEFINED
            } else {
                actionName = parts[0].trim { it <= ' ' }
                actionValue = parts[1].trim { it <= ' ' }
                plantComputerAction = PlantComputerAction.Companion.fromNameAndValue(actionName, actionValue)
            }
        }

        // e.g. 100 : ALPN 1 : on
        if (parts.size == 3) {
            id = parts[0].trim { it <= ' ' }
            actionName = parts[1].trim { it <= ' ' }
            actionValue = parts[2].trim { it <= ' ' }
            plantComputerAction = PlantComputerAction.Companion.fromId(id) // ID is dominant.
        }
        id = plantComputerAction.id
        actionName = plantComputerAction.actionName
        actionValue = plantComputerAction.actionValue
    }

    /**
     * Convert to a ":"-separated string command.
     * e.g. 100:ALPN 1:on
     *
     * @return The command as ":"-separated String
     */
    fun toStringCommand(): String {
        return (id + ":"
                + actionName + ":"
                + actionValue)
    }

    /**
     * Convert to JSON String command.
     * e.g. {"id":"100","actionName":"ALPN 1","actionValue":"on"}
     *
     * @return The command as JSON string.
     */
    fun toJsonCommand(): String {
        val objectMapper = ObjectMapper()
        var jsonString = "{}"
        try {
            jsonString = objectMapper.writeValueAsString(this) // FoodComputerCommand object to JSON.
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
        }
        return jsonString
    }
}