package gui;

import javafx.fxml.*;
import javafx.event.*;
import javafx.scene.control.*;
import javafx.application.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import main.HomeSystem;
import data.*;

public class ControlPanelController {
    private HomeSystem system;

    @FXML
    private ListView<String> sensorListView;

    public void setSystem(HomeSystem system) {
        this.system = system;
        refreshSensorList();
    }

    @FXML
    private void activateMode(ActionEvent event) throws Exception {
        system.setActiveMode();
    }

    @FXML
    private void testMode(ActionEvent event) throws Exception {
        system.setCollaudoMode();
    }

    private void refreshSensorList() {
        if (system == null)
            return;

        // ottiene lista sensori come stringhe (es. ID)
        ObservableList<String> sensorNames = FXCollections.observableArrayList(
                system.getSensorInfo() // ti aspetti List<String> qui
        );

        sensorListView.setItems(sensorNames);
    }

    @FXML
    public void closeApp() {
        System.out.println("Chiusura applicazione...");
        FileManager.saveSensors(system.getSensors());
        FileManager.saveStatistics(system.getSensors());
        Platform.exit(); // chiude la finestra e termina l'app
    }
}