package gui;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import main.HomeSystem;
import models.*;
import java.util.HashMap;
import java.util.Map;

// ==============================
// CONTROLLER: RESET SENSORI
// ==============================
//
// Gestisce la finestra GUI per il reset dei sensori.
// Consente di resettare un singolo sensore, tutti i sensori,
// o di svuotare completamente il sistema (cancellazione dati).
//
public class ResetController {

    /**
     * ComboBox contenente gli ID dei sensori disponibili
     */
    @FXML
    private ComboBox<String> sensorComboBox;
    
    /**
     * Riferimento al sistema domotico principale
     */
    private HomeSystem system;

    /**
     * Riferimento al controller principale della GUI
     */
    private ControlPanelController mainController;

    /**
     * Mappa che associa la rappresentazione testuale della coppia
     * (monitor -> intervention) mostrata nella ComboBox
     * all'ID del sensore monitor corrispondente.
     * Serve per recuperare il dato logico a partire dalla selezione UI.
     */
    private Map<String, String> pairMap = new HashMap<>();

    // ==============================
    // SETTER / INIZIALIZZAZIONE
    // ==============================

    /**
     * Imposta il sistema domotico e popola la ComboBox dei sensori
     */
    public void setSystem(HomeSystem system) {
        this.system = system;
        populateSensorComboBox();
    }

    /**
     * Imposta il controller principale della GUI
     */
    public void setMainController(ControlPanelController controller) {
        this.mainController = controller;
    }

    /**
     * Popola la ComboBox con tutti gli ID dei sensori presenti nel sistema
     */
    private void populateSensorComboBox() {
        sensorComboBox.getItems().clear();
        pairMap.clear(); // se vuoi ancora mappare display -> monitorId

        for (Map.Entry<Sensor, Sensor> entry : system.getMonitoringPairs().entrySet()) {

            Sensor monitor = entry.getKey();
            Sensor intervention = entry.getValue();

            String monitorId = monitor.getId();
            String interventionId = intervention.getId();

            String display = monitorId + " -> " + interventionId;

            sensorComboBox.getItems().add(display);

            // utile: recuperi il monitorId quando selezioni la combo
            pairMap.put(display, monitorId);
        }

        if (!sensorComboBox.getItems().isEmpty()) {
            sensorComboBox.getSelectionModel().selectFirst();
        }
    }
    // ==============================
    // AZIONI SUI SENSORI
    // ==============================

    /**
     * Resetta un singolo sensore selezionato nella ComboBox
     */
    @FXML
    private void resetPairSensor() {
        String selected = sensorComboBox.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String monitorId = pairMap.get(selected);
            if (monitorId != null) {
                boolean success = system.resetPairById(monitorId);
                if (success) {
                    System.out.println("Coppia Resettata");
                } else {
                    System.out.println("Errore reset sensore " + monitorId);
                }
            }
        }

        mainController.refreshSensorList();
    }

    /**
     * Resetta tutti i sensori presenti nel sistema
     */
    @FXML
    private void resetAllSensors() {
        system.resetSensors(system); // HomeSystem gestisce internamente il reset
        mainController.refreshSensorList(); // aggiorna la tabella dei sensori
    }

    /**
     * Cancella completamente il sistema: svuota i dati e la memoria dei sensori
     */
    @FXML
    private void clearSystem() {
        system.resetData(system);
        populateSensorComboBox(); // aggiorna ComboBox
        mainController.refreshSensorList(); // aggiorna la tabella nella GUI principale
    }

    /**
     * Chiude la finestra del reset
     */
    @FXML
    private void closeWindow() {
        Stage stage = (Stage) sensorComboBox.getScene().getWindow();
        stage.close();
    }
}