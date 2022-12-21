package hr.unipu;

import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxAssert;
import org.testfx.api.FxToolkit;
import org.testfx.assertions.api.Assertions;
import org.testfx.framework.junit.ApplicationTest;
import org.testfx.matcher.base.WindowMatchers;


import static hr.unipu.PlantComputerApplicationJava.root;

/**
 * Unit testing for User interface: Tiles, Actuators, Logs.
 *
 * @author [Siniša Sovilj](mailto:sinisa.sovilj@unipu.hr)
 */
public class B_UserInterfaceTestJava extends ApplicationTest {

    /**
     * Will be called with {@code @Before} semantics, i.e. before each test method.
     */
    @Before
    public void main() {
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
    public void should_contain_all_tiles() {
        Assertions.assertThat(root.lookup("#temperature-tile")).hasId("temperature-tile");
        Assertions.assertThat(root.lookup("#recipes-board-tile")).hasId("recipes-board-tile");
        Assertions.assertThat(root.lookup("#countdown-tile")).hasId("countdown-tile");

        Assertions.assertThat(root.lookup("#spark-line-sensor1")).hasId("spark-line-sensor1");
        Assertions.assertThat(root.lookup("#spark-line-sensor2")).hasId("spark-line-sensor2");
        Assertions.assertThat(root.lookup("#spark-line-sensor3")).hasId("spark-line-sensor3");
        Assertions.assertThat(root.lookup("#spark-line-sensor4")).hasId("spark-line-sensor4");
        Assertions.assertThat(root.lookup("#spark-line-sensor5")).hasId("spark-line-sensor5");
        Assertions.assertThat(root.lookup("#spark-line-sensor6")).hasId("spark-line-sensor6");

        Assertions.assertThat(root.lookup("#bar-gauge-sensor1")).hasId("bar-gauge-sensor1");
        Assertions.assertThat(root.lookup("#bar-gauge-sensor2")).hasId("bar-gauge-sensor2");
        Assertions.assertThat(root.lookup("#bar-gauge-sensor3")).hasId("bar-gauge-sensor3");
        Assertions.assertThat(root.lookup("#bar-gauge-sensor4")).hasId("bar-gauge-sensor4");
        Assertions.assertThat(root.lookup("#bar-gauge-sensor5")).hasId("bar-gauge-sensor5");
        Assertions.assertThat(root.lookup("#bar-gauge-sensor6")).hasId("bar-gauge-sensor6");

        Assertions.assertThat(root.lookup("#button-grow-light")).hasId("button-grow-light");
        Assertions.assertThat(root.lookup("#button-humidifier")).hasId("button-humidifier");
        Assertions.assertThat(root.lookup("#button-cooling-fan")).hasId("button-cooling-fan");
        Assertions.assertThat(root.lookup("#button-chamber-fan")).hasId("button-chamber-fan");
        Assertions.assertThat(root.lookup("#button-heater")).hasId("button-heater");
        Assertions.assertThat(root.lookup("#button-water-circulation-pump")).hasId("button-water-circulation-pump");
        Assertions.assertThat(root.lookup("#log-button")).hasId("log-button");
        Assertions.assertThat(root.lookup("#recipe-button")).hasId("recipe-button");

        Assertions.assertThat(root.lookup("#camera-tile")).hasId("camera-tile");
        Assertions.assertThat(root.lookup("#text-tile")).hasId("text-tile");
        Assertions.assertThat(root.lookup("#custom-tile")).hasId("custom-tile");
    }


    @Test
    public void when_button_is_clicked_actuators_are_toggled() {
        // when:
        clickOn("#button-grow-light");
        clickOn("#button-humidifier");
        clickOn("#button-cooling-fan");
        clickOn("#button-chamber-fan");
        clickOn("#button-heater");
        clickOn("#button-water-circulation-pump");

        // then:
        Assertions.assertThat(root.lookup("#button-grow-light")).isEnabled();
        Assertions.assertThat(root.lookup("#button-humidifier")).isEnabled();
        Assertions.assertThat(root.lookup("#button-cooling-fan")).isEnabled();
        Assertions.assertThat(root.lookup("#button-chamber-fan")).isEnabled();
        Assertions.assertThat(root.lookup("#button-heater")).isEnabled();
        Assertions.assertThat(root.lookup("#button-water-circulation-pump")).isEnabled();
    }

    @Test
    public void test_log_window() {

        // when:
        clickOn("#log-button");

        // then:
        FxAssert.verifyThat(window("Logs"), WindowMatchers.isShowing());
    }




}
