package hr.unipu.ui

import hr.unipu.event.EventManager
import hr.unipu.client.MqttClientConnection
import eu.hansolo.tilesfx.tools.FlowGridPane
import hr.unipu.event.EventListener
import javafx.scene.layout.VBox
import eu.hansolo.tilesfx.Tile
import javafx.scene.layout.HBox
import hr.unipu.plantcomputer.PlantComputerAction
import hr.unipu.plantcomputer.PlantComputerCommand
import hr.unipu.PlantComputerApplicationKotlin
import java.time.Instant
import com.influxdb.client.domain.WritePrecision
import javafx.animation.AnimationTimer
import javafx.beans.property.DoubleProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.core.JsonProcessingException
import com.influxdb.client.write.Point
import eu.hansolo.tilesfx.TileBuilder
import javafx.scene.text.TextAlignment
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.CornerRadii
import javafx.beans.property.SimpleDoubleProperty
import javafx.collections.ObservableList
import javafx.collections.FXCollections
import javafx.stage.Modality
import javafx.stage.FileChooser
import hr.unipu.recipe.RecipeHandler
import eu.hansolo.tilesfx.events.SwitchEvent
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.ListView
import javafx.scene.image.Image
import javafx.scene.paint.Color
import javafx.scene.paint.Stop
import javafx.stage.Stage
import javafx.util.Duration
import java.io.File
import java.lang.Boolean
import java.lang.Exception
import java.util.*

/**
 * UI for Plant Computer.
 */
