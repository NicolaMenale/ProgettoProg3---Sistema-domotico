package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import main.*;

// ==============================
// CONTROLLER: DECORAZIONE SENSORI
// ==============================
//
// Gestisce la finestra GUI per aggiungere moduli
// (decoratori) ai sensori già installati.
//
public class DecorateSensorController {

    // ComboBox per selezionare il sensore da decorare
    @FXML
    private ComboBox<String> sensorComboBox;

    // ListView per mostrare i moduli disponibili per il sensore selezionato
    @FXML
    private ListView<String> moduleListView;

    // Riferimento al sistema domotico principale
    private HomeSystem system;

    // Riferimento al controller principale della GUI
    private ControlPanelController mainController;

    // ==============================
    // SETTER
    // ==============================

    // Imposta il sistema domotico principale e carica i sensori nella ComboBox
    public void setSystem(HomeSystem system) {
        this.system = system;
        loadSensors();
    }

    // Imposta il controller principale della GUI
    public void setMainController(ControlPanelController controller) {
        this.mainController = controller;
    }

    // ==============================
    // CARICAMENTO SENSORI
    // ==============================

    // Popola la ComboBox con i sensori presenti nel sistema
    private void loadSensors() {
        sensorComboBox.getItems().clear(); // pulisce eventuali valori precedenti
        sensorComboBox.getItems().addAll(system.getSensorInfo()); // aggiunge info sensori

        // Aggiunge listener per aggiornare la lista dei moduli quando cambia il sensore selezionato
        sensorComboBox.setOnAction(e -> loadModules());
    }

    // ==============================
    // CARICAMENTO MODULI DISPONIBILI
    // ==============================

    // Aggiorna la ListView con i moduli disponibili per il sensore selezionato
    private void loadModules() {
        String sensorInfo = sensorComboBox.getValue(); // ottiene info del sensore selezionato
        if (sensorInfo == null)
            return; // nessun sensore selezionato

        // Ottiene l'ID reale del sensore dalla posizione nella ComboBox
        String sensorId = system.getSensorIdByIndex(sensorComboBox.getSelectionModel().getSelectedIndex());

        // Aggiorna la lista dei moduli disponibili
        moduleListView.getItems().clear();
        moduleListView.getItems().addAll(system.getAvailableModules(sensorId));
    }

    // ==============================
    // AGGIUNTA MODULO
    // ==============================

    // Aggiunge il modulo selezionato al sensore selezionato
    @FXML
    private void addModule() {

        String sensorInfo = sensorComboBox.getValue(); // sensore selezionato
        String moduleName = moduleListView.getSelectionModel().getSelectedItem(); // modulo selezionato

        if (sensorInfo == null || moduleName == null)
            return; // niente da fare se nessuna selezione

        // Ottiene l'ID reale del sensore
        String sensorId = system.getSensorIdByIndex(sensorComboBox.getSelectionModel().getSelectedIndex());

        // Chiama HomeSystem per aggiungere il modulo al sensore
        boolean success = system.addModuleToSensor(sensorId, moduleName);

        if (success) {
            loadModules(); // aggiorna moduli disponibili dopo l'aggiunta
            mainController.refreshSensorList(); // aggiorna tabella principale
        }
    }

    // ==============================
    // CHIUSURA FINESTRA
    // ==============================

    // Chiude la finestra corrente
    @FXML
    private void closeWindow() {
        Stage stage = (Stage) sensorComboBox.getScene().getWindow();
        stage.close();
    }
}