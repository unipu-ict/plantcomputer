package hr.unipu;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.domain.HealthCheck;
import hr.unipu.client.MqttClientConnectionJava;
import hr.unipu.event.EventManagerJava;
import hr.unipu.ui.UiWindowJava;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.List;
import java.util.Map;

/**
 * Plant Computer Controller JavaFX class.
 */
public class PlantComputerApplicationJava extends Application {
    public static Parameters parameters = null;
    public static InfluxDBClient influxDBClient = null;
    public static MqttClientConnectionJava mqttClientConnection = null;
    public static Boolean isInfluxDbOnline = false;
    public static Parent root;

    /**
     * JavaFX app entry point. (main thread)
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        // Later, to access any (named or raw) arguments invoke the 'getParameters()' method on the 'Application' class.
        Application.launch(args);
    }


    /**
     * start() method with auto-generated primary stage. (application thread)
     * @param stage JavaFX auto-generated primary stage.
     *
     * Initialize the {@link EventManagerJava} and {@link MqttClientConnectionJava}, start the webserver and show the UI.
     */
    @Override
    public void start(Stage stage) {

        // Application parameters.
        System.out.println(parameters);
        parameters = this.getParameters();
        Map<String, String> namedParams = null;
        List<String> unnamedParams = null;
        List<String> rawParams = null;
        try {
            namedParams = parameters.getNamed();
            unnamedParams = parameters.getUnnamed();
            rawParams = parameters.getRaw();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String paramStr = "Named Parameters: " + namedParams + "\n" +
                "Unnamed Parameters: " + unnamedParams + "\n" +
                "Raw Parameters: " + rawParams;
        System.out.println(paramStr);

        // EventManager.
        EventManagerJava eventManagerJava = new EventManagerJava();

        // MQTT client: instantiate, subscribe.
        mqttClientConnection = new MqttClientConnectionJava( "serv.ovh.dfo.ninja", eventManagerJava);
        mqttClientConnection.subscribe("plantComputerCommand4Sensors");
        mqttClientConnection.subscribe("plantComputerCommand4Actuators");
        mqttClientConnection.subscribe("plantComputerState");

        // InfluxDB database: instantiate, check health.
        char[] token = "84gcXr6-ZNUA9W7LuSq7hfeTvMp_lsBbBhjd2pGyxJivIDEBcyRczqqt-kU5Sb2zbUMPa_inAeOVfnvvtz-7aQ==".toCharArray();
        String org = "hr.unipu";
        String bucket = "plantComputerDb";
        try {
            influxDBClient = InfluxDBClientFactory.create("http://127.0.0.0:8086", token, org, bucket);
            if (influxDBClient!=null) {
                HealthCheck healthCheck = influxDBClient.health();
                if(healthCheck.getStatus() == HealthCheck.StatusEnum.PASS) {
                    isInfluxDbOnline = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        root = new UiWindowJava(eventManagerJava, mqttClientConnection);
        Scene scene = new Scene(root, 1280, 800);

        stage.setScene(scene);
        //stage.setFullScreen(true);
        stage.show();

    }

    @Override
    public void stop() throws Exception {
        super.stop();
        mqttClientConnection.disconnect();
        influxDBClient.close();     // Redundant (autoclosable).
    }
}