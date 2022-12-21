package hr.unipu.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import hr.unipu.event.EventManagerJava;
import hr.unipu.plantcomputer.PlantComputerCommandJava;
import javafx.application.Platform;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * Implementation of MqttCallback interface methods: connectionLost(), deliveryComplete(), messageArrived().
 */
public class MqttClientCallbackJava implements MqttCallback {

    final EventManagerJava eventManagerJava;

    public MqttClientCallbackJava(EventManagerJava eventManagerJava) {
        this.eventManagerJava = eventManagerJava;
    }


    @Override
    public void connectionLost(Throwable throwable) {
        System.out.println("Connection to MQTT broker lost!");
    }


    @Override
    public void messageArrived(String mqttTopic, MqttMessage mqttMessage) {
        String jsonMessage = new String(mqttMessage.getPayload());

        if (mqttTopic.equals("plantComputerCommand4Sensors") || mqttTopic.equals("plantComputerCommand4Actuators") ) {
            System.out.println("Filtered MQTT topic: \"" + mqttTopic + "\".");
            System.out.println("Message received:\n\t" + jsonMessage);
            Platform.runLater(() -> {
                System.out.println("Forwarding MQTT message (of sensors & actuators) to all listeners.");
                parseJsonMessage2Commands(jsonMessage);

            });

        } else if (mqttTopic.equals("plantComputerState")) {
            System.out.println("Filtered MQTT topic: \"" + mqttTopic + "\".");
            System.out.println("Message received:\n\t" + jsonMessage);
            Platform.runLater(() -> {
                System.out.println("Forwarding MQTT message (from sensors) to all listeners.");
                parseJsonMessage2Readings(jsonMessage);
            });

        }


    }

    /**
     * Parse jsonMessage to commands.
     * @param jsonMessage
     */
    private void parseJsonMessage2Commands(String jsonMessage) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            PlantComputerCommandJava[] parsedListActuatorCommands = objectMapper.readValue(jsonMessage, PlantComputerCommandJava[].class);
            for (PlantComputerCommandJava parsedListActuatorCommand : parsedListActuatorCommands) {
                //System.out.println(parsedListActuatorCommand.toStringCommand());
                eventManagerJava.sendEvent(parsedListActuatorCommand);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


    /**
     * Parse jsonMessage to sensors' readings.
     * @param jsonMessage
     */
    private void parseJsonMessage2Readings(String jsonMessage) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            PlantComputerCommandJava[] parsedListSensorReadings = objectMapper.readValue(jsonMessage, PlantComputerCommandJava[].class);
            for (PlantComputerCommandJava parsedListActuatorReading : parsedListSensorReadings) {
                //System.out.println(parsedListActuatorReading.toStringCommand());
                eventManagerJava.sendEvent(parsedListActuatorReading);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        System.out.println("Delivery complete.");
    }

}