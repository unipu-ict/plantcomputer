package hr.unipu.client

import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.lang.Runnable
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.core.JsonProcessingException
import hr.unipu.plantcomputer.PlantComputerCommand
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttException
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime

/**
 * Helper class to add a [PlantComputerCommand] to a table with a timestamp.
 */
class ReceivedMessage(plantComputerCommand: PlantComputerCommand) {
    val timestamp: String
    val plantComputerCommand: PlantComputerCommand
    private val dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

    init {
        timestamp = LocalDateTime.now().format(dateFormat)
        this.plantComputerCommand = plantComputerCommand
    }
}