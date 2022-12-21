package hr.unipu.plantcomputer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * PlantComputerCommand as it is exchanged with the Arduino.
 * - creates Mosquitto command string;
 */
public class PlantComputerCommandJava {
    private String id;
    private String actionName;
    private String actionValue;
    private PlantComputerActionJava plantComputerAction;

    /**
     * No argument constructor.
     */
    public PlantComputerCommandJava() {
        this.plantComputerAction = PlantComputerActionJava.UNDEFINED;
        this.id = plantComputerAction.getId();
        this.actionName = plantComputerAction.getActionName();
        this.actionValue = plantComputerAction.getActionValue();
    }

    /**
     * Constructor with arguments.
     */
    public PlantComputerCommandJava(PlantComputerActionJava plantComputerAction) {
        this.plantComputerAction = plantComputerAction;
        this.id = plantComputerAction.getId();
        this.actionName = plantComputerAction.getActionName();
        this.actionValue = plantComputerAction.getActionValue();
    }

    /**
     * Initialize a {@link PlantComputerCommandJava} from a ":"-separated String as exchanged via Mosquitto.
     * Example: 100                 // id for turning actuator (for light) on
     *          100 : ALPN 1 : on
     *          ALPN 1 : on         // turn actuator (for light) on
     *          SWTM 1 : read       // read sensor (for temperature)
     * @param command {@link String}
     */
    public PlantComputerCommandJava(String command) {
        PlantComputerActionJava plantComputerAction = PlantComputerActionJava.UNDEFINED;
        String[] parts = command.split(":");

        // e.g. 100
        plantComputerAction = parts.length == 1 ? PlantComputerActionJava.fromId(parts[0].trim()) : PlantComputerActionJava.UNDEFINED;

        // e.g. ALPN 1 : on
        if (parts.length == 2) {
            boolean containsOnlyNumbers = !parts[0].trim().contains("[a-zA-Z]+");
            if (containsOnlyNumbers) {
                plantComputerAction = PlantComputerActionJava.UNDEFINED;
            } else {
                this.actionName = parts[0].trim();
                this.actionValue = parts[1].trim();
                plantComputerAction = PlantComputerActionJava.fromNameAndValue(actionName, actionValue);
            }
        }

        // e.g. 100 : ALPN 1 : on
        if (parts.length == 3) {
            this.id = parts[0].trim();
            this.actionName = parts[1].trim();
            this.actionValue = parts[2].trim();
            plantComputerAction = PlantComputerActionJava.fromId(id);     // ID is dominant.
        }

        this.id = plantComputerAction.getId();
        this.actionName = plantComputerAction.getActionName();
        this.actionValue = plantComputerAction.getActionValue();

    }


    /**
     * Convert to a ":"-separated string command.
     * e.g. 100:ALPN 1:on
     *
     * @return The command as ":"-separated String
     */
    public String toStringCommand() {
        return this.getId() + ":"
                + this.getActionName() + ":"
                + this.getActionValue();
    }


    /**
     * Convert to JSON String command.
     * e.g. {"id":"100","actionName":"ALPN 1","actionValue":"on"}
     *
     * @return The command as JSON string.
     */
    public String toJsonCommand() {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = "{}";
        try {
            jsonString = objectMapper.writeValueAsString(this);     // FoodComputerCommand object to JSON.
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonString;
    }


    public String getId() {
        return id;
    }
    public String getActionName() {
        return actionName;
    }
    public String getActionValue() {
        return actionValue;
    }
    public PlantComputerActionJava getPlantComputerAction() {
        return plantComputerAction;
    }

}
