package hr.unipu

import com.influxdb.client.InfluxDBClientFactory
import com.influxdb.client.domain.HealthCheck
import javafx.scene.Scene
import kotlin.Throws
import com.influxdb.client.InfluxDBClient
import hr.unipu.client.MqttClientConnection
import hr.unipu.event.EventManager
import hr.unipu.ui.UiWindow
import javafx.application.Application
import javafx.scene.Parent
import javafx.stage.Stage
import java.lang.Exception
import kotlin.jvm.JvmStatic

/**
 * Plant Computer Controller JavaFX class.
 */
class PlantComputerApplicationKotlin : Application() {
    /**
     * start() method with auto-generated primary stage. (application thread)
     * @param stage JavaFX auto-generated primary stage.
     *
     * Initialize the {@link EventManager} and {@link MqttClientConnection}, start the webserver and show the UI.
     */
    override fun start(stage: Stage) {

        // Application parameters.
        Companion.parameters = parameters
        lateinit var namedParams: Map<String, String>
        lateinit var unnamedParams: List<String>
        lateinit var rawParams: List<String>
        try {
            namedParams = parameters.getNamed()
            unnamedParams = parameters.getUnnamed()
            rawParams = parameters.getRaw()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val paramStr = """
             Named Parameters: $namedParams
             Unnamed Parameters: $unnamedParams
             Raw Parameters: $rawParams
             """.trimIndent()
        println(paramStr)

        // EventManager.
        val eventManager = EventManager()

        // MQTT client: instantiate, subscribe.
        mqttClientConnection = MqttClientConnection("serv.ovh.dfo.ninja", eventManager)
        mqttClientConnection.subscribe("plantComputerCommand4Sensors")
        mqttClientConnection.subscribe("plantComputerCommand4Actuators")
        mqttClientConnection.subscribe("plantComputerState")

        // InfluxDB database: instantiate, check health.
        val token =
            "84gcXr6-ZNUA9W7LuSq7hfeTvMp_lsBbBhjd2pGyxJivIDEBcyRczqqt-kU5Sb2zbUMPa_inAeOVfnvvtz-7aQ==".toCharArray()
        val org = "hr.unipu"
        val bucket = "plantComputerDb"
        try {
            // Problem: Caused by: java.net.ConnectException: Failed to connect to localhost/0:0:0:0:0:0:0:1:8086
            // Solution: localhost -> 127.0.0.0
            influxDBClient = InfluxDBClientFactory.create("http://127.0.0.0:8086", token, org, bucket)
            if (influxDBClient != null) {
                val healthCheck = influxDBClient.health()
                if (healthCheck.status == HealthCheck.StatusEnum.PASS) {
                    isInfluxDbOnline = true
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        root = UiWindow(eventManager, mqttClientConnection)
        val scene = Scene(root, 1280.0, 800.0)
        stage.scene = scene
        //stage.setFullScreen(true);
        stage.show()
    }

    @Throws(Exception::class)
    override fun stop() {
        super.stop()
        mqttClientConnection.disconnect()
        influxDBClient.close() // Redundant (autoclosable).
    }

    companion object {
        lateinit var parameters: Parameters
        lateinit var influxDBClient: InfluxDBClient
        lateinit var mqttClientConnection: MqttClientConnection
        var isInfluxDbOnline = false
        lateinit var root: Parent

        /**
         * JavaFX app entry point. (main thread)
         * @param args Command line arguments.
         */
        @JvmStatic
        fun main(args: Array<String>) {
            // Later, to access any (named or raw) arguments invoke the 'getParameters()' method on the 'Application' class.
            launch(PlantComputerApplicationKotlin::class.java, *args)
        }
    }
}