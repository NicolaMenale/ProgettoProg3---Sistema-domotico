package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.HomeSystem;
import main.TestModeState;
import data.FileManager;
import model.Sensor;
import java.util.List;

public class AppLauncher extends Application {

    // Riferimento statico condiviso al sistema
    private static HomeSystem system;

    public static void main(String[] args) {
        // 1️⃣ Creazione del sistema
        system = new HomeSystem();
        system.setState(new TestModeState());
        // 2️⃣ Caricamento sensori
        List<Sensor> sensors = FileManager.loadSensors();
        system.setSensors(sensors);
        system.rebuildMonitoringPairs();

        // 3️⃣ Caricamento statistiche
        FileManager.loadStatistics(sensors);

        // 4️⃣ Lancia la GUI
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Carica il file FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ControlPanelView.fxml"));
        Parent root = loader.load();

        // Passa il sistema al controller
        ControlPanelController controller = loader.getController();
        controller.setSystem(system);

        // Mostra la scena
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Sistema Domotico");
        stage.show();

        stage.setOnCloseRequest(event -> {
            // Salva dati prima di chiudere
            System.out.println("Salvataggio dati prima di uscire...");
            FileManager.saveSensors(system.getSensors());
            FileManager.saveStatistics(system.getSensors());
        });
    }
}