package hr.unipu.event;

import hr.unipu.plantcomputer.PlantComputerCommandJava;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds list of listeners, adds new listener, sends event (plant computer command) to
 * all listeners, with method from EventListener interface - onQueueMessage().
 */
public class EventManagerJava {

    /**
     * The list with components to be notified of a new plant computer Command received from Mosquitto.
     */
    private List<EventListenerJava> eventListeners = new ArrayList<>();


    /**
     * Used by every component which wants to be notified of new events.
     *
     * @param eventListenerJava {@link EventListenerJava}
     */
    public void addListener(EventListenerJava eventListenerJava) {
        this.eventListeners.add(eventListenerJava);
    }


    /**
     * Used by Mosquitto callback to forward a received messaged to all components in the application who were added
     * as a listener.
     *
     * @param plantComputerCommand {@link PlantComputerCommandJava}
     */
    public void sendEvent(PlantComputerCommandJava plantComputerCommand) {
        this.eventListeners.forEach(listener -> listener.onQueueMessage(plantComputerCommand));
    }

}
