package gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import main.HomeSystem;
import data.FileManager;
import model.Sensor;

import java.util.List;
import java.util.stream.Collectors;

public class ResetController {

    @FXML
    private ComboBox<String> sensorComboBox;
    private HomeSystem system;
    private ControlPanelController mainController;

    public void setSystem(HomeSystem system) {
        this.system = system;
        populateSensorComboBox();
    }

    public void setMainController(ControlPanelController controller) {
        this.mainController = controller;
    }

    private void populateSensorComboBox() {
        List<String> sensorIds = system.getSensors().stream()
                .map(Sensor::getId)
                .collect(Collectors.toList());
        sensorComboBox.setItems(FXCollections.observableArrayList(sensorIds));
        if (!sensorIds.isEmpty())
            sensorComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void resetSingleSensor() {
        String sensorId = sensorComboBox.getSelectionModel().getSelectedItem();
        if (sensorId != null) {
            boolean success = system.resetSensorById(sensorId);
            if (success) {
                System.out.println("Sensore " + sensorId + " resettato (base + moduli).");
            } else {
                System.out.println("Errore nel resettare il sensore " + sensorId);
            }
        }
        mainController.refreshSensorList();   // aggiorna la tabella nella GUI principale
    }

    @FXML
    private void resetAllSensors() {
        try {
            system.resetSensors();
            System.out.println("Tutti i sensori e moduli sono stati resettati.");
        } catch (IllegalStateException e) {
            System.out.println("Errore: stato del sistema non impostato!");
        }
        mainController.refreshSensorList();   // aggiorna la tabella nella GUI principale
    }

    @FXML
    private void clearSystem() {
        FileManager.clearDataFiles();
        system.getSensors().clear(); // rimuove sensori dalla memoria
        System.out.println("Sistema resettato completamente.");
        populateSensorComboBox(); // aggiorna ComboBox
        mainController.refreshSensorList();   // aggiorna la tabella nella GUI principale
    }

    @FXML
    private void closeWindow() {
        Stage stage = (Stage) sensorComboBox.getScene().getWindow();
        stage.close();
    }
}