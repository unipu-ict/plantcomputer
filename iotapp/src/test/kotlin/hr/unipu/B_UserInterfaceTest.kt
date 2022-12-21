package hr.unipu

import org.testfx.framework.junit.ApplicationTest
import org.junit.Before
import kotlin.Throws
import org.junit.After
import org.testfx.api.FxToolkit
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import hr.unipu.PlantComputerApplicationJava
import com.influxdb.client.InfluxDBClient
import com.influxdb.client.InfluxDBClientFactory
import javafx.application.Application
import org.junit.BeforeClass
import org.junit.Test
import org.testfx.api.FxAssert
import org.testfx.assertions.api.Assertions
import org.testfx.matcher.base.WindowMatchers
import java.lang.Exception

/**
 * Unit testing for User interface: Tiles, Actuators, Logs.
 *
 * @author [Siniša Sovilj](mailto:sinisa.sovilj@unipu.hr)
 */
class B_UserInterfaceTest : ApplicationTest() {
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
    fun should_contain_all_tiles() {
        Assertions.assertThat(PlantComputerApplicationKotlin.root.lookup("#temperature-tile")).hasId("temperature-tile")
        Assertions.assertThat(PlantComputerApplicationKotlin.root.lookup("#recipes-board-tile")).hasId("recipes-board-tile")
        Assertions.assertThat(PlantComputerApplicationKotlin.root.lookup("#countdown-tile")).hasId("countdown-tile")
        Assertions.assertThat(PlantComputerApplicationKotlin.root.lookup("#spark-line-sensor1")).hasId("spark-line-sensor1")
        Assertions.assertThat(PlantComputerApplicationKotlin.root.lookup("#spark-line-sensor2")).hasId("spark-line-sensor2")
        Assertions.assertThat(PlantComputerApplicationKotlin.root.lookup("#spark-line-sensor3")).hasId("spark-line-sensor3")
        Assertions.assertThat(PlantComputerApplicationKotlin.root.lookup("#spark-line-sensor4")).hasId("spark-line-sensor4")
        Assertions.assertThat(PlantComputerApplicationKotlin.root.lookup("#spark-line-sensor5")).hasId("spark-line-sensor5")
        Assertions.assertThat(PlantComputerApplicationKotlin.root.lookup("#spark-line-sensor6")).hasId("spark-line-sensor6")
        Assertions.assertThat(PlantComputerApplicationKotlin.root.lookup("#bar-gauge-sensor1")).hasId("bar-gauge-sensor1")
        Assertions.assertThat(PlantComputerApplicationKotlin.root.lookup("#bar-gauge-sensor2")).hasId("bar-gauge-sensor2")
        Assertions.assertThat(PlantComputerApplicationKotlin.root.lookup("#bar-gauge-sensor3")).hasId("bar-gauge-sensor3")
        Assertions.assertThat(PlantComputerApplicationKotlin.root.lookup("#bar-gauge-sensor4")).hasId("bar-gauge-sensor4")
        Assertions.assertThat(PlantComputerApplicationKotlin.root.lookup("#bar-gauge-sensor5")).hasId("bar-gauge-sensor5")
        Assertions.assertThat(PlantComputerApplicationKotlin.root.lookup("#bar-gauge-sensor6")).hasId("bar-gauge-sensor6")
        Assertions.assertThat(PlantComputerApplicationKotlin.root.lookup("#button-grow-light")).hasId("button-grow-light")
        Assertions.assertThat(PlantComputerApplicationKotlin.root.lookup("#button-humidifier")).hasId("button-humidifier")
        Assertions.assertThat(PlantComputerApplicationKotlin.root.lookup("#button-cooling-fan")).hasId("button-cooling-fan")
        Assertions.assertThat(PlantComputerApplicationKotlin.root.lookup("#button-chamber-fan")).hasId("button-chamber-fan")
        Assertions.assertThat(PlantComputerApplicationKotlin.root.lookup("#button-heater")).hasId("button-heater")
        Assertions.assertThat(PlantComputerApplicationKotlin.root.lookup("#button-water-circulation-pump")).hasId("button-water-circulation-pump")
        Assertions.assertThat(PlantComputerApplicationKotlin.root.lookup("#log-button")).hasId("log-button")
        Assertions.assertThat(PlantComputerApplicationKotlin.root.lookup("#recipe-button")).hasId("recipe-button")
        Assertions.assertThat(PlantComputerApplicationKotlin.root.lookup("#camera-tile")).hasId("camera-tile")
        Assertions.assertThat(PlantComputerApplicationKotlin.root.lookup("#text-tile")).hasId("text-tile")
        Assertions.assertThat(PlantComputerApplicationKotlin.root.lookup("#custom-tile")).hasId("custom-tile")
    }

    @Test
    fun when_button_is_clicked_actuators_are_toggled() {
        // when:
        clickOn("#button-grow-light")
        clickOn("#button-humidifier")
        clickOn("#button-cooling-fan")
        clickOn("#button-chamber-fan")
        clickOn("#button-heater")
        clickOn("#button-water-circulation-pump")

        // then:
        Assertions.assertThat(PlantComputerApplicationKotlin.root.lookup("#button-grow-light")).isEnabled
        Assertions.assertThat(PlantComputerApplicationKotlin.root.lookup("#button-humidifier")).isEnabled
        Assertions.assertThat(PlantComputerApplicationKotlin.root.lookup("#button-cooling-fan")).isEnabled
        Assertions.assertThat(PlantComputerApplicationKotlin.root.lookup("#button-chamber-fan")).isEnabled
        Assertions.assertThat(PlantComputerApplicationKotlin.root.lookup("#button-heater")).isEnabled
        Assertions.assertThat(PlantComputerApplicationKotlin.root.lookup("#button-water-circulation-pump")).isEnabled
    }

    @Test
    fun test_log_window() {

        // when:
        clickOn("#log-button")

        // then:
        FxAssert.verifyThat(window("Logs"), WindowMatchers.isShowing())
    }
}