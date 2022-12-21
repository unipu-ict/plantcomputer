package hr.unipu;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import hr.unipu.client.MqttClientConnectionJava;
import hr.unipu.event.EventManagerJava;
import javafx.application.Application;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import org.junit.*;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit.ApplicationTest;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertArrayEquals;

/**
 * Unit testing for: Parameters/Arguments, MQTT client connection, InfulxDB database connection.
 *
 * @author [Siniša Sovilj](mailto:sinisa.sovilj@unipu.hr)
 */
public class A_PlantComputerAppTestJava extends ApplicationTest {

    /**
     * Will be called with {@code @Before} semantics, i.e. before each test method.
     */
    @BeforeClass
    public static void main() {
        String[] appArgs = new String[]{"--simulationMode=true"};
        Class appClass = PlantComputerApplicationJava.class;
        try {
            ApplicationTest.launch(appClass, appArgs);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Before
    public void setUp () throws Exception {
    }

    @After
    public void tearDown () throws Exception {
        FxToolkit.hideStage();
        release(new KeyCode[]{});
        release(new MouseButton[]{});
    }

    @Test
    public void testApplicationParameters() {
        // Expected
        List<String> rawParamsExpected= List.of("--simulationMode=true");

        // Actual
        Application.Parameters parametersActual = PlantComputerApplicationJava.parameters;
        Map<String, String> namedParamsActual = parametersActual.getNamed();
        List<String> unnamedParamsActual = parametersActual.getUnnamed();
        List<String> rawParamsActual = parametersActual.getRaw();

        // Assert
        Assert.assertArrayEquals(rawParamsActual.toArray(), rawParamsActual.toArray());
    }

    @Test
    public void testMqttClientConnection() {
        // Expected
        EventManagerJava eventManagerJava = new EventManagerJava();
        MqttClientConnectionJava mqttClientConnectionExpected = new MqttClientConnectionJava( "serv.ovh.dfo.ninja", eventManagerJava);
        mqttClientConnectionExpected.subscribe("plantComputerCommand4Sensors");
        mqttClientConnectionExpected.subscribe("plantComputerCommand4Actuators");
        mqttClientConnectionExpected.subscribe("plantComputerState");

        // Actual
        MqttClientConnectionJava mqttClientConnectionActual = PlantComputerApplicationJava.mqttClientConnection;

        // Assert
        Assert.assertEquals(mqttClientConnectionActual.getMqttClient().getCurrentServerURI(), mqttClientConnectionExpected.getMqttClient().getCurrentServerURI());
        Assert.assertEquals(mqttClientConnectionActual.getMqttClient().getTopic("plantComputerCommand4Sensors").getName(), mqttClientConnectionExpected.getMqttClient().getTopic("plantComputerCommand4Sensors").getName());
        Assert.assertEquals(mqttClientConnectionActual.getMqttClient().getTopic("plantComputerCommand4Actuators").getName(), mqttClientConnectionExpected.getMqttClient().getTopic("plantComputerCommand4Actuators").getName());
        Assert.assertEquals(mqttClientConnectionActual.getMqttClient().getTopic("plantComputerState").getName(), mqttClientConnectionExpected.getMqttClient().getTopic("plantComputerState").getName());
    }

    @Test
    public void testInfluxDbConnection() {
        // Expected
        char[] token = "84gcXr6-ZNUA9W7LuSq7hfeTvMp_lsBbBhjd2pGyxJivIDEBcyRczqqt-kU5Sb2zbUMPa_inAeOVfnvvtz-7aQ==".toCharArray();
        String org = "hr.unipu";
        String bucket = "plantComputerDb";
        InfluxDBClient influxDBClientExpected = InfluxDBClientFactory.create("http://127.0.0.0:8086", token, org, bucket);

        // Actual
        InfluxDBClient influxDBClientActual =  PlantComputerApplicationJava.influxDBClient;

        // Assert
        Assert.assertEquals(influxDBClientActual.getClass(), influxDBClientExpected.getClass());
    }
}
