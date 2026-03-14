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
import model.*;

import java.io.IOException;
import java.util.*;

import data.*;
import decorator.SensorDecorator;

public class ControlPanelController {
    private HomeSystem system;

    @FXML
    private Label modeLabel;
    @FXML
    private TableView<Sensor> statsTableView;
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
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private TextField thresholdField;
    @FXML
    private TextArea logArea;
    @FXML
    private TextArea alarmQueueArea;

    public void setSystem(HomeSystem system) {
        this.system = system;
        setupTable();
        refreshSensorList();
    }

    @FXML
    private void activateMode(ActionEvent event) throws Exception {
        system.setState(new ActiveModeState());
        system.setActiveMode();
        modeLabel.setText("MODALITÀ: ATTIVATO");
        refreshSensorList();
    }

    @FXML
    private void testMode(ActionEvent event) throws Exception {
        system.setState(new TestModeState());
        system.setCollaudoMode();
        modeLabel.setText("MODALITÀ: COLLAUDO");
        refreshSensorList();
    }

    public void refreshSensorList() {
        if (system == null)
            return;
        ObservableList<Sensor> data = FXCollections.observableArrayList(system.getSensors());
        statsTableView.setItems(data);
        statsTableView.refresh();
    }

    private void setupTable() {
        // Colonne base
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        // Tipo (Monitoring o Intervention)
        typeColumn.setCellValueFactory(cell -> {
            Sensor base = getBaseSensor(cell.getValue());
            String tipo = (base instanceof MonitoringSensor) ? "Monitoring" : "Intervention";
            return new SimpleStringProperty(tipo);
        });

        // Attivo (YES/NO)
        activeColumn.setCellValueFactory(cell -> {
            Sensor base = getBaseSensor(cell.getValue());
            String val = base.isActive() ? "YES" : "NO";
            return new SimpleStringProperty(val);
        });

        // Threshold
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

        // Moduli
        moduleColumn.setCellValueFactory(cell -> {
            Sensor s = cell.getValue();
            List<String> modules = new ArrayList<>();
            Sensor current = s;
            while (current instanceof SensorDecorator dec) {
                modules.add(dec.getModuleName());
                current = dec.getWrappedSensor();
            }

            // se vuoi ordine base -> ultimo modulo
            Collections.reverse(modules);

            String val = modules.isEmpty() ? "-" : String.join(", ", modules);
            return new SimpleStringProperty(val);
        });

        // Allarmi / Attivazioni
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

        readingsColumn.setCellValueFactory(cell -> {
            Sensor base = getBaseSensor(cell.getValue());
            if (base instanceof MonitoringSensor m) {
                return new SimpleStringProperty(m.getReadings().toString());
            }

            return new SimpleStringProperty("-");
        });

        alarmHistoryColumn.setCellValueFactory(cell -> {
            Sensor base = getBaseSensor(cell.getValue());
            if (base instanceof MonitoringSensor m) {
                return new SimpleStringProperty(m.getAlarmHistory().toString());
            }

            return new SimpleStringProperty("-");
        });

    }

    private Sensor getBaseSensor(Sensor s) {
        Sensor current = s;
        while (current instanceof SensorDecorator dec) {
            current = dec.getWrappedSensor();
        }
        return current;
    }

    @FXML
    private void openInstallPairWindow() throws IOException {
        if (system.isActivated()) {
            addLog("Modalità Attivato. Creazione sensori non permesso.");
            return; // blocca apertura finestra
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("InstallPairView.fxml"));
        Parent root = loader.load();

        // Prendi il controller della finestra secondaria
        InstallPairController controller = loader.getController();
        controller.setSystem(system); // Passa il riferimento al sistema principale
        controller.setMainController(this);

        // Crea una nuova finestra
        Stage stage = new Stage();
        stage.setTitle("Aggiungi coppia di sensori");
        stage.setScene(new Scene(root));
        stage.initModality(Modality.APPLICATION_MODAL); // blocca il main panel finché la finestra è aperta
        stage.showAndWait(); // aspetta che l’utente chiuda la finestra

        // Dopo la chiusura, aggiorna la lista dei sensori nel main panel
        refreshSensorList();
    }

    @FXML
    private void openResetWindow() throws IOException {
        if (system.isActivated()) {
            addLog("Modalità Attivato. Reset non permesso.");
            return; // blocca apertura finestra
        }

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

        // Dopo chiusura, aggiorna lista sensori
        refreshSensorList();
    }

    @FXML
    private void openDecorateWindow() throws IOException {
        if (system.isActivated()) {
            addLog("Modalità Attivato. Aggiunta moduli non permesso.");
            return; // blocca apertura finestra
        }

        FXMLLoader loader = new FXMLLoader(getClass().getResource("DecorateSensorView.fxml"));
        Parent root = loader.load();

        DecorateSensorController controller = loader.getController();

        controller.setSystem(system);
        controller.setMainController(this);

        Stage stage = new Stage();
        stage.setTitle("Aggiungi Moduli Sensore");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void simulateCycle() {
        List<String> logs = system.simulateSensorCycle();
        alarmQueue();
        for (String log : logs) {
            addLog(log);
        }
        refreshSensorList();
    }

    @FXML
    public void alarmQueue() {
        alarmQueueArea.clear();

        for (Sensor s : system.getAlarmQueue()) {
            Sensor base = getBaseSensor(s); // gestisce eventuali decorator
            String line = base.getId();

            if (s instanceof MonitoringSensor ms) {
                line += " - Ultimo allarme: " + ms.getLastAlarmTime();
            }

            alarmQueueArea.appendText(line + "\n");
        }
    }

    public void addLog(String message) {
        logArea.appendText(message + "\n");
        logArea.setScrollTop(Double.MAX_VALUE);
    }

    @FXML
    public void closeApp() {
        system.setCollaudoMode();
        System.out.println("Chiusura applicazione...");
        FileManager.saveSensors(system.getSensors());
        FileManager.saveStatistics(system.getSensors());
        Platform.exit(); // chiude la finestra e termina l'app
    }
}