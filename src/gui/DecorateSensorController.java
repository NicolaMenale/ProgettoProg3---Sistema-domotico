package gui;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import main.*;

public class DecorateSensorController {

    @FXML
    private ComboBox<String> sensorComboBox;

    @FXML
    private ListView<String> moduleListView;

    private HomeSystem system;
    private ControlPanelController mainController;

    public void setSystem(HomeSystem system) {
        this.system = system;
        loadSensors();
    }

    public void setMainController(ControlPanelController controller) {
        this.mainController = controller;
    }

    private void loadSensors() {
        sensorComboBox.getItems().clear();
        sensorComboBox.getItems().addAll(system.getSensorInfo());

        sensorComboBox.setOnAction(e -> loadModules());
    }

    private void loadModules() {
        String sensorInfo = sensorComboBox.getValue();
        if (sensorInfo == null)
            return;

        String sensorId = system.getSensorIdByIndex(sensorComboBox.getSelectionModel().getSelectedIndex());

        moduleListView.getItems().clear();
        moduleListView.getItems().addAll(system.getAvailableModules(sensorId));
    }

    @FXML
    private void addModule() {

        String sensorInfo = sensorComboBox.getValue();
        String moduleName = moduleListView.getSelectionModel().getSelectedItem();

        if (sensorInfo == null || moduleName == null)
            return;

        String sensorId = system.getSensorIdByIndex(sensorComboBox.getSelectionModel().getSelectedIndex());

        boolean success = system.addModuleToSensor(sensorId, moduleName);

        if (success) {

            loadModules(); // aggiorna moduli disponibili

            mainController.refreshSensorList(); // aggiorna tabella principale
        }
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) sensorComboBox.getScene().getWindow();
        stage.close();
    }
}