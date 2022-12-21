package hr.unipu;

/**
 * A new main class that doesn't extend from Application.
 * - To trick Java11 that checks javafx.graphics module to be present. If that module is not present,
 *   the launch is aborted. Hence, having the JavaFX libraries as jars on the classpath is not allowed in this case.
 */
public class LauncherJava {

    public static void main(String[] args) {
        PlantComputerApplicationJava.main(args);
    }

}
