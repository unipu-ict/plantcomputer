package hr.unipu

import org.testfx.framework.junit.ApplicationTest
import kotlin.Throws
import org.testfx.api.FxToolkit
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import hr.unipu.PlantComputerApplicationKotlin
import com.influxdb.client.InfluxDBClient
import com.influxdb.client.InfluxDBClientFactory
import hr.unipu.client.MqttClientConnection
import hr.unipu.event.EventManager
import javafx.application.Application
import org.junit.*
import org.testfx.api.FxAssert
import org.testfx.matcher.base.WindowMatchers
import java.lang.Exception
import java.util.List

/**
 * Unit testing for: Parameters/Arguments, MQTT client connection, InfulxDB database connection.
 *
 * @author [Siniša Sovilj](mailto:sinisa.sovilj@unipu.hr)
 */
class A_PlantComputerAppTest : ApplicationTest() {

    /**
     * Will be called with `@Before` semantics, i.e. before each test method.
     */
    @Before
    fun main() {
        val appArgs = arrayOf("--simulationMode=true")
        val appClass: Class<*> = PlantComputerApplicationKotlin::class.java
        try {
            launch(appClass as Class<out Application>?, *appArgs)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    @Before
    @Throws(Exception::class)
    fun setUp() {
    }

    @After
    @Throws(Exception::class)
    fun tearDown() {
        FxToolkit.hideStage()
        release(*arrayOf<KeyCode>())
        release(*arrayOf<MouseButton>())
    }

    @Test
    fun testApplicationParameters() {
        // Expected
        val rawParamsExpected = List.of("--simulationMode=true")

        // Actual
        val parametersActual = PlantComputerApplicationKotlin.parameters
        val namedParamsActual = parametersActual.named
        val unnamedParamsActual = parametersActual.unnamed
        val rawParamsActual = parametersActual.raw

        // Assert
        Assert.assertArrayEquals(rawParamsActual.toTypedArray(), rawParamsActual.toTypedArray())
    }

    @Test
    fun testMqttClientConnection() {
        // Expected
        val eventManager = EventManager()
        val mqttClientConnectionExpected = MqttClientConnection("serv.ovh.dfo.ninja", eventManager)
        mqttClientConnectionExpected.subscribe("plantComputerCommand4Sensors")
        mqttClientConnectionExpected.subscribe("plantComputerCommand4Actuators")
        mqttClientConnectionExpected.subscribe("plantComputerState")

        // Actual
        val mqttClientConnectionActual = PlantComputerApplicationKotlin.mqttClientConnection

        // Assert
        Assert.assertEquals(
            mqttClientConnectionActual.mqttClient.currentServerURI,
            mqttClientConnectionExpected.getMqttClient().currentServerURI
        )
        Assert.assertEquals(
            mqttClientConnectionActual.mqttClient.getTopic("plantComputerCommand4Sensors").name,
            mqttClientConnectionExpected.getMqttClient().getTopic("plantComputerCommand4Sensors")!!.name
        )
        Assert.assertEquals(
            mqttClientConnectionActual.mqttClient.getTopic("plantComputerCommand4Actuators").name,
            mqttClientConnectionExpected.getMqttClient().getTopic("plantComputerCommand4Actuators")!!.name
        )
        Assert.assertEquals(
            mqttClientConnectionActual.mqttClient.getTopic("plantComputerState").name,
            mqttClientConnectionExpected.getMqttClient().getTopic("plantComputerState")!!.name
        )
    }

    @Test
    fun testInfluxDbConnection() {
        // Expected
        val token =
            "84gcXr6-ZNUA9W7LuSq7hfeTvMp_lsBbBhjd2pGyxJivIDEBcyRczqqt-kU5Sb2zbUMPa_inAeOVfnvvtz-7aQ==".toCharArray()
        val org = "hr.unipu"
        val bucket = "plantComputerDb"
        val influxDBClientExpected = InfluxDBClientFactory.create("http://127.0.0.0:8086", token, org, bucket)

        // Actual
        val influxDBClientActual = PlantComputerApplicationKotlin.influxDBClient

        // Assert
        Assert.assertEquals(influxDBClientActual.javaClass, influxDBClientExpected.javaClass)
    }


}