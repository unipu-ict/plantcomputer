package hr.unipu.recipe

/**
 * Direct Controller
 * This node controls the variables which do not have any feedback and the
 * output setting is just set once and maintained.
 */
class ControllerDirect() {
    private var set_point: Double = 0.0
    private var variableName: String = ""

    constructor(variableName: String) : this()
    {
        set_point = 0.0
        this.variableName = variableName
    }

    fun update(state: Double): Double {
        // If setpoint was made null, or was already null, do nothing.
        return if (state == 0.0) 0.0 else set_point - state
    }

    fun set_point_callback(set_point: Double) {
        this.set_point = set_point
    }

    init {
        val sub_name = "cmd"
        val state_sub_name = "state"
        val desired_sub_name = "desired"
    }
}