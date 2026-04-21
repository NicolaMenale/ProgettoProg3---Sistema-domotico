package main;

import java.util.*;

import data.*;
import models.*;
import state.*;

// ==============================
// CLASSE PRINCIPALE: Main
// ==============================

/**
 * Entry point del sistema domotico.
 * Gestisce modalità terminale, caricamento/salvataggio sensori e statistiche,
 * e l’interazione con l’utente tramite Scanner.
 */
public class Main {

    // Scanner per input da terminale
    private static final Scanner scanner = new Scanner(System.in);

    // ==============================
    // METODO MAIN
    // ==============================
    public static void main(String[] args) {

        // Creazione sistema
        HomeSystem system = new HomeSystem();

        // Gestione globale delle eccezioni non catturate
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            System.out.println("ERRORE FATALE: " + throwable.getMessage());
            System.out.println("Salvataggio dati prima di terminare...");
            FileManager.saveSensors(system.getSensors());
            FileManager.saveStatistics(system.getSensors());
            throwable.printStackTrace();
        });

        List<Sensor> sensors = FileManager.loadSensors(); // Caricamento sensori salvati da file
        system.setSensors(sensors);
        system.rebuildMonitoringPairs(); // ricostruisce le coppie monitoraggio-intervento
        FileManager.loadStatistics(sensors); // Caricamento statistiche dei sensori
        boolean exit = false; // Flag di uscita dal menu principale

        // MENU PRINCIPALE
        while (!exit) {
            System.out.println("\n=== Sistema Domotico ===");
            System.out.println("1. Modalità Collaudo");
            System.out.println("2. Modalità Attivato");
            System.out.println("0. Esci");
            System.out.print("Scelta: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> testMode(system); // entra in modalità collaudo
                case 2 -> activeMode(system); // entra in modalità attivato
                case 0 -> { // esce dal programma e salva
                    FileManager.saveSensors(system.getSensors());
                    FileManager.saveStatistics(system.getSensors());
                    exit = true;
                }
                default -> System.out.println("Scelta non valida.");
            }
        }
        System.out.println("Sistema terminato.");
    }

    // ==============================
    // MODALITÀ COLLAUDO
    // ==============================
    private static void testMode(HomeSystem system) {

        // Imposta lo stato corrente su TestMode
        system.setState(new CollaudoState());
        system.setCollaudoMode();

        boolean back = false;
        while (!back) {

            // Stampa menu collaudo
            System.out.println("\n--- MODALITÀ COLLAUDO ---");
            System.out.println("1. Installa nuovi sensori");
            System.out.println("2. Mostra sensori installati");
            System.out.println("3. Reset");
            System.out.println("4. Aggiungi modulo a un sensore");
            System.out.println("5. Mostra statistiche sensori");
            System.out.println("0. Torna indietro");
            System.out.print("Scelta: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> installSensors(system); // installazione coppie sensori
                case 2 -> system.showSensorsS(); // stampa sensori
                case 3 -> resetSensorsMenu(system); // menu reset
                case 4 -> decorateSensor(system); // aggiunta moduli
                case 5 -> system.showStatisticsS(); // mostra statistiche
                case 0 -> back = true;
                default -> System.out.println("Scelta non valida.");
            }
        }
    }

    // ==============================
    // INSTALLAZIONE SENSORI (FACTORY)
    // ==============================
    private static void installSensors(HomeSystem system) {

        // Chiede al sistema di iniziare l’installazione (può controllare lo stato e bloccare se non permesso)
        system.installSensorsS();

        boolean back = false; // flag per uscire dal menu

        while (!back) {

            // Stampa il menu di installazione
            System.out.println("\n--- INSTALLAZIONE SENSORE ---");
            System.out.println("1. Installa coppie di Sensori");
            System.out.println("0. Torna indietro");
            System.out.print("Scelta: ");

            // Legge la scelta dell’utente
            int choice = scanner.nextInt();

            switch (choice) {

                case 1 -> { // installazione coppia di sensori

                    // Array dei tipi disponibili per monitoraggio e intervento
                    String[] monitorTypes = { "TEMPERATURE", "ELECTRICITY", "SMOKE", "GAS", "MOVEMENT" };
                    String[] interventionTypes = { "AIRCONDITIONER", "POWERCUT", "SIREN", "VENT", "LOCK" };

                    // Stampa i tipi disponibili
                    System.out.println("Tipi disponibili:");
                    for (int i = 0; i < monitorTypes.length; i++) {
                        System.out.println((i + 1) + ". " + monitorTypes[i] + " " + interventionTypes[i]);
                    }
                    System.out.println("0. Torna indietro");
                    System.out.print("Scegli tipo: ");

                    // Legge la scelta dell’utente
                    int Choice = scanner.nextInt();

                    // Se l’utente sceglie 0, esce dal case
                    if (Choice == 0)
                        break;

                    // Controlla che la scelta sia valida
                    if (Choice < 1 || Choice > monitorTypes.length) {
                        System.out.println("Scelta non valida.");
                        break;
                    }

                    // Ottiene i tipi selezionati
                    String monitorType = monitorTypes[Choice - 1];
                    String interventionType = interventionTypes[Choice - 1];

                    // Chiede all’utente il valore di soglia per il sensore di monitoraggio
                    System.out.print("Inserisci valore soglia (threshold) per il sensore " + monitorType + ": ");
                    int threshold = scanner.nextInt();
                    
                    // Installa la coppia di sensori tramite HomeSystem
                    system.installMonitoringPair(monitorType, interventionType, threshold);
                }

                case 0 -> back = true; // esce dal menu

                default -> System.out.println("Scelta non valida."); // input non valido
            }
        }
    }

    // ==============================
    // MENU RESET SENSORI
    // ==============================
    private static void resetSensorsMenu(HomeSystem system) {

        // Controlla se lo stato attuale del sistema permette il reset
        system.resetS();

        boolean back = false; // flag per uscire dal menu

        while (!back) {

            // Stampa il menu reset sensori
            System.out.println("\n--- RESET SENSORI ---");
            System.out.println("1. Reset coppia sensori");
            System.out.println("2. Reset tutti i sensori");
            System.out.println("3. Cancellazione Totale");
            System.out.println("0. Torna indietro");
            System.out.print("Scelta: ");

            // Legge la scelta dell’utente
            int choice = scanner.nextInt();

            switch (choice) {

                case 1 -> { // reset coppia di sensori

                    // Ottiene la lista dei sensori installati
                    List<String> sensorInfo = system.getSensorInfo();

                    // Se non ci sono sensori, informa l’utente e ricomincia il loop
                    if (sensorInfo.isEmpty()) {
                        System.out.println("Nessun sensore installato.");
                        continue;
                    }

                    // Mostra la lista dei sensori
                    System.out.println("\nSeleziona sensore da resettare:");
                    system.printPairSensor();
                    System.out.println("0. Annulla");
                    System.out.print("Scelta: ");

                    // Legge la scelta dell’utente
                    int sensorChoice = scanner.nextInt();

                    // Se utente sceglie 0, esce dal case
                    if (sensorChoice == 0)
                        break;

                    // Controlla che la scelta sia valida
                    if (sensorChoice < 1 || sensorChoice > sensorInfo.size()) {
                        System.out.println("Scelta non valida.");
                        continue;
                    }

                    // Ottiene l’ID del sensore selezionato
                    String sensorId = system.getSensorIdByIndex(sensorChoice - 1);

                    // Resetta il sensore tramite HomeSystem
                    if (sensorId != null) {
                        boolean success = system.resetPairById(sensorId);
                        if (success)
                            System.out.println("Coppia Resettata");
                        else
                            System.out.println("Errore nel resettare il sensore " + sensorId);
                    }
                }

                case 2 -> { // reset tutti i sensori
                    // HomeSystem gestisce internamente il reset completo
                    system.resetSensors(system);
                    System.out.println("Tutti i sensori e moduli sono stati resettati.");
                }

                case 3 -> { // cancellazione totale
                    system.resetData(system);
                    System.out.println("Sistema resettato.");
                }

                case 0 -> back = true; // esce dal menu

                default -> System.out.println("Scelta non valida."); // input non valido
            }
        }
    }

    // ==============================
    // DECORAZIONE SENSORI (MULTIPLI MODULI)
    // ==============================
    private static void decorateSensor(HomeSystem system) {

        // Controlla se lo stato attuale del sistema permette di installare moduli
        system.installModules();

        boolean back = false; // flag per uscire dal menu decorazione

        while (!back) {

            // Ottiene le informazioni testuali dei sensori installati
            List<String> sensorInfo = system.getSensorInfo();

            // Se non ci sono sensori, termina il metodo
            if (sensorInfo.isEmpty()) {
                System.out.println("Nessun sensore installato.");
                return;
            }

            // Stampa lista dei sensori installati
            System.out.println("\n--- SENSORI INSTALLATI ---");
            for (int i = 0; i < sensorInfo.size(); i++) {
                System.out.println((i + 1) + ". " + sensorInfo.get(i));
            }
            System.out.println("0. Torna indietro");

            // Chiede all’utente quale sensore decorare
            System.out.print("Seleziona sensore da decorare: ");
            int sensorChoice = scanner.nextInt();

            // Se utente sceglie 0, esce dal menu
            if (sensorChoice == 0)
                break;

            // Controlla che la scelta sia valida
            if (sensorChoice < 1 || sensorChoice > sensorInfo.size()) {
                System.out.println("Scelta non valida.");
                continue;
            }

            // Ottiene l’ID del sensore selezionato
            String sensorId = system.getSensorIdByIndex(sensorChoice - 1);

            // Loop per aggiungere più moduli a un singolo sensore
            boolean addingModules = true;
            while (addingModules) {

                // Ottiene i moduli disponibili per il sensore selezionato
                List<String> availableModules = system.getAvailableModules(sensorId);

                // Se non ci sono moduli disponibili, esce dal loop
                if (availableModules.isEmpty()) {
                    System.out.println("Tutti i moduli disponibili sono già installati su " + sensorId);
                    break;
                }

                // Stampa i moduli disponibili
                System.out.println("\nModuli disponibili per " + sensorId + ":");
                for (int i = 0; i < availableModules.size(); i++) {
                    System.out.println((i + 1) + ". " + availableModules.get(i));
                }
                System.out.println("0. Torna indietro");

                // Chiede all’utente quale modulo aggiungere
                System.out.print("Seleziona modulo da aggiungere (0 per finire): ");
                int moduleChoice = scanner.nextInt();

                // Se utente sceglie 0, esce dal loop aggiunta moduli
                if (moduleChoice == 0)
                    break;

                // Controlla che la scelta del modulo sia valida
                if (moduleChoice < 1 || moduleChoice > availableModules.size()) {
                    System.out.println("Scelta modulo non valida.");
                    continue;
                }

                // Ottiene il nome del modulo selezionato
                String moduleName = availableModules.get(moduleChoice - 1);

                // Aggiunge il modulo al sensore tramite HomeSystem
                boolean success = system.addModuleToSensor(sensorId, moduleName);

                // Comunica all’utente il risultato dell’operazione
                if (success)
                    System.out.println("Modulo " + moduleName + " aggiunto a " + sensorId);
                else
                    System.out.println("Errore nell'aggiunta modulo.");
            }
        }
    }

    // ==============================
    // MODALITÀ ATTIVATO
    // ==============================
    private static void activeMode(HomeSystem system) {

        // Imposta lo stato attivo
        system.setState(new AttivoState());
        system.setActiveMode();

        boolean back = false;

        while (!back) {
            System.out.println("\n--- MODALITÀ ATTIVATO ---");
            System.out.println("1. Simula ciclo sensori");
            System.out.println("2. Mostra sensori installati");
            System.out.println("3. Mostra statistiche sensori");
            System.out.println("0. Torna indietro");
            System.out.print("Scelta: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> system.simulateSensorCycleS(); // gestisce letture/allarmi
                case 2 -> system.showSensorsS(); // stampa sensori
                case 3 -> system.showStatisticsS(); // mostra statistiche
                case 0 -> back = true;
                default -> System.out.println("Scelta non valida.");
            }
        }
    }
}