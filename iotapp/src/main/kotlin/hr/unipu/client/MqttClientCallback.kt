package hr.unipu.client

import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.lang.Runnable
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.core.JsonProcessingException
import hr.unipu.event.EventManager
import hr.unipu.plantcomputer.PlantComputerCommand
import javafx.application.Platform
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttException
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime

/**
 * Implementation of MqttCallback interface methods: connectionLost(), deliveryComplete(), messageArrived().
 */
class MqttClientCallback(val eventManager: EventManager) : MqttCallback {
    override fun connectionLost(throwable: Throwable) {
        println("Connection to MQTT broker lost!")
    }

    override fun messageArrived(mqttTopic: String, mqttMessage: MqttMessage) {
        val jsonMessage = String(mqttMessage.payload)
        if (mqttTopic == "plantComputerCommand4Sensors" || mqttTopic == "plantComputerCommand4Actuators") {
            println("Filtered MQTT topic: \"$mqttTopic\".")
            println("Message received:\n\t$jsonMessage")
            Platform.runLater {
                println("Forwarding MQTT message (of actuators) to all listeners.")
                parseJsonMessage2Commands(jsonMessage)
            }
        } else if (mqttTopic == "plantComputerState") {
            println("Filtered MQTT topic: \"$mqttTopic\".")
            println("Message received:\n\t$jsonMessage")
            Platform.runLater {
                println("Forwarding MQTT message (from sensors) to all listeners.")
                parseJsonMessage2Readings(jsonMessage)
            }
        }
    }

    /**
     * Parse jsonMessage to commands.
     * @param jsonMessage
     */
    private fun parseJsonMessage2Commands(jsonMessage: String) {
        val objectMapper = ObjectMapper()
        try {
            val parsedListActuatorCommands =
                objectMapper.readValue(jsonMessage, Array<PlantComputerCommand>::class.java)
            for (parsedListActuatorCommand in parsedListActuatorCommands) {
                eventManager.sendEvent(parsedListActuatorCommand)
            }
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
        }
    }

    /**
     * Parse jsonMessage to sensors' readings.
     * @param jsonMessage
     */
    private fun parseJsonMessage2Readings(jsonMessage: String) {
        val objectMapper = ObjectMapper()
        try {
            val parsedListSensorReadings = objectMapper.readValue(jsonMessage, Array<PlantComputerCommand>::class.java)
            for (parsedListActuatorReading in parsedListSensorReadings) {
                eventManager.sendEvent(parsedListActuatorReading)
            }
        } catch (e: JsonProcessingException) {
            e.printStackTrace()
        }
    }

    override fun deliveryComplete(iMqttDeliveryToken: IMqttDeliveryToken) {
        println("Delivery complete.")
    }
}