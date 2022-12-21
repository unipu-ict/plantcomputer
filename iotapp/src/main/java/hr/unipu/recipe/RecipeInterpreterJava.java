package hr.unipu.recipe;

import com.fasterxml.jackson.databind.ObjectMapper;
import hr.unipu.plantcomputer.RecipeDTOJava;
import hr.unipu.ui.UiWindowJava;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * RecipeInterpreter interprets loaded recipe (simple and flex format).
 */
public class RecipeInterpreterJava {

    // A threshold to compare time values in seconds.
    private static Integer THRESHOLD = 1;

    // Number of millisecond from EPOCH (normally 1/01/1970 00:00 UTC)
    public static Date EPOCH = Date.from(Instant.ofEpochMilli(0));

    public static long MIN_DATE;    // For verifying time format.
    public static long MAX_DATE;

    public static RecipeDTOJava recipe;


    /**
     * Initialization of:
     * - timezone;
     * - static variables;
     */
    public static void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));

        try {
            Date date_min = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").parse("06-11-2010 07:00:00");
            MIN_DATE = unix_time_seconds(date_min); //1.276.239.600 (seconds).
            Date date_max = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss").parse("06-11-2035 07:00:00");
            MAX_DATE = unix_time_seconds(date_max); //2.065.158.000 (seconds).
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }


    /**
     * Defines unix_time_seconds(dt).
     */
    public static long unix_time_seconds(Date dt) {
        TimeUnit time = TimeUnit.SECONDS;
        return time.toSeconds((dt.getTime() - EPOCH.getTime()) / 1000);
    }


    /**
     * Loading simple recipe.
     */
    public static Map loadSimpleRecipe(String fileName) {
        File file = UiWindowJava.file;
        String path = "";
        if (file == null) {
            ClassLoader loader = RecipeInterpreterJava.class.getClassLoader();
            URL pathURL = loader.getResource("recipes/" + fileName);
            try {
                path = Paths.get(pathURL.toURI()).toString();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            path = file.getPath();
        }

        ObjectMapper mapper = new ObjectMapper();
        Map<?, ?> recipe = null;
        try {
            recipe = mapper.readValue(Paths.get(path).toFile(), Map.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return recipe;
    }


    /**
     * Loading flex format recipe.
     */
    public static RecipeDTOJava loadFlexFormatRecipe(String fileName)  {
        File file = UiWindowJava.file;
        String path = "";
        if (file == null) {
            ClassLoader loader = RecipeInterpreterJava.class.getClassLoader();
            URL pathURL = loader.getResource("recipes/" + fileName);
            try {
                path = Paths.get(pathURL.toURI()).toString();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        } else {
            path = file.getPath();
        }

        ObjectMapper mapper = new ObjectMapper();
        recipe = null;
        try {
            System.out.println("Path: " + Paths.get(path));
            recipe = mapper.readValue(Paths.get(path).toFile(), RecipeDTOJava.class);
            if (recipe.getFormat().equals("phased")) {
                recipe.mapOperations2Phases();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Testing: air_temperature (should be 22) = " + recipe.getPhases().get(0).getStep().getAir_temperature().get(0).getValue());

        return recipe;

    }

    /**
     * Recipe interpreter (for simple recipe).
     * <p>
     * Produces a tuple of `(variable, value)` pairs by building up
     * a recipe state from walking through the recipe keyframes
     */
    public static Map interpret_simple_recipe(Map recipe, long start_time, long now_time) {

        String _id = (String) recipe.get("_id");
        ArrayList operations = (ArrayList) recipe.get("operations");
        System.out.println("_id = " + _id);
        System.out.println("operations = " + operations.toString());

        int lastIndex = operations.size() - 1;
        ArrayList lastOperation = (ArrayList) operations.get(lastIndex);
        long end_time_relative = (Integer) lastOperation.get(0);
        System.out.println("end_time_relative = " + end_time_relative);
        System.out.printf("recipe_handler: interpret_simple_recipe end_time_relative=%s\n", end_time_relative);

        long end_time = start_time + end_time_relative;

        // If start time is at some point in the future beyond the threshold.
        if (start_time - now_time > THRESHOLD)
            System.err.println("Recipes cannot be scheduled for the future.");

        // If there are no recipe operations, immediately start and stop.
        // The recipe.
        if (operations.size() < 1)
            return Map.of(Map.of("recipe_start", _id), Map.of("recipe_end", _id));
        if (now_time >= (end_time + THRESHOLD))
            return Map.of("recipe_end", _id);
        if (Math.abs(now_time - start_time) < THRESHOLD)
            return Map.of("recipe_start", _id);

        long now_relative = (now_time - start_time);

        // Create a state object to accrue recipe setpoint values.
        Map<String, Double> state = new LinkedHashMap<>();  // Keeps the keys in the order they were inserted.
        // Build up state up until now_time (inclusive).
        System.out.printf("recipe_handler: interpret_simple_recipe now=%s\n", now_relative);
        for (int i = 0; i < operations.size(); i++) {
            ArrayList operation = (ArrayList) operations.get(i);
            Integer timestamp = (Integer) operation.get(0);
            String variable = (String) operation.get(1);
            Double value = Double.parseDouble(operation.get(2).toString());
            if (timestamp > now_relative) break;
            state.put(variable, value);
            System.out.printf("recipe_handler: interpret_simple_recipe: %s %s %s\n", timestamp, variable, value);
        }

        return state;
    }


    /**
     * Recipe Interpreter (for flex format recipes) should read a recipe, now_time, start_time, variable and return a value.
     * - Determine the time since the beginning of the recipe.
     * - Determine what is the current step
     * - Calculate the remaining time left in this step.
     * - Look up the value within that step for that variable.
     */
    public static Map interpret_flexformat_recipe(RecipeDTOJava recipe, long start_time, long now_time) {

        String _id = (String) recipe.get_id();
        System.out.println("_id = " + _id);

        List<RecipeDTOJava.Phases> phases = recipe.getPhases();
        System.out.println("phases = " + phases.toString());
        System.out.println("phases size = " + phases.size());

        verify_time_units(now_time);
        verify_time_units(start_time);

        // If start time is at some point in the future beyond the threshold.
        if (start_time - now_time > THRESHOLD)
            System.err.println("Recipes cannot be scheduled for the future.");

        // If there are no recipe operations, immediately start and stop.
        // The recipe.
        if (phases.size() < 1)
            return Map.of(Map.of("recipe_start", _id), Map.of("recipe_end", _id));
        if (Math.abs(now_time - start_time) < THRESHOLD)
            return Map.of("recipe_start", _id);

        String time_units = verify_time_units_are_consistent(phases);
        System.out.println("time_units = " + time_units);

        // Returns a list of the phases and step durations.
        List<List<Long>> duration_of_phases_steps = calc_duration_of_phases_steps(phases);
        System.out.println("duration_of_phases_steps: " + duration_of_phases_steps);

        Long[] ans = calc_phase_and_time_remaining(duration_of_phases_steps,
                start_time,
                now_time,
                time_units);
        Long current_phase_number = ans[0];
        Long duration_in_step = ans[1];
        System.out.println("current_phase_number: " + current_phase_number);
        System.out.println("duration_in_step: " + duration_in_step);

        // Need to create a function to calculate the end time of the recipe.
        RecipeDTOJava.Phases current_phase = phases.get(Math.toIntExact(current_phase_number));

        // Create a state object to accrue recipe setpoint values.
        Map<String, Double> state = new LinkedHashMap<>();

        List<List<?>> items = current_phase.getStep().items();

        int size_of_step = items.size();
        System.out.println("size_of_step (in current phase): " + size_of_step);

        Long start_time_variable = 0L;
        Long end_time_variable = 0L;
        Double value = 0.0;
        String variable = "";

        for (int i = 0; i < size_of_step; i++) {
            int size_of_item = items.get(i).size();
            System.out.println("size_of_item (in current step): " + size_of_item);

            List<?> variable_step_data = items.get(i);
            Map<String, Double> result = determine_value_for_step(variable_step_data, duration_in_step);     //state[variable] = value
            state.putAll(result);
        }

        return state;
    }


    /**
     * Verifies the units for incoming time variables are valid.
     */
    public static void verify_time_units(long time_var) {
        if (MIN_DATE < time_var && time_var < MAX_DATE)
            return;
        else
            System.err.println("Variable time format is not correct. The value should be between {" + MIN_DATE + "} and {" + MAX_DATE + "}, but received: " + time_var);
    }


    /**
     * The time units are stored for each phase in the recipe rather than at the recipe level.
     */
    public static String verify_time_units_are_consistent(List<RecipeDTOJava.Phases> phases) {
        String time_units = "";

        for (RecipeDTOJava.Phases phase : phases) {
            if (phase.getTime_units().isEmpty())
                System.err.println("time_units is missing from the phase. Please check recipe format");
            if (time_units.equals("")) {
                time_units = phase.getTime_units();
            } else if (!time_units.equals(phase.getTime_units())) {
                System.err.println("time_units are not consistent across each phase in the recipe. {" + time_units + "} != {" + phase.getTime_units() + "}");
            }
        }

        return time_units;
    }


    /**
     * Given the time within a step, what value is expected.
     */
    public static Map<String, Double> determine_value_for_step(List<?> variable_step_data, Long duration_in_step) {

        Long start_time = 0L;
        Long end_time = 0L;
        String variable = "";
        Double value = 0.0;
        Map<String, Double> ans = new LinkedHashMap<>();

        for (int j = 0; j < variable_step_data.size(); j++) {
            Object item = variable_step_data.get(j);

            if (item.getClass() == RecipeDTOJava.Phases.Step.AirTemperature.class) {
                start_time = ((RecipeDTOJava.Phases.Step.AirTemperature) item).getStart_time();
                end_time = ((RecipeDTOJava.Phases.Step.AirTemperature) item).getEnd_time();
                variable = ((RecipeDTOJava.Phases.Step.AirTemperature) item).getName();
                value = ((RecipeDTOJava.Phases.Step.AirTemperature) item).getValue();
            }
            if (item.getClass() == RecipeDTOJava.Phases.Step.LightIntensityBlue.class) {
                start_time = ((RecipeDTOJava.Phases.Step.LightIntensityBlue) item).getStart_time();
                end_time = ((RecipeDTOJava.Phases.Step.LightIntensityBlue) item).getEnd_time();
                variable = ((RecipeDTOJava.Phases.Step.LightIntensityBlue) item).getName();
                value = ((RecipeDTOJava.Phases.Step.LightIntensityBlue) item).getValue();
            }
            if (item.getClass() == RecipeDTOJava.Phases.Step.LightIntensityRed.class) {
                start_time = ((RecipeDTOJava.Phases.Step.LightIntensityRed) item).getStart_time();
                end_time = ((RecipeDTOJava.Phases.Step.LightIntensityRed) item).getEnd_time();
                variable = ((RecipeDTOJava.Phases.Step.LightIntensityRed) item).getName();
                value = ((RecipeDTOJava.Phases.Step.LightIntensityRed) item).getValue();
            }
            if (item.getClass() == RecipeDTOJava.Phases.Step.LightIlluminance.class) {
                start_time = ((RecipeDTOJava.Phases.Step.LightIlluminance) item).getStart_time();
                end_time = ((RecipeDTOJava.Phases.Step.LightIlluminance) item).getEnd_time();
                variable = ((RecipeDTOJava.Phases.Step.LightIlluminance) item).getName();
                value = ((RecipeDTOJava.Phases.Step.LightIlluminance) item).getValue();
            }
            if (item.getClass() == RecipeDTOJava.Phases.Step.Nutrient_FloraDuoA.class) {
                start_time = ((RecipeDTOJava.Phases.Step.Nutrient_FloraDuoA) item).getStart_time();
                end_time = ((RecipeDTOJava.Phases.Step.Nutrient_FloraDuoA) item).getEnd_time();
                variable = ((RecipeDTOJava.Phases.Step.Nutrient_FloraDuoA) item).getName();
                value = ((RecipeDTOJava.Phases.Step.Nutrient_FloraDuoA) item).getValue();
            }
            if (item.getClass() == RecipeDTOJava.Phases.Step.Nutrient_FloraDuoB.class) {
                start_time = ((RecipeDTOJava.Phases.Step.Nutrient_FloraDuoB) item).getStart_time();
                end_time = ((RecipeDTOJava.Phases.Step.Nutrient_FloraDuoB) item).getEnd_time();
                variable = ((RecipeDTOJava.Phases.Step.Nutrient_FloraDuoB) item).getName();
                value = ((RecipeDTOJava.Phases.Step.Nutrient_FloraDuoB) item).getValue();
            }

            if (start_time <= duration_in_step && duration_in_step <= end_time) {
                System.out.println("variable: " + variable + ", value: " + value + ", start_time: " + start_time + ", end_time: " + end_time);
                ans.put(variable, value);
                return ans;
            }

        }

        return ans;
    }


    /**
     * Determines the total duration of this step. Normally it is 24 hours.
     */
    public static Long calculate_max_duration_from_step(RecipeDTOJava.Phases.Step step) {
        Long max_time = 0L;

        Method[] allMethods = step.getClass().getMethods();
        Method[] getMethods = (Method[]) Arrays.stream(allMethods)
                .filter(method -> method.toString().contains("get"))
                .filter(method -> !method.toString().contains("getClass"))
                .toArray(Method[]::new);

        List<List<?>> items = step.items();
        //System.out.println("Number of items: " + items.size());

        int size_of_step = items.size();
        System.out.println("size_of_step: " + size_of_step);
        Long start_time = 0L;
        Long end_time = 0L;
        for (int i = 0; i < size_of_step; i++) {
            int size_of_item = items.get(i).size();
            for (int j = 0; j < size_of_item; j++) {
                Object item = items.get(i).get(j);
                if (item.getClass() == RecipeDTOJava.Phases.Step.AirTemperature.class) {
                    start_time = ((RecipeDTOJava.Phases.Step.AirTemperature) item).getStart_time();
                    end_time = ((RecipeDTOJava.Phases.Step.AirTemperature) item).getEnd_time();
                }
                if (item.getClass() == RecipeDTOJava.Phases.Step.LightIntensityBlue.class) {
                    start_time = ((RecipeDTOJava.Phases.Step.LightIntensityBlue) item).getStart_time();
                    end_time = ((RecipeDTOJava.Phases.Step.LightIntensityBlue) item).getEnd_time();
                }
                if (item.getClass() == RecipeDTOJava.Phases.Step.LightIntensityRed.class) {
                    start_time = ((RecipeDTOJava.Phases.Step.LightIntensityRed) item).getStart_time();
                    end_time = ((RecipeDTOJava.Phases.Step.LightIntensityRed) item).getEnd_time();
                }
                if (item.getClass() == RecipeDTOJava.Phases.Step.LightIntensityRed.class) {
                    start_time = ((RecipeDTOJava.Phases.Step.LightIntensityRed) item).getStart_time();
                    end_time = ((RecipeDTOJava.Phases.Step.LightIntensityRed) item).getEnd_time();
                }
                if (item.getClass() == RecipeDTOJava.Phases.Step.Nutrient_FloraDuoA.class) {
                    start_time = ((RecipeDTOJava.Phases.Step.Nutrient_FloraDuoA) item).getStart_time();
                    end_time = ((RecipeDTOJava.Phases.Step.Nutrient_FloraDuoA) item).getEnd_time();
                }
                if (item.getClass() == RecipeDTOJava.Phases.Step.Nutrient_FloraDuoB.class) {
                    start_time = ((RecipeDTOJava.Phases.Step.Nutrient_FloraDuoB) item).getStart_time();
                    end_time = ((RecipeDTOJava.Phases.Step.Nutrient_FloraDuoB) item).getEnd_time();
                }
                //System.out.println("start_time: " + start_time);
                //System.out.println("end_time: " + end_time);

                if (start_time > end_time)
                    System.err.println("Start_time is after end time.");
                else if (max_time < Math.max(start_time, end_time))
                    max_time = Math.max(start_time, end_time);
            }
        }
        System.out.println("max_time = " + max_time);
        return max_time;
    }


    /**
     * Returns a list with the duration of the step and the entire phase.
     */
    public static List<List<Long>> calc_duration_of_phases_steps(List<RecipeDTOJava.Phases> phases) {
        List<List<Long>> duration_of_phases_steps = new ArrayList<>();

        for (RecipeDTOJava.Phases phase : phases) {
            Integer cycles = phase.getCycles();
            Long max_duration = calculate_max_duration_from_step(phase.getStep());
            duration_of_phases_steps.add(List.of(cycles * max_duration, max_duration));
        }

        return duration_of_phases_steps;
    }


    /**
     * Converts a number duration from Seconds into the units specified in the options(units variable).
     */
    public static Long convert_duration_units(Long duration, String units) {

        Map<String, Double> divider = Map.of("hours", 3600.0,
                "days", 3600 * 24.0,
                "milliseconds", 0.001,
                "ms", 0.001,
                "seconds", 1.0);
        if (divider.get("units") == null)
            System.err.println("Error time_units in recipe are not available. Valid options are: days, hours, milliseconds, ms");

        Long duration_in_hours = (long) (duration / divider.get(units));
        return duration_in_hours;
    }

    /**
     * Calculates how far along the recipe is in progress given the start_time and the time it is now (aka now_time).
     */
    public static Long[] calc_phase_and_time_remaining(List<List<Long>> duration_of_phases_steps, Long start_time, Long now_time, String time_units) {

        Long current_phase_number = 0L;
        Long duration_in_phase = 0L;
        Long[] ans = new Long[2];

        Long time_elapsed = now_time - start_time;
        System.out.println("time_elapsed [in seconds]: " + time_elapsed);

        time_elapsed = convert_duration_units(time_elapsed, time_units);
        System.out.println("time_elapsed [in " + time_units + "]: " + time_elapsed);

        for (int i = 0; i < duration_of_phases_steps.size(); i++) {
            Long total_duration = duration_of_phases_steps.get(i).get(0);
            Long step_duration = duration_of_phases_steps.get(i).get(1);
            System.out.println("total_duration: " + total_duration);
            System.out.println("step_duration: " + step_duration);

            if (time_elapsed > total_duration)
                time_elapsed -= total_duration;
            else {
                duration_in_phase = time_elapsed % step_duration;
                current_phase_number = (long) i;
                break;
            }
        }

        ans[0] = current_phase_number;
        ans[1] = duration_in_phase;
        return ans;
    }


}
