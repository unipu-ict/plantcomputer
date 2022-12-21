package hr.unipu.recipe

import hr.unipu.ui.UiWindow
import java.util.LinkedHashMap
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.TimeZone
import java.util.concurrent.TimeUnit
import java.lang.Runnable
import java.lang.InterruptedException
import java.time.Instant
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.IOException

/**
 * A controller that passes on the measured reading of a sensor as an
 * actuation command.
 */
class ControllerLinear() {
    private var set_point: Double = 0.0
    private var variableName: String = ""

    constructor(variableName: String) : this()
    {
        set_point = 0.0
        this.variableName = variableName
    }

    fun update(state: Double): Double {
        // If setpoint was made null, or was already null, do nothing.
        return state
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