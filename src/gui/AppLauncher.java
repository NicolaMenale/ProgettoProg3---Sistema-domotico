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

// ==============================
// LAUNCHER APPLICAZIONE GUI
// ==============================
//
// Classe principale che avvia il sistema domotico con GUI JavaFX.
// Carica sensori, statistiche e apre la finestra principale.
//
public class AppLauncher extends Application {

    // Riferimento statico condiviso al sistema domotico
    private static HomeSystem system;

    // ==============================
    // PUNTO DI INGRESSO MAIN
    // ==============================
    public static void main(String[] args) {

        // Creazione del sistema
        system = new HomeSystem();

        // Gestore globale delle eccezioni non catturate
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            System.out.println("ERRORE FATALE: " + throwable.getMessage());
            System.out.println("Salvataggio dati prima di terminare...");
            FileManager.saveSensors(system.getSensors());
            FileManager.saveStatistics(system.getSensors());
            throwable.printStackTrace();
        });

        // Imposta stato iniziale in modalità Collaudo
        system.setState(new TestModeState());

        // Caricamento sensori dal file
        List<Sensor> sensors = FileManager.loadSensors();
        system.setSensors(sensors);

        // Ricostruisce le coppie di monitoraggio / intervento
        system.rebuildMonitoringPairs();

        // Caricamento statistiche sensori
        FileManager.loadStatistics(sensors);

        // Avvia la GUI
        launch(args);
    }

    // ==============================
    // AVVIO GUI (JavaFX start)
    // ==============================
    @Override
    public void start(Stage stage) throws Exception {

        // Carica la scena principale da FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ControlPanelView.fxml"));
        Parent root = loader.load();

        // Passa il sistema domotico al controller
        ControlPanelController controller = loader.getController();
        controller.setSystem(system);

        // Imposta la scena e mostra la finestra principale
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Sistema Domotico");
        stage.show();

        // Gestione chiusura finestra: salva dati
        stage.setOnCloseRequest(event -> {
            System.out.println("Salvataggio dati prima di uscire...");
            FileManager.saveSensors(system.getSensors());
            FileManager.saveStatistics(system.getSensors());
        });
    }
}