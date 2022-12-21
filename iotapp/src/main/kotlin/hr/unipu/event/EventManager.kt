package hr.unipu.event

import hr.unipu.plantcomputer.PlantComputerCommand
import java.util.ArrayList
import java.util.function.Consumer

/**
 * Holds list of listeners, adds new listener, sends event (plant computer command) to
 * all listeners, with method from EventListener interface - onQueueMessage().
 */
class EventManager {
    /**
     * The list with components to be notified of a new plant computer Command received from Mosquitto.
     */
    private val eventListeners: MutableList<EventListener> = ArrayList()

    /**
     * Used by every component which wants to be notified of new events.
     *
     * @param eventListener [EventListener]
     */
    fun addListener(eventListener: EventListener) {
        eventListeners.add(eventListener)
    }

    /**
     * Used by Mosquitto callback to forward a received messaged to all components in the application who were added
     * as a listener.
     *
     * @param plantComputerCommand [PlantComputerCommand]
     */
    fun sendEvent(plantComputerCommand: PlantComputerCommand) {
        eventListeners.forEach(Consumer { listener: EventListener -> listener.onQueueMessage(plantComputerCommand) })
    }
}