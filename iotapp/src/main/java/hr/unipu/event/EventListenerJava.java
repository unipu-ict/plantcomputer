package hr.unipu.event;

import hr.unipu.plantcomputer.PlantComputerCommandJava;

public interface EventListenerJava {
    /**
     * Whenever a new {@link PlantComputerCommandJava} is received from Mosquitto, all listeners will be notified, so they can handle
     * it for their own use.
     *
     * @param plantComputerCommand {@link PlantComputerCommandJava}
     */
    void onQueueMessage(PlantComputerCommandJava plantComputerCommand);

}
