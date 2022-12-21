package hr.unipu.recipe

/**
 * Proportional-Integral-Derivative (PID) controller.
 * The controller gains are passed in as parameters `Kp`, `Ki`, and `Kd`. It also
 * accepts an `upper_limit` and `lower_limit` to bound the control effort output.
 * `windup_limit` defines a limit for the integrator of the control loop.
 * `deadband_width` can be used to apply a deadband to the control effors.
 * Specifically, commands with absolute value less than `deadband_width` will be
 * changed to 0.
 */
class ControllerPID() {
    private var Kp = 0.0
    private var Ki = 0.0
    private var Kd = 0.0
    private var upper_limit = 1.0
    private var lower_limit = 1.0
    private var windup_limit = 1000.0
    private var deadband_width = 0.0
    private var set_point: Double = 0.0
    private var last_error: Double = 0.0
    private var integrator: Double = 0.0
    private var variableName = ""

    constructor(
        Kp: Double,
        Ki: Double,
        Kd: Double,
        upper_limit: Double,
        lower_limit: Double,
        windup_limit: Double,
        deadband_width: Double,
        variableName: String
    ) : this()
    {
        this.Kp = Kp
        this.Ki = Ki
        this.Kd = Kd
        this.upper_limit = upper_limit
        this.lower_limit = lower_limit
        this.windup_limit = windup_limit
        this.deadband_width = deadband_width
        set_point = 0.0
        last_error = 0.0
        integrator = 0.0
        this.variableName = variableName
    }

    fun update(state: Double): Double {

        // If setpoint was made null, or was already null, do nothing.
        if (state == 0.0) return 0.0
        val error = set_point - state
        if (Math.abs(error) < deadband_width) return 0.0
        val p_value = Kp * error
        val d_value = Kd * (error - last_error)
        last_error = error
        integrator = integrator + error
        integrator = Math.max(-windup_limit, Math.min(windup_limit, integrator))
        val i_value = Ki * integrator
        var res = p_value + i_value + d_value
        res = Math.min(upper_limit, Math.max(lower_limit, res))
        return res
    }

    fun state_callback(state: Double) {
        val cmd = update(state) ?: return
    }

    fun set_point_callback(set_point: Double) {
        this.set_point = set_point
    }

    /**
     * When we receive the recipe end message, reset this PID controller to its default values.
     * This disables the set point so the controller will just idle until it is set by a new recipe.
     */
    fun recipe_end_callback() {
        set_point = 0.0
    }

    init {
        val param_names: List<*> =
            java.util.List.of("Kp", "Ki", "Kd", "lower_limit", "upper_limit", "windup_limit", "deadband_width")
        val sub_name = "cmd"
        val state_sub_name = "state"
        val desired_sub_name = "desired"
    }
}