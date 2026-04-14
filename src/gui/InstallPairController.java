package gui;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import main.HomeSystem;

// ==============================
// CONTROLLER: INSTALLAZIONE COPPIA SENSORI
// ==============================
//
// Gestisce la finestra GUI per l'aggiunta di una coppia
// di sensori (monitoraggio + intervento) con threshold impostato.
//
public class InstallPairController {

    // Area di testo per eventuali messaggi/log
    @FXML
    private TextArea logArea;

    // ComboBox per selezionare il tipo di coppia sensore
    @FXML
    private ComboBox<String> typeComboBox;

    // Campo di testo per inserire il valore della soglia (threshold)
    @FXML
    private TextField thresholdField;

    // Riferimento al controller principale della GUI
    private ControlPanelController mainController;

    // Riferimento al sistema domotico principale
    private HomeSystem system;

    // Tipi di sensori di monitoraggio disponibili
    private final String[] monitorTypes = { "TEMPERATURE", "ELECTRICITY", "SMOKE", "GAS", "MOVEMENT" };

    // Tipi di sensori di intervento disponibili
    private final String[] interventionTypes = { "AIRCONDITIONER", "POWERCUT", "SIREN", "VENT", "LOCK" };

    // ==============================
    // SETTER
    // ==============================

    /**
     * Imposta il controller principale della GUI
     */
    public void setMainController(ControlPanelController controller) {
        this.mainController = controller;
    }

    /**
     * Imposta il sistema domotico principale
     */
    public void setSystem(HomeSystem system) {
        this.system = system;
    }

    // ==============================
    // INIZIALIZZAZIONE DELLA GUI
    // ==============================

    /**
     * Popola la ComboBox con le coppie monitoraggio → intervento
     */
    @FXML
    public void initialize() {
        for (int i = 0; i < monitorTypes.length; i++) {
            typeComboBox.getItems().add(
                    monitorTypes[i] + " → " + interventionTypes[i]);
        }
        typeComboBox.getSelectionModel().selectFirst(); // seleziona la prima coppia di default
    }

    // ==============================
    // AZIONE INSTALLAZIONE COPPIA
    // ==============================

    // Viene chiamato al click sul pulsante "Installa"
    @FXML
    private void installPair() {
        
        try {
            // Ottiene l'indice della coppia selezionata
            int choice = typeComboBox.getSelectionModel().getSelectedIndex();

            // Determina i tipi di sensore da installare
            String monitorType = monitorTypes[choice];
            String interventionType = interventionTypes[choice];

            // Legge il threshold inserito dall'utente
            int threshold = Integer.parseInt(thresholdField.getText());

            // Chiama HomeSystem per installare la coppia di sensori
            system.installMonitoringPair(monitorType, interventionType, threshold);

            // Chiude la finestra di installazione
            Stage stage = (Stage) thresholdField.getScene().getWindow();
            stage.close();
        } catch (NumberFormatException e) {
            // Gestisce l'errore se il threshold non è un numero
            mainController.addLog("Inserire un Numero");
        } catch (Exception e) {
            // Stampa eventuali altri errori
            e.printStackTrace();
        }
    }
}