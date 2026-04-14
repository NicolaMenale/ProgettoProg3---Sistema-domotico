package gui;

import javafx.fxml.*;
import javafx.event.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.*;
import javafx.application.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.*;
import main.*;
import state.AttivoState;
import state.CollaudoState;

import java.io.IOException;
import java.util.*;

import data.*;
import decorator.SensorDecorator;
import models.*;

// ==============================
// CONTROLLER PRINCIPALE GUI
// ==============================
//
// Gestisce la finestra principale del sistema domotico.
// Mostra sensori, statistiche, modalità di funzionamento
// e consente di aprire finestre secondarie per installazione,
// reset e decorazione dei sensori.
//
public class ControlPanelController {

    // Riferimento al sistema domotico principale
    private HomeSystem system;

    // ==============================
    // ELEMENTI GUI
    // ==============================

    @FXML
    private Label modeLabel; // mostra modalità corrente (Collaudo / Attivato)
    @FXML
    private TableView<Sensor> statsTableView; // tabella sensori / statistiche

    // Colonne tabella
    @FXML
    private TableColumn<Sensor, String> idColumn;
    @FXML
    private TableColumn<Sensor, String> typeColumn;
    @FXML
    private TableColumn<Sensor, String> activeColumn;
    @FXML
    private TableColumn<Sensor, String> thresholdColumn;
    @FXML
    private TableColumn<Sensor, String> moduleColumn;
    @FXML
    private TableColumn<Sensor, String> alarmColumn;
    @FXML
    private TableColumn<Sensor, String> readingsColumn;
    @FXML
    private TableColumn<Sensor, String> alarmHistoryColumn;

    // Altri componenti GUI
    @FXML
    private ComboBox<String> typeComboBox; // selezione tipo sensore per installazione
    @FXML
    private TextField thresholdField; // input threshold
    @FXML
    private TextArea logArea; // log eventi generali
    @FXML
    private TextArea alarmQueueArea; // mostra allarmi attivi

    // ==============================
    // SET SYSTEM
    // ==============================

    // Imposta il sistema domotico principale e prepara la tabella sensori
    public void setSystem(HomeSystem system) {
        this.system = system;
        setupTable();
        refreshSensorList();
    }

    // ==============================
    // CAMBIO MODALITÀ
    // ==============================

    // Imposta modalità Attivato
    @FXML
    private void activateMode(ActionEvent event) throws Exception {
        system.setState(new AttivoState());
        system.setActiveMode();
        modeLabel.setText("MODALITÀ: ATTIVATO");
        refreshSensorList(); // aggiorna tabella
    }

    // Imposta modalità Collaudo
    @FXML
    private void testMode(ActionEvent event) throws Exception {
        system.setState(new CollaudoState());
        system.setCollaudoMode();
        modeLabel.setText("MODALITÀ: COLLAUDO");
        refreshSensorList(); // aggiorna tabella
    }

    // ==============================
    // AGGIORNAMENTO TABELLA SENSORI
    // ==============================

    // Aggiorna lista dei sensori nella tabella
    public void refreshSensorList() {
        if (system == null)
            return;
        ObservableList<Sensor> data = FXCollections.observableArrayList(system.getSensors());
        statsTableView.setItems(data);
        statsTableView.refresh();
    }

    // Imposta le colonne della tabella sensori
    private void setupTable() {
        // Colonna ID sensore
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Colonna tipo sensore (Monitoring / Intervention)
        typeColumn.setCellValueFactory(cell -> {
            Sensor base = getBaseSensor(cell.getValue());
            String tipo = (base instanceof MonitoringSensor) ? "Monitoring" : "Intervention";
            return new SimpleStringProperty(tipo);
        });

        // Colonna stato attivo (YES/NO)
        activeColumn.setCellValueFactory(cell -> {
            Sensor base = getBaseSensor(cell.getValue());
            String val = base.isActive() ? "YES" : "NO";
            return new SimpleStringProperty(val);
        });

        // Colonna threshold (solo per MonitoringSensor)
        thresholdColumn.setCellValueFactory(cell -> {
            Sensor base = getBaseSensor(cell.getValue());
            String val;
            if (base instanceof MonitoringSensor ms) {
                val = String.valueOf(ms.getThreshold());
            } else {
                val = "-";
            }
            return new SimpleStringProperty(val);
        });

        // Colonna moduli installati (decoratori)
        moduleColumn.setCellValueFactory(cell -> {
            Sensor s = cell.getValue();
            List<String> modules = new ArrayList<>();
            Sensor current = s;

            // Scorre tutti i decorator e prende i nomi dei moduli
            while (current instanceof SensorDecorator dec) {
                modules.add(dec.getModuleName());
                current = dec.getWrappedSensor();
            }

            Collections.reverse(modules); // ordine base -> ultimo modulo

            String val = modules.isEmpty() ? "-" : String.join(", ", modules);
            return new SimpleStringProperty(val);
        });

        // Colonna numero allarmi / attivazioni
        alarmColumn.setCellValueFactory(cell -> {
            Sensor base = getBaseSensor(cell.getValue());
            String val;
            if (base instanceof MonitoringSensor ms) {
                val = String.valueOf(ms.getAlarmCount());
            } else if (base instanceof InterventionSensor is) {
                val = String.valueOf(is.getActivationCount());
            } else {
                val = "-";
            }
            return new SimpleStringProperty(val);
        });

        // Colonna letture (solo MonitoringSensor)
        readingsColumn.setCellValueFactory(cell -> {
            Sensor base = getBaseSensor(cell.getValue());
            if (base instanceof MonitoringSensor m) {
                return new SimpleStringProperty(m.getReadings().toString());
            }
            return new SimpleStringProperty("-");
        });

        // Colonna storico allarmi (solo MonitoringSensor)
        alarmHistoryColumn.setCellValueFactory(cell -> {
            Sensor base = getBaseSensor(cell.getValue());
            if (base instanceof MonitoringSensor m) {
                return new SimpleStringProperty(m.getAlarmHistory().toString());
            }
            return new SimpleStringProperty("-");
        });
    }

