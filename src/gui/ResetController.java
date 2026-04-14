package gui;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import main.HomeSystem;
import models.*;

import java.util.List;
import java.util.stream.Collectors;

import data.FileManager;

// ==============================
// CONTROLLER: RESET SENSORI
// ==============================
//
// Gestisce la finestra GUI per il reset dei sensori.
// Consente di resettare un singolo sensore, tutti i sensori,
// o di svuotare completamente il sistema (cancellazione dati).
//
public class ResetController {

    // ComboBox contenente gli ID dei sensori disponibili
    @FXML
    private ComboBox<String> sensorComboBox;

    // Riferimento al sistema domotico principale
    private HomeSystem system;

    // Riferimento al controller principale della GUI
    private ControlPanelController mainController;

    // ==============================
    // SETTER / INIZIALIZZAZIONE
    // ==============================

    // Imposta il sistema domotico e popola la ComboBox dei sensori
    public void setSystem(HomeSystem system) {
        this.system = system;
        populateSensorComboBox();
    }

    // Imposta il controller principale della GUI
    public void setMainController(ControlPanelController controller) {
        this.mainController = controller;
    }

    // Popola la ComboBox con tutti gli ID dei sensori presenti nel sistema
    private void populateSensorComboBox() {
        // ottiene gli ID dei sensori
        List<String> sensorIds = system.getSensors().stream().map(Sensor::getId).collect(Collectors.toList());
        sensorComboBox.setItems(FXCollections.observableArrayList(sensorIds));
        if (!sensorIds.isEmpty())
            sensorComboBox.getSelectionModel().selectFirst(); // seleziona il primo elemento
    }

    // ==============================
    // AZIONI SUI SENSORI
    // ==============================

    // Resetta un singolo sensore selezionato nella ComboBox
    @FXML
    private void resetSingleSensor() {
        String sensorId = sensorComboBox.getSelectionModel().getSelectedItem();
        if (sensorId != null) {
            boolean success = system.resetSensorById(sensorId); // HomeSystem gestisce il reset completo
            if (success) {
                System.out.println("Coppia Resettata");
            } else {
                System.out.println("Errore nel resettare il sensore " + sensorId);
            }
        }
        mainController.refreshSensorList(); // aggiorna la tabella dei sensori nella GUI principale
    }

    // Resetta tutti i sensori presenti nel sistema
    @FXML
    private void resetAllSensors() {
        system.resetSensorsS(); // HomeSystem gestisce internamente il reset
        mainController.refreshSensorList(); // aggiorna la tabella dei sensori
    }

    // Cancella completamente il sistema: svuota i dati e la memoria dei sensori
    @FXML
    private void clearSystem() {
        FileManager.clearDataFiles(); // cancella file nella cartella dati
        system.getSensors().clear();  // rimuove tutti i sensori dalla memoria
        System.out.println("Sistema resettato completamente.");
        populateSensorComboBox(); // aggiorna ComboBox
        mainController.refreshSensorList(); // aggiorna la tabella nella GUI principale
    }

    // Chiude la finestra del reset
    @FXML
    private void closeWindow() {
        Stage stage = (Stage) sensorComboBox.getScene().getWindow();
        stage.close();
    }
}