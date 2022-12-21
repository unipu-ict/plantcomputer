package hr.unipu.recipe;

/**
 * Direct Controller
 * This node controls the variables which do not have any feedback and the
 * output setting is just set once and maintained.
 */
public class ControllerDirectJava {

    private Double set_point;
    private String variableName;

    public ControllerDirectJava() {
        String sub_name = "cmd";
        String state_sub_name = "state";
        String desired_sub_name = "desired";

    }

    public ControllerDirectJava(String variableName) {
        this();
        this.set_point = null;
        this.variableName = variableName;
    }

    public Double update(Double state) {
        // If setpoint was made null, or was already null, do nothing.
        if (state == null) return null;

        Double error = this.set_point - state;
        Double res = error;
        return res;
    }

    public void set_point_callback(Double set_point) {
        this.set_point = set_point;
    }

}
