package hr.unipu

import kotlin.jvm.JvmStatic

/**
 * A new main class that doesn't extend from Application.
 * - To trick Java11 that checks javafx.graphics module to be present. If that module is not present,
 *   the launch is aborted. Hence, having the JavaFX libraries as jars on the classpath is not allowed in this case.
 */
object LauncherKotlin {
    @JvmStatic
    fun main(args: Array<String>) {
        PlantComputerApplicationKotlin.main(args)
    }
}