    // ==============================
    // FINSTRE SECONDARIE
    // ==============================

    // Apre finestra per installare coppia di sensori
    @FXML
    private void openInstallPairWindow() throws IOException {
        try {
            system.installSensorsS(); // verifica permessi stato

            FXMLLoader loader = new FXMLLoader(getClass().getResource("InstallPairView.fxml"));
            Parent root = loader.load();

            InstallPairController controller = loader.getController();
            controller.setSystem(system);
            controller.setMainController(this);

            Stage stage = new Stage();
            stage.setTitle("Aggiungi coppia di sensori");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // blocca main panel
            stage.showAndWait(); // attende chiusura finestra

            refreshSensorList(); // aggiorna tabella dopo chiusura
        } catch (IllegalStateException e) {
            logArea.appendText(e.getMessage() + "\n");
        }
    }

    // Apre finestra reset sensori
    @FXML
    private void openResetWindow() throws IOException {
        try {
            system.resetSensorsS(); // verifica permessi stato

            FXMLLoader loader = new FXMLLoader(getClass().getResource("ResetView.fxml"));
            Parent root = loader.load();

            ResetController controller = loader.getController();
            controller.setSystem(system);
            controller.setMainController(this);

            Stage stage = new Stage();
            stage.setTitle("Reset Sensori");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // blocca main panel
            stage.showAndWait();

            refreshSensorList(); // aggiorna tabella dopo chiusura
        } catch (IllegalStateException e) {
            logArea.appendText(e.getMessage() + "\n");
        }
    }

    // Apre finestra decorazione sensore
    @FXML
    private void openDecorateWindow() throws IOException {
        try {
            system.installModules(); // verifica permessi stato

            FXMLLoader loader = new FXMLLoader(getClass().getResource("DecorateSensorView.fxml"));
            Parent root = loader.load();

            DecorateSensorController controller = loader.getController();
            controller.setSystem(system);
            controller.setMainController(this);

            Stage stage = new Stage();
            stage.setTitle("Aggiungi Moduli Sensore");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IllegalStateException e) {
            logArea.appendText(e.getMessage() + "\n");
        }
    }

    // ==============================
    // SIMULAZIONE CICLO SENSORI
    // ==============================

    @FXML
    private void simulateCycle() {
        try {
            system.simulateSensorCycleS(); // verifica stato

            List<String> logs = system.simulateSensorCycle(); // esegue ciclo sensori
            alarmQueue(); // aggiorna lista allarmi attivi
            for (String log : logs) {
                addLog(log); // scrive log eventi
            }
            refreshSensorList(); // aggiorna tabella
        } catch (IllegalStateException e) {
            logArea.appendText(e.getMessage() + "\n");
        }
    }

    // ==============================
    // GESTIONE CODA ALLARMI
    // ==============================

    @FXML
    public void alarmQueue() {
        try {
            alarmQueueArea.clear();

            // Scorri tutti i sensori del sistema
            for (Sensor s : system.getSensors()) {
                Sensor base = getBaseSensor(s);

                // Solo MonitoringSensor attivi
                if (base instanceof MonitoringSensor ms && ms.isActive()) {
                    String line = ms.getId() + " - Ultimo allarme: " + ms.getLastAlarmTime();
                    alarmQueueArea.appendText(line + "\n");
                }
            }
        } catch (IllegalStateException e) {
            logArea.appendText(e.getMessage() + "\n");
        }
    }

    // ==============================
    // HELPER
    // ==============================

    // Ottiene il sensore base senza decorator
    private Sensor getBaseSensor(Sensor s) {
        Sensor current = s;
        while (current instanceof SensorDecorator dec) {
            current = dec.getWrappedSensor();
        }
        return current;
    }

    // ==============================
    // LOG GENERALE
    // ==============================

    // Scrive messaggi nell'area log
    public void addLog(String message) {
        logArea.appendText(message + "\n");
        logArea.setScrollTop(Double.MAX_VALUE);
    }

    // ==============================
    // CHIUSURA APPLICAZIONE
    // ==============================

    @FXML
    public void closeApp() {
        system.setCollaudoMode(); // imposta stato sicuro
        System.out.println("Chiusura applicazione...");
        FileManager.saveSensors(system.getSensors());
        FileManager.saveStatistics(system.getSensors());
        Platform.exit(); // chiude app
    }
}