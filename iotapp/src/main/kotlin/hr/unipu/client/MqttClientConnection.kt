package hr.unipu.client

import hr.unipu.event.EventManager
import hr.unipu.plantcomputer.PlantComputerCommand
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttException
import org.eclipse.paho.client.mqttv3.MqttMessage

/**
 * Initialize connection with MQTT server, connect, subscribe to MQTT topic,
 * and set callback method (with forwarding of event manager).
 */
class MqttClientConnection(mqttServerURI: String, private val eventManager: EventManager) {
    lateinit var mqttClient: MqttClient
        private set
    private lateinit var mqttTopic: String
    private fun initConnection(mqttServerURI: String): Boolean {
        try {
            mqttClient = MqttClient("tcp://$mqttServerURI:1883", MqttClient.generateClientId())
            println("Mosquitto client initialized.")
            return true
        } catch (ex: MqttException) {
            System.err.println("MqttException: " + ex.message)
        }
        return false
    }

    private fun connect() {
        try {
            mqttClient.connect()
            println("Connected to Mosquitto client.")
        } catch (ex: MqttException) {
            System.err.println("MqttException: " + ex.message)
        }
    }

    fun subscribe(mqttTopic: String) {
        this.mqttTopic = mqttTopic
        try {
            mqttClient.subscribe(this.mqttTopic)
            mqttClient.setCallback(MqttClientCallback(eventManager))
            println("Subscribed to Mqtt topic: $mqttTopic")
        } catch (ex: MqttException) {
            System.err.println("Error while subscribing: " + ex.message)
        }
    }

    fun sendMessage(plantComputerCommand: PlantComputerCommand, mqttTopic: String) {
        this.mqttTopic = mqttTopic

        //this.sendMessage(plantComputerCommand.toStringCommand());      // Sending old command e.g. 100:APLN 1:on
        this.sendMessage(plantComputerCommand.toJsonCommand(), mqttTopic) // Sending JSON command.
    }

    fun sendMessage(messageText: String, mqttTopic: String) {
        this.mqttTopic = mqttTopic
        if (!mqttClient.isConnected) {
            System.err.println("The queue client is not connected!")
            connect()
        }
        val message = MqttMessage()
        message.payload = messageText.toByteArray()
        try {
            mqttClient.publish(this.mqttTopic, message)
        } catch (ex: MqttException) {
            System.err.println("MqttException: " + ex.message)
        }
    }

    fun disconnect() {
        try {
            mqttClient.disconnect()
        } catch (ex: MqttException) {
            System.err.println("MqttException: " + ex.message)
        }
    }

    @JvmName("getMqttClient1")
    fun getMqttClient(): MqttClient {
        return mqttClient

    }

    init {
        run {
            if (!initConnection(mqttServerURI)) {
                System.err.println("Initializing connection failed.")
                return@run
            }
        }
        connect()
    }
}