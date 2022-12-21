package hr.unipu.recipe;

import hr.unipu.plantcomputer.RecipeDTOJava;
import hr.unipu.ui.UiWindowJava;
import javafx.application.Platform;
import javafx.util.Duration;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * RecipeHandler is in charge of running recipes.
 * It provides a service `start_recipe` which takes as input a recipe ID and
 * starts the recipe. It also provides a service `stop_recipe`
 * which takes no inputs and stops the currently running recipe. It defines a
 * parameter `current_recipe` which stores the ID of the currently running recipe.
 */
public class RecipeHandlerJava {

    public static List<VariableInfoJava.EnvironmentVariable> ENVIRONMENTAL_VARIABLES = VariableInfoJava.environment_variables;
    public static List<VariableInfoJava.RecipeVariable> RECIPE_VARIABLES = VariableInfoJava.recipe_variables;

    public static List<VariableInfoJava.EnvironmentVariable> VALID_VARIABLES = ENVIRONMENTAL_VARIABLES;

    public static String RECIPE_START = RECIPE_VARIABLES.get(0).getName();
    public static String RECIPE_END = RECIPE_VARIABLES.get(1).getName();
    public static Map PUBLISHERS = new LinkedHashMap();

    public static Map<String, String> RECIPE_INTERPRETERS = Map.of(
            "simple", "interpret_simple_recipe",
            "flexformat", "interpret_flexformat_recipe"
    );
    private static RecipeDTOJava recipe;
    public static Map<String, Double> setpoints;
    private static long now_time;
    private static long start_time;

    /**
     * Read recipe.
     */
    public static void get_recipe(String recipeName) {
        recipe = RecipeInterpreterJava.loadFlexFormatRecipe(recipeName);
    }


    /**
     * Get the state-related variables of the currently running recipe.
     */
    public static void get_state() {
        Calendar currentTime = Calendar.getInstance();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        dateFormatter.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
        Date now_time_date = currentTime.getTime();

        now_time = RecipeInterpreterJava.unix_time_seconds(now_time_date);

        // If start_time is not defined from previous recipe.
        if (start_time == 0) {
            start_time = now_time - 1;  // Test for recipe in process at first change (1 secs in)
        }

    }


    /**
     * Run recpie. Main entry point.
     * Starts up the node and then waits forever.
     *
     * @param recipeName
     */
    public static void run_recipe(String recipeName) {

        // Initialization of: timezone; static variables date_min, date_max;
        RecipeInterpreterJava.init();

        // Set rate for WHILE LOOP:
        TimeUnit samplingRate = TimeUnit.SECONDS;
        long timeToSleep = 10L;     // Sampling rate for recipe state update.

        // WHILE LOOP:
        RecipeHandlerJava.get_state();
        RecipeHandlerJava.get_recipe(recipeName);

        // - If we have a recipe, process it. Running a recipe is a blocking
        //   operation, so the recipe will stay in this turn of the loop
        //   until it is finished.
        // - Interpret recipe.
        // - Get recipe state and publish it.

        System.out.println("start_time = " + start_time + " now_time = " + now_time);
        setpoints = RecipeInterpreterJava.interpret_flexformat_recipe(recipe, start_time, now_time);

        // Calculate recipe duration.
        List<RecipeDTOJava.Phases> phases = recipe.getPhases();
        List<List<Long>> duration_of_phases_steps = RecipeInterpreterJava.calc_duration_of_phases_steps(phases);
        System.out.println("duration_of_phases_steps: " + duration_of_phases_steps);
        Long recipe_duration = 0L;
        for (List<Long> duration_of_phases_step : duration_of_phases_steps) {
            recipe_duration += duration_of_phases_step.get(0);
        }
        System.out.println("Recipe duration: " + recipe_duration);
        Duration countDownDuration = Duration.hours(recipe_duration.doubleValue());

        Platform.runLater(() -> {
            // Update UI thread from here (if needed).
            UiWindowJava.init(countDownDuration);
            UiWindowJava.countdownTile.setVisible(true);
            UiWindowJava.timer2.start();
        });


        // WHILE LOOP.
        ArduinoHandlerJava arduinoHandler = new ArduinoHandlerJava();
        while (UiWindowJava.isRecipeSelected()) {

            System.out.println("In the recipe WHILE LOOP");

            // FOR LOOP: publish any setpoints that we can.
            for (Map.Entry<String, Double> entry : setpoints.entrySet()) {

                System.out.println(entry.getKey() + " : " + entry.getValue());

                //Send desired setpoints to ArduinoHandler.
                try {
                    arduinoHandler.set_desired_setpoint(entry);
                } catch (Exception exception) {
                    System.out.println(exception.getMessage());
                }

            }

            // Sleep in WHILE LOOP (before new time step).
            try {
                System.out.println("Going to sleep for "
                        + timeToSleep
                        + " seconds");

                samplingRate.sleep(timeToSleep);

                System.out.println("Slept for "
                        + timeToSleep
                        + " seconds");

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        System.out.println("Out of recipe WHILE LOOP");

    }

}
