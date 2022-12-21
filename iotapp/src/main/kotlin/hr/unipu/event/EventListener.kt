package hr.unipu.event

import hr.unipu.plantcomputer.PlantComputerCommand

interface EventListener {
    /**
     * Whenever a new [PlantComputerCommand] is received from Mosquitto, all listeners will be notified, so they can handle
     * it for their own use.
     *
     * @param plantComputerCommand [PlantComputerCommand]
     */
    fun onQueueMessage(plantComputerCommand: PlantComputerCommand?)
}