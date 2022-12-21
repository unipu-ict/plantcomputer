package hr.unipu.recipe;

/**
 * A controller that passes on the measured reading of a sensor as an
 * actuation command.
 */
public class ControllerLinearJava {

    private Double set_point;
    private String variableName;

    public ControllerLinearJava() {
        String sub_name = "cmd";
        String state_sub_name = "state";
        String desired_sub_name = "desired";

    }

    public ControllerLinearJava(String variableName) {
        this();
        this.set_point = null;
        this.variableName = variableName;
    }

    public Double update(Double state) {
        // If setpoint was made null, or was already null, do nothing.
        if (state == null) return null;

        Double res = state;
        return res;
    }

    public void set_point_callback(Double set_point) {
        this.set_point = set_point;
    }
}
