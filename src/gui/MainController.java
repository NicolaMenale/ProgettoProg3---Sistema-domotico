package gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import main.HomeSystem;
import data.*;

public class MainController {
    private HomeSystem system;

    public void setSystem(HomeSystem system) {
        this.system = system;
    }

    @FXML
    private void activateMode() {
        system.setActiveMode();
    }

    @FXML
    private void testMode() {
        system.setCollaudoMode();
    }

    @FXML
    public void closeApp() {
        System.out.println("Chiusura applicazione...");
        FileManager.saveSensors(system.getSensors());
        FileManager.saveStatistics(system.getSensors());
        Platform.exit(); // chiude la finestra e termina l'app
    }
}