class UiWindow(eventManager: EventManager, mqttClientConnection: MqttClientConnection?)
    : FlowGridPane(NO_OF_COLS, NO_OF_ROWS), EventListener {
    private val eventManager: EventManager
    private val home: VBox
    private val sensorsVBox1: VBox
    private val sensorsVbox2: VBox
    private val actuatorsVbox1: VBox
    private val actuatorsVbox2: VBox
    private val logs: VBox
    private val recipes: VBox
    private val imageTile: Tile
    private val btGrowLight: Tile
    private val btHumidifier: Tile
    private val btCoolingFan: Tile
    private val btChamberFan: Tile
    private val btHeater: Tile
    private val btWaterCirculationPump: Tile
    private val temperatureTile: Tile
    private val recipesBoardTile: Tile
    val countdownHbox: HBox
    val countdownVbox1: VBox
    val countdownVbox2: VBox
    private val sparkLineSensor1: Tile
    private val sparkLineSensor2: Tile
    private val sparkLineSensor3: Tile
    private val sparkLineSensor4: Tile
    private val sparkLineSensor5: Tile
    private val sparkLineSensor6: Tile
    private val barGaugeSensor1: Tile
    private val barGaugeSensor2: Tile
    private val barGaugeSensor3: Tile
    private val barGaugeSensor4: Tile
    private val barGaugeSensor5: Tile
    private val barGaugeSensor6: Tile
    private val cameraTile: Tile
    private val customTile: Tile
    var target1 = 27.0      // [°C]
    var target2 = 60.0      // [%]
    var target3 = 1.5       // [mS/cm]
    var target4 = 6.4       // []
    var target5 = 18.33     // [°C]
    var target6 = 15000.0   // [lux]

    companion object {
        private const val TILE_WIDTH = 150.0
        private const val TILE_HEIGHT = 150.0
        private const val NO_OF_COLS = 6
        private const val NO_OF_ROWS = 5
        private const val SPACING = 5
        private var mqttClientConnection: MqttClientConnection? = null
        private var blockSending = false
        var selectedPlantComputerAction = PlantComputerAction.UNDEFINED
        var listActuatorCommands: MutableList<PlantComputerCommand> = ArrayList()
        var listReadingCommands: MutableList<PlantComputerCommand> = ArrayList()
        var listStateReadings: MutableList<PlantComputerCommand> = ArrayList()
        private val RND = Random()
        var file: File? = null

        // Countdown timer.
        lateinit var countdownTile: Tile
        private const val SECONDS_PER_DAY = 86400
        private const val SECONDS_PER_HOUR = 3600
        private const val SECONDS_PER_MINUTE = 60
        private lateinit var days: Tile
        private lateinit var hours: Tile
        private lateinit var minutes: Tile
        private lateinit var seconds: Tile
        private lateinit var duration: Duration

        // Climate recipes.
        var isRecipeSelected = false
            private set
        lateinit var task: TimerTask

        private lateinit var textTile: Tile
        private val logText = LinkedList<String>()
        private lateinit var timer1: AnimationTimer
        private var lastTimerCall1: Long = 0L
        lateinit var timer2: AnimationTimer
        private var lastTimerCall2: Long = 0L
        lateinit var value1 : DoubleProperty    // SATM 1
        lateinit var value2 : DoubleProperty    // SAHU 1
        lateinit var value3 : DoubleProperty    // SWEC 1
        lateinit var value4 : DoubleProperty    // SWPH 1
        lateinit var value5 : DoubleProperty    // SWTM 1
        lateinit var value6 : DoubleProperty    // SLIN 1


        /**
         * Send a message to Mosquitto if a new action and/or different parameters are selected.
         */
        fun sendMessageForActuators() {
            if (blockSending) {
                // Avoid sending the same command to Mosquitto again to avoid infinite loops.
                return
            }

            // Create single actuator command.
            val plantComputerCommand = PlantComputerCommand(
                selectedPlantComputerAction
            )

            // Sending message command for actuators to MQTT broker.
            if (mqttClientConnection != null) {
                val isMqttClientConnected = mqttClientConnection!!.mqttClient.isConnected
                if (isMqttClientConnected) {
                    val mapper = ObjectMapper()
                    try {
                        // Sending list of commands for actuators.
                        mqttClientConnection!!.sendMessage(
                            mapper.writeValueAsString(listActuatorCommands),
                            "plantComputerCommand4Actuators"
                        )
                    } catch (e: JsonProcessingException) {
                        e.printStackTrace()
                    }
                }
            }

            // Adding short log to UI.
            logText.add(plantComputerCommand.toStringCommand())
            if (logText.size > 5) logText.remove() // FIFO, remove the oldest.
            val logTextReversed = logText.clone() as LinkedList<*>
            Collections.reverse(logTextReversed)
            textTile.description = logTextReversed.toString()
                .replace("[", "")
                .replace("]", "")
                .replace(",", "")
        }

        /**
         * Send a message to Mosquitto for reading sensors' and actuators' states.
         */
        private fun sendMessageForReadingStates() {
            if (blockSending) {
                // Avoid sending the same command to Mosquitto again to avoid infinite loops.
                return
            }

            // Sending message command for actuators to MQTT broker.
            if (mqttClientConnection != null) {
                val isMqttClientConnected = mqttClientConnection!!.mqttClient!!.isConnected
                if (isMqttClientConnected) {
                    val mapper = ObjectMapper()
                    try {
                        // Sending list of commands for actuators.
                        mqttClientConnection!!.sendMessage(
                            mapper.writeValueAsString(listReadingCommands),
                            "plantComputerCommand4Sensors"
                        )
                    } catch (e: JsonProcessingException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        /**
         * Send a message to Mosquitto of reading sensors and actuators state - if simulationMode==true.
         */
        private fun sendMessageStateReadings() {
            if (blockSending) {
                // Avoid sending the same command to Mosquitto again to avoid infinite loops.
                return
            }

            // Sending message "test" readings for sensors and actuators to MQTT broker.
            if (mqttClientConnection != null) {
                val isMqttClientConnected = mqttClientConnection!!.mqttClient.isConnected
                if (isMqttClientConnected) {
                    val mapper = ObjectMapper()
                    try {
                        // Sending list of readings of sensors.
                        mqttClientConnection!!.sendMessage(
                            mapper.writeValueAsString(listStateReadings),
                            "plantComputerState"
                        )
                    } catch (e: JsonProcessingException) {
                        e.printStackTrace()
                    }
                }
            }
        }

        fun init(countDownTime: Duration?) {
            duration = countDownTime!!
            lastTimerCall2 = System.nanoTime()
            //timer2.stop() // Reset timer.
            timer2 = object : AnimationTimer() {
                override fun handle(now: Long) {
                    if (now > lastTimerCall2 + 1000000000L) {
                        duration = duration.subtract(Duration.seconds(1.0))
                        val remainingSeconds = duration.toSeconds().toInt()
                        val d = remainingSeconds / SECONDS_PER_DAY
                        val h = remainingSeconds % SECONDS_PER_DAY / SECONDS_PER_HOUR
                        val m = remainingSeconds % SECONDS_PER_DAY % SECONDS_PER_HOUR / SECONDS_PER_MINUTE
                        val s = remainingSeconds % SECONDS_PER_DAY % SECONDS_PER_HOUR % SECONDS_PER_MINUTE
                        if (d == 0 && h == 0 && m == 0 && s == 0) {
                            timer2.stop()
                        }
                        days.description = Integer.toString(d)
                        hours.description = Integer.toString(h)
                        minutes.description = String.format("%02d", m)
                        seconds.description = String.format("%02d", s)
                        lastTimerCall2 = now
                    }
                }
            }
        }

        private fun createTile(TITLE: String, TEXT: String): Tile {
            return TileBuilder.create().skinType(Tile.SkinType.CHARACTER)
                .prefSize(TILE_WIDTH / 5, TILE_HEIGHT / 5)
                .title(TITLE)
                .textSize(Tile.TextSize.BIGGER)
                .titleAlignment(TextAlignment.CENTER)
                .description(TEXT)
                .build()
        }
    }

    // Instantiations of all tile nodes.
    init {
        super.setHgap(SPACING.toDouble())
        super.setVgap(SPACING.toDouble())
        super.setAlignment(Pos.TOP_LEFT)
        super.setCenterShape(true)
        super.setPadding(Insets(5.0))
        //super.setPrefSize(1280, 720);    // original: 800x600, best: 1280x720 (16:9)
        super.setBackground(Background(BackgroundFill(Color.web("#101214"), CornerRadii.EMPTY, Insets.EMPTY)))
        Companion.mqttClientConnection = mqttClientConnection
        this.eventManager = eventManager
        eventManager.addListener(this) // Add listener to UI Window.
        value1 = SimpleDoubleProperty(0.0)
        value2 = SimpleDoubleProperty(0.0)
        value3 = SimpleDoubleProperty(0.0)
        value4 = SimpleDoubleProperty(0.0)
        value5 = SimpleDoubleProperty(0.0)
        value6 = SimpleDoubleProperty(0.0)


        /*
         * ---HOME group---
         */

        // Home group pane.
        home = VBox()

        // Image tile.
        imageTile = TileBuilder.create()
            .skinType(Tile.SkinType.IMAGE)
            .prefSize(TILE_WIDTH, TILE_HEIGHT * 1)
            .title("Plant Computer in Java/Kotlin")
            .titleAlignment(TextAlignment.CENTER)
            .image(Image(UiWindow::class.java.getResourceAsStream("/unipu-lat.png")))
            .imageMask(Tile.ImageMask.NONE)
            .text("ver.1.0")
            .textAlignment(TextAlignment.CENTER)
            .build()

        temperatureTile = TileBuilder.create().skinType(Tile.SkinType.FIRE_SMOKE)
            .prefSize(TILE_WIDTH, TILE_HEIGHT)
            .title("Air Temperature".toUpperCase()) //.text("Air temperature")
            .unit("\u00b0C")
            .threshold(target1 * 2) // triggers the fire and smoke effect
            .decimals(1)
            .animated(true)
            .build()
        temperatureTile.valueProperty().bind(value1)
        temperatureTile.id = "temperature-tile"


        val recipesList: ObservableList<Any?> = FXCollections.observableArrayList<Any>()
        val recipesListView: ListView<*> = ListView<Any?>(recipesList)
        recipesListView.style = "-fx-control-inner-background: black;"
        val addRecipeButton = Button("Add recipe".toUpperCase())
        addRecipeButton.onAction = EventHandler { e: ActionEvent? ->
            val newWindow = Stage()
            newWindow.initModality(Modality.NONE)
            val fileChooser = FileChooser()
            fileChooser.title = "Open JSON Recipe File"
            fileChooser.initialDirectory = File(System.getProperty("user.dir"), "/build/resources/main/recipes")
            fileChooser.extensionFilters.addAll(
                FileChooser.ExtensionFilter("JSON recipes", "*.json")
            )
            file = fileChooser.showOpenDialog(newWindow)
            if (file != null) {
                recipesList.add(file!!.name)
            }
        }
        addRecipeButton.id = "recipe-button"
        recipesListView.selectionModel.selectedItemProperty().addListener { observable, oldValue, newValue ->
            isRecipeSelected = newValue != null
            task = object : TimerTask() {
                override fun run() {
                    println("Selected recipe: $newValue")

                    // Stop task if unselected.
                    if (!isRecipeSelected) {
                        task.cancel()
                        task.cancel()
                        println("Task canceled.")
                        countdownTile.isVisible = false
                        return
                    }

                    // Set Recipe counter before, since is in while loop after.
                    Platform.runLater {}

                    // Run recipe.
                    println("Running recipe handler...")
                    RecipeHandler.run_recipe(newValue as String)
                }
            }
            Thread(task).start()
        }
        recipes = VBox()
        recipes.children.addAll(recipesListView, addRecipeButton)


        recipesBoardTile = TileBuilder.create()
            .skinType(Tile.SkinType.CUSTOM)
            .prefSize(TILE_WIDTH, TILE_HEIGHT)
            .title("Recipes".toUpperCase())
            .text("Click recipe to run, CTRL+click to stop")
            .graphic(recipes)
            .roundedCorners(true)
            .build()
        recipesBoardTile.id = "recipes-board-tile"


        countdownHbox = HBox()
        countdownVbox1 = VBox()
        countdownVbox2 = VBox()
        days = createTile("DAYS", "0")
        hours = createTile("HOURS", "0")
        minutes = createTile("MINUTES", "0")
        seconds = createTile("SECONDS", "0")
        countdownVbox1.children.addAll(days, hours)
        countdownVbox2.children.addAll(minutes, seconds)
        countdownHbox.children.addAll(countdownVbox1, countdownVbox2)
        countdownHbox.padding = Insets(10.0)
        countdownTile = TileBuilder.create()
            .skinType(Tile.SkinType.CUSTOM)
            .prefSize(TILE_WIDTH, TILE_HEIGHT)
            .title("Recipe Countdown Timer".toUpperCase())
            .graphic(countdownHbox)
            .roundedCorners(true)
            .build()
        countdownTile.isVisible = false
        countdownTile.id = "countdown-tile"
        home.children.addAll(
            imageTile,
            temperatureTile,
            countdownTile,
            recipesBoardTile
        )
        home.spacing = SPACING.toDouble()


        /*
         * ---SENSORS group---
         */

        // Sensors group pane 1.
        sensorsVBox1 = VBox()

        // Air Temperature [°C], target 18.33°C.
        sparkLineSensor1 = TileBuilder.create()
            .skinType(Tile.SkinType.SPARK_LINE)
            .prefSize(TILE_WIDTH * 3, TILE_HEIGHT)
            .title("Air Temperature".toUpperCase())
            .unit("\u00b0C")
            .valueVisible(true)
            .decimals(1)
            .gradientStops(
                Stop(0.00000, Color.TRANSPARENT),
                Stop(0.00001, Color.web("#3552a0")),
                Stop(0.09090, Color.web("#456acf")),
                Stop(0.27272, Color.web("#45a1cf")),
                Stop(0.36363, Color.web("#30c8c9")),
                Stop(0.45454, Color.web("#30c9af")),
                Stop(0.50909, Color.web("#56d483")),
                Stop(0.72727, Color.web("#9adb49")),
                Stop(0.81818, Color.web("#efd750")),
                Stop(0.90909, Color.web("#ef9850")),
                Stop(1.00000, Color.web("#ef6050"))
            )
            .minValue(0.0)
            .maxValue(target1 * 2)
            .strokeWithGradient(true)
            .fixedYScale(true)
            .build()
        sparkLineSensor1.valueProperty().bind(value1)
        sparkLineSensor1.id = "spark-line-sensor1"


        // Air Humidity [%], target 60%.
        sparkLineSensor2 = TileBuilder.create()
            .skinType(Tile.SkinType.SPARK_LINE)
            .prefSize(TILE_WIDTH * 3, TILE_HEIGHT)
            .title("Air Humidity".toUpperCase())
            .unit("%")
            .decimals(1)
            .gradientStops(
                Stop(0.0, Tile.RED),  // Too dry.
                Stop(0.4, Tile.GREEN),
                Stop(1.0, Tile.BLUE)
            ) // Too wet.
            .strokeWithGradient(true)
            .minValue(0.0)
            .maxValue(target2 * 2.0)
            .fixedYScale(true)
            .build()
        sparkLineSensor2.valueProperty().bind(value2)
        sparkLineSensor2.id = "spark-line-sensor2"


        // Water Electrical Conductivity [mS/cm], target 1.5mS/cm.
        sparkLineSensor3 = TileBuilder.create()
            .skinType(Tile.SkinType.SPARK_LINE)
            .prefSize(TILE_WIDTH * 3, TILE_HEIGHT)
            .title("Water EC".toUpperCase())
            .unit("mS/cm")
            .decimals(1)
            .gradientStops(
                Stop(0.0, Tile.GRAY),  // Too low nutrient solution.
                Stop(0.5, Tile.GREEN),
                Stop(1.0, Tile.RED)
            ) // Too much nutrient solution.
            .strokeWithGradient(true)
            .minValue(0.0)
            .maxValue(target3 * 2)
            .fixedYScale(true)
            .build()
        sparkLineSensor3.valueProperty().bind(value3)
        sparkLineSensor3.id = "spark-line-sensor3"


        // Water pH [], target 6.4;
        sparkLineSensor4 = TileBuilder.create()
            .skinType(Tile.SkinType.SPARK_LINE)
            .prefSize(TILE_WIDTH * 3, TILE_HEIGHT)
            .title("Water pH".toUpperCase())
            .unit(" ")
            .decimals(1)
            .gradientStops(
                Stop(0.0, Tile.RED),  // Too acid.
                Stop(0.5, Tile.GREEN),
                Stop(1.0, Tile.BLUE)
            ) // Too alkaline.
            .strokeWithGradient(true)
            .minValue(0.0)
            .maxValue(target4 * 2)
            .fixedYScale(true)
            .build()
        sparkLineSensor4.valueProperty().bind(value4)
        sparkLineSensor4.id = "spark-line-sensor4"


        // Water temperature [C], , target 18.33°C.
        sparkLineSensor5 = TileBuilder.create()
            .skinType(Tile.SkinType.SPARK_LINE)
            .prefSize(TILE_WIDTH * 3, TILE_HEIGHT)
            .title("Water Temperature".toUpperCase())
            .unit("\u00b0C")
            .decimals(1)
            .gradientStops(
                Stop(0.0, Tile.BLUE),  // Water temperature too low.
                Stop(0.5, Tile.GREEN),
                Stop(1.0, Tile.RED)
            ) // Water temperature too high.
            .strokeWithGradient(true)
            .minValue(0.0)
            .maxValue(target5 * 2)
            .fixedYScale(true)
            .build()
        sparkLineSensor5.valueProperty().bind(value5)
        sparkLineSensor5.id = "spark-line-sensor5"


        // Light Intensity [lux].
        sparkLineSensor6 = TileBuilder.create()
            .skinType(Tile.SkinType.SPARK_LINE)
            .prefSize(TILE_WIDTH * 3, TILE_HEIGHT)
            .title("Light Intensity".toUpperCase())
            .unit("lux")
            .decimals(0)
            .gradientStops(
                Stop(0.0, Tile.BLUE),  // Light intensity too low.
                Stop(0.5, Tile.GREEN),
                Stop(1.0, Tile.RED)
            ) // Light intensity too high.
            .strokeWithGradient(true)
            .minValue(0.0)
            .maxValue(target6 * 2)
            .fixedYScale(true)
            .build()
        sparkLineSensor6.valueProperty().bind(value6)
        sparkLineSensor6.id = "spark-line-sensor6"
        sensorsVBox1.children.addAll(
            sparkLineSensor1,
            sparkLineSensor2,
            sparkLineSensor3,
            sparkLineSensor4,
            sparkLineSensor5,
            sparkLineSensor6
        )
        sensorsVBox1.spacing = SPACING.toDouble()


        // Sensors group pane 2.
        sensorsVbox2 = VBox()
        barGaugeSensor1 = TileBuilder.create()
            .skinType(Tile.SkinType.BAR_GAUGE)
            .prefSize(TILE_WIDTH, TILE_HEIGHT)
            .title("Air Temperature".toUpperCase())
            .text("\"SATM 1\"")
            .unit("\u00b0C")
            .textVisible(true)
            .value(0.0)
            .valueVisible(true)
            .gradientStops(
                Stop(0.00000, Color.TRANSPARENT),
                Stop(0.00001, Color.web("#3552a0")),
                Stop(0.09090, Color.web("#456acf")),
                Stop(0.27272, Color.web("#45a1cf")),
                Stop(0.36363, Color.web("#30c8c9")),
                Stop(0.45454, Color.web("#30c9af")),
                Stop(0.50909, Color.web("#56d483")),
                Stop(0.72727, Color.web("#9adb49")),
                Stop(0.81818, Color.web("#efd750")),
                Stop(0.90909, Color.web("#ef9850")),
                Stop(1.00000, Color.web("#ef6050"))
            )
            .strokeWithGradient(true)
            .threshold(target1)
            .thresholdVisible(true)
            .minValue(0.0)
            .maxValue(target1 * 2)
            .decimals(1)
            .tickLabelDecimals(1)
            .animated(true)
            .build()
        barGaugeSensor1.valueProperty().bind(value1)
        barGaugeSensor1.id = "bar-gauge-sensor1"


        barGaugeSensor2 = TileBuilder.create()
            .skinType(Tile.SkinType.BAR_GAUGE)
            .prefSize(TILE_WIDTH, TILE_HEIGHT)
            .title("Air Humidity".toUpperCase())
            .text("\"SAHU 1\"")
            .unit("%")
            .textVisible(true)
            .value(0.0)
            .gradientStops(
                Stop(0.0, Tile.RED),  // Too dry.
                Stop(0.4, Tile.GREEN),
                Stop(1.0, Tile.BLUE)
            ) // Too wet.
            .strokeWithGradient(true)
            .minValue(0.0) // 0%
            .maxValue(100.0) // 100%
            .threshold(target2)
            .thresholdVisible(true)
            .decimals(1)
            .tickLabelDecimals(1)
            .animated(true)
            .build()
        barGaugeSensor2.valueProperty().bind(value2)
        barGaugeSensor2.id = "bar-gauge-sensor2"


        barGaugeSensor3 = TileBuilder.create()
            .skinType(Tile.SkinType.BAR_GAUGE)
            .prefSize(TILE_WIDTH, TILE_HEIGHT)
            .title("Water EC".toUpperCase())
            .text("\"SWEC 1\"")
            .unit("mS/cm")
            .textVisible(true)
            .value(0.0)
            .gradientStops(
                Stop(0.0, Tile.GRAY),  // Too low nutrient solution.
                Stop(0.5, Tile.GREEN),
                Stop(1.0, Tile.RED)
            ) // Too much nutrient solution.
            .strokeWithGradient(true)
            .minValue(0.0)
            .maxValue(target3 * 2)
            .threshold(target3)
            .thresholdVisible(true)
            .decimals(1)
            .tickLabelDecimals(1)
            .animated(true)
            .build()
        barGaugeSensor3.valueProperty().bind(value3)
        barGaugeSensor3.id = "bar-gauge-sensor3"


        barGaugeSensor4 = TileBuilder.create()
            .skinType(Tile.SkinType.BAR_GAUGE)
            .prefSize(TILE_WIDTH, TILE_HEIGHT)
            .title("Water pH".toUpperCase())
            .text("\"SWPH 1\"")
            .unit(" ")
            .textVisible(true)
            .value(0.0)
            .gradientStops(
                Stop(0.0, Tile.RED),  // Too acid.
                Stop(0.5, Tile.GREEN),
                Stop(1.0, Tile.BLUE)
            ) // Too alkaline.
            .strokeWithGradient(true)
            .minValue(0.0) // 0
            .maxValue(10.0) // 10
            .threshold(target4)
            .thresholdVisible(true)
            .decimals(1)
            .tickLabelDecimals(1)
            .animated(true)
            .build()
        barGaugeSensor4.valueProperty().bind(value4)
        barGaugeSensor4.id = "bar-gauge-sensor4"


        barGaugeSensor5 = TileBuilder.create()
            .skinType(Tile.SkinType.BAR_GAUGE)
            .prefSize(TILE_WIDTH, TILE_HEIGHT)
            .title("Water Temperature".toUpperCase())
            .text("\"SWTM 1\"")
            .unit("\u00b0C")
            .textVisible(true)
            .value(0.0)
            .gradientStops(
                Stop(0.0, Tile.BLUE),  // Water temperature too low.
                Stop(0.5, Tile.GREEN),
                Stop(1.0, Tile.RED)
            ) // Water temperature too high.
            .strokeWithGradient(true)
            .minValue(0.0)
            .maxValue(target5 * 2)
            .threshold(target5)
            .thresholdVisible(true)
            .decimals(1)
            .tickLabelDecimals(1)
            .animated(true)
            .build()
        barGaugeSensor5.valueProperty().bind(value5)
        barGaugeSensor5.id = "bar-gauge-sensor5"


        barGaugeSensor6 = TileBuilder.create()
            .skinType(Tile.SkinType.BAR_GAUGE)
            .prefSize(TILE_WIDTH, TILE_HEIGHT)
            .title("Light Intensity".toUpperCase())
            .text("\"SLIN 1\"")
            .unit("lux")
            .textVisible(true)
            .value(0.0)
            .gradientStops(
                Stop(0.0, Tile.BLUE),  // Light intensity too low.
                Stop(0.5, Tile.GREEN),
                Stop(1.0, Tile.RED)
            ) // Light intensity too high.
            .strokeWithGradient(true)
            .minValue(0.0)
            .maxValue(target6 * 2)
            .threshold(target6)
            .thresholdVisible(true)
            .decimals(0)
            .tickLabelDecimals(0)
            .animated(true)
            .build()
        barGaugeSensor6.valueProperty().bind(value6)
        barGaugeSensor6.id = "bar-gauge-sensor6"


        sensorsVbox2.children.addAll(
            barGaugeSensor1,
            barGaugeSensor2,
            barGaugeSensor3,
            barGaugeSensor4,
            barGaugeSensor5,
            barGaugeSensor6
        )
        sensorsVbox2.spacing = SPACING.toDouble()


        /*
         * ---ACTUATORS group---
         */

        // Actuators group pane.
        actuatorsVbox1 = VBox()
        actuatorsVbox2 = VBox()

        btGrowLight = TileBuilder.create()
            .skinType(Tile.SkinType.SWITCH)
            .prefSize(TILE_WIDTH, TILE_HEIGHT)
            .title("Grow Lights".toUpperCase())
            .text("\"APLN 1\"") //.description("Description text")
            .build()
        btGrowLight.id = "button-grow-light"
        btGrowLight.setOnSwitchPressed { e: SwitchEvent? ->
            //System.out.println("Switch pressed.");
            if (btGrowLight.isActive) {
                //System.out.println("Switch is active.");
                setAction(PlantComputerAction.LIGHT_TURN_ON)
            } else {
                //System.out.println("Switch is NOT active.");
                setAction(PlantComputerAction.LIGHT_TURN_OFF)
            }
        }


        btHumidifier = TileBuilder.create()
            .skinType(Tile.SkinType.SWITCH)
            .prefSize(TILE_WIDTH, TILE_HEIGHT)
            .title("Humidifier".toUpperCase())
            .text("\"AAHU 1\"") //.description("Description text")
            .build()
        btHumidifier.id = "button-humidifier"
        btHumidifier.setOnSwitchPressed { e: SwitchEvent? ->
            if (btHumidifier.isActive) {
                setAction(PlantComputerAction.HUMIDIFIER_TURN_ON)
            } else {
                setAction(PlantComputerAction.HUMIDIFIER_TURN_OFF)
            }
        }


        btCoolingFan = TileBuilder.create()
            .skinType(Tile.SkinType.SWITCH)
            .prefSize(TILE_WIDTH, TILE_HEIGHT)
            .title("Cooling Fan".toUpperCase())
            .text("\"AAVE 1\"") //.description("Description text")
            .build()
        btCoolingFan.id = "button-cooling-fan"
        btCoolingFan.setOnSwitchPressed { e: SwitchEvent? ->
            if (btCoolingFan.isActive) {
                setAction(PlantComputerAction.CHILLER_FAN_TURN_ON)
            } else {
                setAction(PlantComputerAction.CHILLER_FAN_TURN_OFF)
            }
        }


        btChamberFan = TileBuilder.create()
            .skinType(Tile.SkinType.SWITCH)
            .prefSize(TILE_WIDTH, TILE_HEIGHT)
            .title("Cahmber Fan".toUpperCase())
            .text("\"AACR 1\"") //.description("Description text")
            .build()
        btChamberFan.id = "button-chamber-fan"
        btChamberFan.setOnSwitchPressed { e: SwitchEvent? ->
            if (btChamberFan.isActive) {
                setAction(PlantComputerAction.CHAMBER_FAN_TURN_ON)
            } else {
                setAction(PlantComputerAction.CHAMBER_FAN_TURN_OFF)
            }
        }


        btHeater = TileBuilder.create()
            .skinType(Tile.SkinType.SWITCH)
            .prefSize(TILE_WIDTH, TILE_HEIGHT)
            .title("Heater Core".toUpperCase())
            .text("\"AAHE 1\"") //.description("Description text")
            .build()
        btHeater.id = "button-heater"
        btHeater.setOnSwitchPressed { e: SwitchEvent? ->
            if (btHeater.isActive) {
                setAction(PlantComputerAction.HEATER_TURN_ON)
            } else {
                setAction(PlantComputerAction.HEATER_TURN_OFF)
            }
        }


        btWaterCirculationPump = TileBuilder.create()
            .skinType(Tile.SkinType.SWITCH)
            .prefSize(TILE_WIDTH, TILE_HEIGHT)
            .title("Water Circulation Pump".toUpperCase())
            .text("\"AAWP 1\"") //.description("Description text")
            .build()
        btWaterCirculationPump.id = "button-water-circulation-pump"
        btWaterCirculationPump.setOnSwitchPressed { e: SwitchEvent? ->
            if (btWaterCirculationPump.isActive) {
                setAction(PlantComputerAction.WATER_PUMP_TURN_ON)
            } else {
                setAction(PlantComputerAction.WATER_PUMP_TURN_OFF)
            }
        }


        actuatorsVbox1.children.addAll(
            btGrowLight, btHumidifier,
            btCoolingFan, btChamberFan
        )
        actuatorsVbox2.children.addAll(
            btHeater,
            btWaterCirculationPump
        )
        actuatorsVbox1.spacing = SPACING.toDouble()
        actuatorsVbox2.spacing = SPACING.toDouble()


        /*
         * ---LOGS---
         */
        logs = VBox()

        cameraTile = TileBuilder.create()
            .skinType(Tile.SkinType.IMAGE)
            .prefSize(TILE_WIDTH * 2, TILE_HEIGHT * 2 + SPACING * 1)
            .title("CAMERA")
            .image(Image(UiWindow::class.java.getResourceAsStream("/camera1.jpg")))
            .imageMask(Tile.ImageMask.NONE) //.text("Whatever text")
            .textSize(Tile.TextSize.SMALLER)
            .textVisible(false)
            .textAlignment(TextAlignment.CENTER)
            .build()
        cameraTile.id = "camera-tile"

        textTile = TileBuilder.create()
            .skinType(Tile.SkinType.TEXT)
            .prefSize(TILE_WIDTH * 2, TILE_HEIGHT * 2 + SPACING * 1)
            .title("Logs".toUpperCase())
            .description(logText.toString())
            .descriptionAlignment(Pos.TOP_RIGHT) //.text("Whatever text")
            .textSize(Tile.TextSize.SMALLER)
            .textVisible(false)
            .build()
        textTile.setId("text-tile")

        val logButton = Button("LOGS")
        logButton.onAction = EventHandler { e: ActionEvent? ->
            val newWindow = Stage()
            newWindow.initModality(Modality.NONE)
            val logsPanel = LogsPanel()
            logsPanel.id = "logs-panel"
            eventManager.addListener(logsPanel)
            val logsScene = Scene(logsPanel, 800.0, 600.0)
            newWindow.scene = logsScene
            newWindow.title = "Logs"
            newWindow.show()
        }
        logButton.id = "log-button"


        customTile = TileBuilder.create()
            .skinType(Tile.SkinType.CUSTOM)
            .prefSize(TILE_WIDTH, TILE_HEIGHT)
            .title("Log details".toUpperCase()) //.text("Whatever text")
            .graphic(logButton)
            .roundedCorners(false)
            .build()
        customTile.id = "custom-tile"

        logs.children.addAll(cameraTile, textTile, customTile)
        logs.spacing = SPACING.toDouble()

        super.getChildren().addAll(
            home,
            sensorsVBox1, sensorsVbox2,
            actuatorsVbox1, actuatorsVbox2, logs
        )

        /*
            Timer for animation:
             - if simulationMode==true -> random generator
             - if simulationMode==false -> sensors/actuators read (default).
         */
        lastTimerCall1 = System.nanoTime()
        timer1 = object : AnimationTimer() {
            override fun handle(now: Long) {
                if (now > lastTimerCall1 + 10000000000L) {     // Update every 10.0 seconds.

                    // Sending command for reading sensors & actuators to MQTT broker.
                    listReadingCommands.clear()
                    listReadingCommands.add(PlantComputerCommand(PlantComputerAction.TEMPERATURE_AIR_STATE_READ))
                    listReadingCommands.add(PlantComputerCommand(PlantComputerAction.HUMIDITY_AIR_STATE_READ))
                    listReadingCommands.add(PlantComputerCommand(PlantComputerAction.CONDUCTIVITY_WATER_STATE_READ))
                    listReadingCommands.add(PlantComputerCommand(PlantComputerAction.PH_WATER_STATE_READ))
                    listReadingCommands.add(PlantComputerCommand(PlantComputerAction.TEMPERATURE_WATER_STATE_READ))
                    listReadingCommands.add(PlantComputerCommand(PlantComputerAction.LIGHT_INTENSITY_STATE_READ))
                    listReadingCommands.add(PlantComputerCommand(PlantComputerAction.LIGHT_STATE_READ))
                    listReadingCommands.add(PlantComputerCommand(PlantComputerAction.HUMIDIFIER_STATE_READ))
                    listReadingCommands.add(PlantComputerCommand(PlantComputerAction.COOLING_FAN_STATE_READ))
                    listReadingCommands.add(PlantComputerCommand(PlantComputerAction.CHAMBER_FAN_STATE_READ))
                    listReadingCommands.add(PlantComputerCommand(PlantComputerAction.HEATER_TURN_STATE_READ))
                    listReadingCommands.add(PlantComputerCommand(PlantComputerAction.WATER_PUMP_STATE_READ))
                    sendMessageForReadingStates()


                    var rndValue1 = RND.nextDouble() * sparkLineSensor1.range * 0.1 + target1
                    var rndValue2 = RND.nextDouble() * sparkLineSensor2.range * 0.1 + target2
                    var rndValue3 = RND.nextDouble() * sparkLineSensor3.range * 0.1 + target3
                    var rndValue4 = RND.nextDouble() * sparkLineSensor4.range * 0.1 + target4
                    var rndValue5 = RND.nextDouble() * sparkLineSensor5.range * 0.1 + target5
                    var rndValue6 = RND.nextDouble() * sparkLineSensor6.range * 0.1 + target6

                    /**
                     * TESTING/SIMULATION: sensor readings as MQTT message.
                     */
                    // Sending command for reading sensors to MQTT broker.
                    var simulationMode = false // default.
                    val namedParams = PlantComputerApplicationKotlin.parameters.named
                    if (namedParams.containsKey("simulationMode")) {            // if contains testing argument
                        simulationMode =
                            Boolean.parseBoolean(namedParams["simulationMode"]) // run --args='--testing=true'
                    }
                    if (simulationMode == true) {
                        listStateReadings.clear()
                        listStateReadings.add(
                            PlantComputerCommand(
                                PlantComputerAction.TEMPERATURE_AIR_STATE.setActionValue(
                                    rndValue1.toString()
                                )
                            )
                        )
                        listStateReadings.add(
                            PlantComputerCommand(
                                PlantComputerAction.HUMIDITY_AIR_STATE.setActionValue(
                                    rndValue2.toString()
                                )
                            )
                        )
                        listStateReadings.add(
                            PlantComputerCommand(
                                PlantComputerAction.CONDUCTIVITY_WATER_STATE.setActionValue(
                                    rndValue3.toString()
                                )
                            )
                        )
                        listStateReadings.add(
                            PlantComputerCommand(
                                PlantComputerAction.PH_WATER_STATE.setActionValue(
                                    rndValue4.toString()
                                )
                            )
                        )
                        listStateReadings.add(
                            PlantComputerCommand(
                                PlantComputerAction.TEMPERATURE_WATER_STATE.setActionValue(
                                    rndValue5.toString()
                                )
                            )
                        )
                        listStateReadings.add(
                            PlantComputerCommand(
                                PlantComputerAction.LIGHT_INTENSITY_STATE.setActionValue(
                                    rndValue6.toString()
                                )
                            )
                        )
                        val isBtGrowLightOn: String
                        isBtGrowLightOn = if (btGrowLight.isActive) "on" else "off"
                        listStateReadings.add(
                            PlantComputerCommand(
                                PlantComputerAction.LIGHT_STATE.setActionValue(
                                    isBtGrowLightOn
                                )
                            )
                        )
                        val isBtHumidifierOn: String
                        isBtHumidifierOn = if (btHumidifier.isActive) "on" else "off"
                        listStateReadings.add(
                            PlantComputerCommand(
                                PlantComputerAction.HUMIDIFIER_STATE.setActionValue(
                                    isBtHumidifierOn
                                )
                            )
                        )
                        val isBtCoolingFanOn: String
                        isBtCoolingFanOn = if (btCoolingFan.isActive) "on" else "off"
                        listStateReadings.add(
                            PlantComputerCommand(
                                PlantComputerAction.COOLING_FAN_STATE.setActionValue(
                                    isBtCoolingFanOn
                                )
                            )
                        )
                        val isBtChamberFanOn: String
                        isBtChamberFanOn = if (btChamberFan.isActive) "on" else "off"
                        listStateReadings.add(
                            PlantComputerCommand(
                                PlantComputerAction.CHAMBER_FAN_STATE.setActionValue(
                                    isBtChamberFanOn
                                )
                            )
                        )
                        val isBtHeaterOn: String
                        isBtHeaterOn = if (btHeater.isActive) "on" else "off"
                        listStateReadings.add(
                            PlantComputerCommand(
                                PlantComputerAction.HEATER_TURN_STATE.setActionValue(
                                    isBtHeaterOn
                                )
                            )
                        )
                        val isBtWaterCirculationPumpOn: String
                        isBtWaterCirculationPumpOn = if (btWaterCirculationPump.isActive) "on" else "off"
                        listStateReadings.add(
                            PlantComputerCommand(
                                PlantComputerAction.WATER_PUMP_STATE.setActionValue(
                                    isBtWaterCirculationPumpOn
                                )
                            )
                        )
                        sendMessageStateReadings() // if (simulationMode==true)
                    }
                    lastTimerCall1 = now
                }
            }
        }
        timer1.start()
    }


    /**
     * Handle the chosen effect from a button or a Mosquitto message to enable/disable the available UI elements and
     * highlight the button of the selected [PlantComputerAction].
     */
    private fun setAction(plantComputerAction: PlantComputerAction) {
        // Single actuator command.
        selectedPlantComputerAction = plantComputerAction

        // Creating list of actuator commands.
        listActuatorCommands.clear()
        if (btGrowLight.isActive) listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.LIGHT_TURN_ON)) else listActuatorCommands.add(
            PlantComputerCommand(PlantComputerAction.LIGHT_TURN_OFF)
        )
        if (btHumidifier.isActive) listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.HUMIDIFIER_TURN_ON)) else listActuatorCommands.add(
            PlantComputerCommand(PlantComputerAction.HUMIDIFIER_TURN_OFF)
        )
        if (btCoolingFan.isActive) listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.CHILLER_FAN_TURN_ON)) else listActuatorCommands.add(
            PlantComputerCommand(PlantComputerAction.CHILLER_FAN_TURN_OFF)
        )
        if (btChamberFan.isActive) listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.CHAMBER_FAN_TURN_ON)) else listActuatorCommands.add(
            PlantComputerCommand(PlantComputerAction.CHAMBER_FAN_TURN_OFF)
        )
        if (btHeater.isActive) listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.HEATER_TURN_ON)) else listActuatorCommands.add(
            PlantComputerCommand(PlantComputerAction.HEATER_TURN_OFF)
        )
        if (btWaterCirculationPump.isActive) listActuatorCommands.add(PlantComputerCommand(PlantComputerAction.WATER_PUMP_TURN_ON)) else listActuatorCommands.add(
            PlantComputerCommand(PlantComputerAction.WATER_PUMP_TURN_OFF)
        )
        sendMessageForActuators()
    }


    /**
     * On received MQTT message.
     */
    override fun onQueueMessage(plantComputerCommand: PlantComputerCommand?) {
        blockSending = true
        println("Command received from Mosquitto: " + plantComputerCommand!!.toStringCommand())

        // Sensor readings + update values in UI:
        if (plantComputerCommand.id == "500" && plantComputerCommand.actionName == "SATM 1") {
            value1.set(plantComputerCommand.actionValue.toDouble())
        } else if (plantComputerCommand.id == "510" && plantComputerCommand.actionName == "SAHU 1") {
            value2.set(plantComputerCommand.actionValue.toDouble())
        } else if (plantComputerCommand.id == "520" && plantComputerCommand.actionName == "SWEC 1") {
            value3.set(plantComputerCommand.actionValue.toDouble())
        } else if (plantComputerCommand.id == "530" && plantComputerCommand.actionName == "SWPH 1") {
            value4.set(plantComputerCommand.actionValue.toDouble())
        } else if (plantComputerCommand.id == "540" && plantComputerCommand.actionName == "SWTM 1") {
            value5.set(plantComputerCommand.actionValue.toDouble())
        } else if (plantComputerCommand.id == "550" && plantComputerCommand.actionName == "SLIN 1") {
            value6.set(plantComputerCommand.actionValue.toDouble())
        }

        // Write data into influxDB:
        try {
            if (PlantComputerApplicationKotlin.isInfluxDbOnline == true) {
                println("Writing data into influxDb.")
                writeDataIntoInfluxDb()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        blockSending = false
    }

    /**
     * Write into influxDB.
     */
    private fun writeDataIntoInfluxDb() {

        // Write data
        val writeApi = PlantComputerApplicationKotlin.influxDBClient.writeApiBlocking

        // Write by Data Points
        val point1 = Point.measurement("air_temperature")
            .addTag("sensor_id", "SATM 1")
            .addField("value", value1.doubleValue())
            .time(Instant.now().toEpochMilli(), WritePrecision.MS)
        val point2 = Point.measurement("air_humidity")
            .addTag("sensor_id", "SAHU 1")
            .addField("value", value2.doubleValue())
            .time(Instant.now().toEpochMilli(), WritePrecision.MS)
        val point3 = Point.measurement("water_electrical_conductivity")
            .addTag("sensor_id", "SWEC 1")
            .addField("value", value3.doubleValue())
            .time(Instant.now().toEpochMilli(), WritePrecision.MS)
        val point4 = Point.measurement("water_potential_hydrogen")
            .addTag("sensor_id", "SWPH 1")
            .addField("value", value4.doubleValue())
            .time(Instant.now().toEpochMilli(), WritePrecision.MS)
        val point5 = Point.measurement("water_temperature")
            .addTag("sensor_id", "SWTM 1")
            .addField("value", value5.doubleValue())
            .time(Instant.now().toEpochMilli(), WritePrecision.MS)
        val point6 = Point.measurement("light intensity")
            .addTag("sensor_id", "SLIN 1")
            .addField("value", value6.doubleValue())
            .time(Instant.now().toEpochMilli(), WritePrecision.MS)
        val listPoint: MutableList<Point> = ArrayList()
        listPoint.add(point1)
        listPoint.add(point2)
        listPoint.add(point3)
        listPoint.add(point4)
        listPoint.add(point5)
        listPoint.add(point6)
        writeApi.writePoints(listPoint)
    }


}