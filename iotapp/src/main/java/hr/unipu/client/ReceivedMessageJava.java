package hr.unipu.client;

import hr.unipu.plantcomputer.PlantComputerCommandJava;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Helper class to add a {@link PlantComputerCommandJava} to a table with a timestamp.
 */
public class ReceivedMessageJava {

    private final String timestamp;
    private final PlantComputerCommandJava plantComputerCommand;

    private final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ReceivedMessageJava(PlantComputerCommandJava plantComputerCommand) {
        this.timestamp = LocalDateTime.now().format(dateFormat);
        this.plantComputerCommand = plantComputerCommand;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public PlantComputerCommandJava getPlantComputerCommand() {
        return plantComputerCommand;
    }
}
