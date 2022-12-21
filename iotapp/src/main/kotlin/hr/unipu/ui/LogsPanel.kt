package hr.unipu.ui

import hr.unipu.client.ReceivedMessage
import hr.unipu.event.EventListener
import hr.unipu.plantcomputer.PlantComputerCommand
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.collections.ObservableList
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.cell.PropertyValueFactory

class LogsPanel internal constructor() : TableView<Any?>(), EventListener {
    private val list : ObservableList<Any?> = FXCollections.observableArrayList()

    /**
     * [PlantComputerCommand] received from Mosquitto.
     * We put it on top of our internal list so it gets added to the table.
     *
     * @param plantComputerCommand The [PlantComputerCommand]
     */
    override fun onQueueMessage(plantComputerCommand: PlantComputerCommand?) {
        list.add(0, ReceivedMessage(plantComputerCommand!!))
    }

    /**
     * Construct the UI as a [TableView].
     */
    init {

        val colTimestamp: TableColumn<Any?, Any?> = TableColumn("Timestamp")
        colTimestamp.style = "-fx-alignment: TOP-LEFT;"
        colTimestamp.minWidth = 150.0
        colTimestamp.setCellValueFactory(PropertyValueFactory<Any, Any>("timestamp"))


        val colCommandAction: TableColumn<Any?, Any?> = TableColumn("Action ID")
        colCommandAction.style = "-fx-alignment: TOP-LEFT;"
        colCommandAction.minWidth = 100.0
        colCommandAction.setCellValueFactory(PropertyValueFactory<Any?, Any?>("plantComputerCommand"))
        colCommandAction.setCellFactory { column: Any? ->
            object : TableCell<Any?, Any?>() {
                override fun updateItem(plantComputerCommand: Any?, empty: Boolean) {
                    super.updateItem(plantComputerCommand, empty)
                    if (plantComputerCommand == null || empty) {
                        text = null
                        style = ""
                    } else {
                        text = (plantComputerCommand as PlantComputerCommand).id
                    }
                }
            }
        }



        val colActionName: TableColumn<Any?, Any?> = TableColumn("Action Name")
        colActionName.style = "-fx-alignment: TOP-LEFT;"
        colActionName.minWidth = 100.0
        colActionName.setCellValueFactory(PropertyValueFactory<Any?, Any?>("plantComputerCommand"))
        colActionName.setCellFactory { column: Any? ->
            object : TableCell<Any?, Any?>() {
                override fun updateItem(item: Any?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (item == null || empty) {
                        text = null
                        style = ""
                    } else {
                        text = (item as PlantComputerCommand).actionName.toString()
                    }
                }
            }
        }


        val colActionValue: TableColumn<Any?, Any?> = TableColumn("Action Value")
        colActionValue.style = "-fx-alignment: TOP-LEFT;"
        colActionValue.minWidth = 100.0
        colActionValue.setCellValueFactory(PropertyValueFactory<Any?, Any?>("plantComputerCommand"))
        colActionValue.setCellFactory { column: Any? ->
            object : TableCell<Any?, Any?>() {
                protected override fun updateItem(item: Any?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (item == null || empty) {
                        text = null
                        style = ""
                    } else {
                        text = (item as PlantComputerCommand).actionValue.toString()
                    }
                }
            }
        }


        val colDataString: TableColumn<Any?, Any?> = TableColumn("Data String")
        colDataString.style = "-fx-alignment: TOP-CENTER;"
        colDataString.minWidth = 300.0
        colDataString.setCellValueFactory(PropertyValueFactory<Any?, Any?>("plantComputerCommand"))
        colDataString.setCellFactory { column: Any? ->
            object : TableCell<Any?, Any?>() {
                protected override fun updateItem(item: Any?, empty: Boolean) {
                    super.updateItem(item, empty)
                    text = if (item == null || empty) {
                        null
                    } else {
                        (item as PlantComputerCommand).toStringCommand()
                    }
                }
            }
        }


        columns.addAll(
            colTimestamp,
            colCommandAction,
            colActionName,
            colActionValue,
            colDataString
        )
        setItems(list)
    }

}

