package main;

import java.util.*;
import data.*;
import model.Sensor;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    static void main() {

        // ===== Creazione sistema =====
        HomeSystem system = new HomeSystem();
        Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
            System.out.println("ERRORE FATALE: " + throwable.getMessage());
            System.out.println("Salvataggio dati prima di terminare...");
            FileManager.saveSensors(system.getSensors());
            FileManager.saveStatistics(system.getSensors());
            throwable.printStackTrace();
        });

        // 1️⃣ Caricamento sensori
        List<Sensor> sensors = FileManager.loadSensors();
        system.setSensors(sensors);
        system.rebuildMonitoringPairs();
        // 2️⃣ Caricamento statistiche
        FileManager.loadStatistics(sensors);

        boolean exit = false;

        while (!exit) {
            System.out.println("\n=== SMART HOME SYSTEM ===");
            System.out.println("1. Modalità Collaudo");
            System.out.println("2. Modalità Attivato");
            System.out.println("0. Esci");
            System.out.print("Scelta: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> testMode(system);
                case 2 -> activeMode(system);
                case 0 -> {
                    FileManager.saveSensors(system.getSensors());
                    FileManager.saveStatistics(system.getSensors());
                    exit = true;
                }
                default -> System.out.println("Scelta non valida.");
            }
        }

        System.out.println("Sistema terminato.");
    }

    // =========================
    // MODALITÀ COLLAUDO
    // =========================
    private static void testMode(HomeSystem system) {

        system.setState(new TestModeState());

        // Thread per statistiche periodiche
        /*
         * Thread statsThread = new Thread(() -> {
         * while (!Thread.currentThread().isInterrupted()) {
         * try {
         * Thread.sleep(5000); // ogni 5 secondi
         * } catch (InterruptedException e) {
         * break; // uscita dal thread se interrotto
         * }
         * 
         * List<Sensor> sensors = system.getSensors();
         * if (!sensors.isEmpty()) {
         * System.out.println("\n--- STATISTICHE PERIODICHE SENSORI ---");
         * system.showStatistics();
         * }
         * }
         * });
         * statsThread.setDaemon(true); // non blocca l'uscita
         * statsThread.start();
         */
        system.setCollaudoMode();
        boolean back = false;
        system.clearAlarmQueues();
        while (!back) {

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
                case 1 -> installSensors(system);
                case 2 -> system.showSensors();
                case 3 -> resetSensorsMenu(system);
                case 4 -> decorateSensor(system);
                case 5 -> system.showStatistics();
                case 0 -> back = true;
                default -> System.out.println("Scelta non valida.");
            }
        }
    }

    // =========================
    // INSTALLAZIONE SENSORI (FACTORY)
    // =========================
    private static void installSensors(HomeSystem system) {

        boolean back = false;

        while (!back) {
            System.out.println("\n--- INSTALLAZIONE SENSORE ---");
            System.out.println("1. Installa coppie di Sensori");
            System.out.println("0. Torna indietro");
            System.out.print("Scelta: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> { // Sensore di monitoraggio
                    String[] monitorTypes = { "TEMPERATURE", "ELECTRICITY", "SMOKE", "GAS", "MOVEMENT" };
                    String[] interventionTypes = { "AIRCONDITIONER", "POWERCUT", "SIREN", "VENT", "LOCK" };

                    System.out.println("Tipi disponibili:");
                    for (int i = 0; i < monitorTypes.length; i++) {
                        System.out.println((i + 1) + ". " + monitorTypes[i] + " " + interventionTypes[i]);
                    }
                    System.out.println("0. Torna indietro");
                    System.out.print("Scegli tipo: ");
                    int Choice = scanner.nextInt();

                    if (Choice == 0)
                        break;
                    if (Choice < 1 || Choice > monitorTypes.length) {
                        System.out.println("Scelta non valida.");
                        break;
                    }
                    String monitorType = monitorTypes[Choice - 1];
                    String interventionType = interventionTypes[Choice - 1];

                    // --- Inserimento threshold ---
                    System.out.print("Inserisci valore soglia (threshold) per il sensore " + monitorType + ": ");
                    double threshold = scanner.nextDouble();

                    try {
                        system.installMonitoringPair(monitorType, interventionType, threshold);
                    } catch (IllegalArgumentException e) {
                        System.out.println("Errore: " + e.getMessage());
                    }
                }
                case 0 -> back = true;
                default -> System.out.println("Scelta non valida.");
            }
        }
    }

    // ==========================
    // DECORAZIONE SENSORI (MULTIPLI MODULI)
    // ==========================
    private static void decorateSensor(HomeSystem system) {

        boolean back = false;

        while (!back) {
            List<String> sensorInfo = system.getSensorInfo();
            if (sensorInfo.isEmpty()) {
                System.out.println("Nessun sensore installato.");
                return;
            }

            System.out.println("\n--- SENSORI INSTALLATI ---");
            for (int i = 0; i < sensorInfo.size(); i++) {
                System.out.println((i + 1) + ". " + sensorInfo.get(i));
            }
            System.out.println("0. Torna indietro");

            System.out.print("Seleziona sensore da decorare: ");
            int sensorChoice = scanner.nextInt();
            if (sensorChoice == 0)
                break;
            if (sensorChoice < 1 || sensorChoice > sensorInfo.size()) {
                System.out.println("Scelta non valida.");
                continue;
            }

            String sensorId = system.getSensorIdByIndex(sensorChoice - 1);

            boolean addingModules = true;
            while (addingModules) {
                List<String> availableModules = system.getAvailableModules(sensorId);
                if (availableModules.isEmpty()) {
                    System.out.println("Tutti i moduli disponibili sono già installati su " + sensorId);
                    break;
                }

                System.out.println("\nModuli disponibili per " + sensorId + ":");
                for (int i = 0; i < availableModules.size(); i++) {
                    System.out.println((i + 1) + ". " + availableModules.get(i));
                }
                System.out.println("0. Torna indietro");
                System.out.print("Seleziona modulo da aggiungere (0 per finire): ");
                int moduleChoice = scanner.nextInt();
                if (moduleChoice == 0)
                    break;
                if (moduleChoice < 1 || moduleChoice > availableModules.size()) {
                    System.out.println("Scelta modulo non valida.");
                    continue;
                }

                String moduleName = availableModules.get(moduleChoice - 1);
                boolean success = system.addModuleToSensor(sensorId, moduleName);

                if (success) {
                    System.out.println("Modulo " + moduleName + " aggiunto a " + sensorId);
                } else {
                    System.out.println("Errore nell'aggiunta modulo.");
                }
            }
        }
    }

    // ==========================
    // MENU RESET SENSORI
    // ==========================
    private static void resetSensorsMenu(HomeSystem system) {
        boolean back = false;

        while (!back) {
            System.out.println("\n--- RESET SENSORI ---");
            System.out.println("1. Reset singolo sensore");
            System.out.println("2. Reset tutti i sensori");
            System.out.println("3. Cancellazione Totale");
            System.out.println("0. Torna indietro");
            System.out.print("Scelta: ");

            int choice = scanner.nextInt();

            switch (choice) {
                case 1 -> {
                    // Ottieni lista dei sensori come info testuali
                    List<String> sensorInfo = system.getSensorInfo();
                    if (sensorInfo.isEmpty()) {
                        System.out.println("Nessun sensore installato.");
                        continue;
                    }

                    System.out.println("\nSeleziona sensore da resettare:");
                    for (int i = 0; i < sensorInfo.size(); i++) {
                        System.out.println((i + 1) + ". " + sensorInfo.get(i));
                    }
                    System.out.println("0. Annulla");
                    System.out.print("Scelta: ");

                    int sensorChoice = scanner.nextInt();
                    if (sensorChoice == 0)
                        break;
                    if (sensorChoice < 1 || sensorChoice > sensorInfo.size()) {
                        System.out.println("Scelta non valida.");
                        continue;
                    }

                    String sensorId = system.getSensorIdByIndex(sensorChoice - 1);
                    if (system.resetSensorById(sensorId)) {
                        System.out.println("Sensore " + sensorId + " resettato (base + moduli).");
                    } else {
                        System.out.println("Errore nel resettare il sensore " + sensorId);
                    }
                }

                case 2 -> {
                    system.resetSensors(); // HomeSystem gestisce tutto internamente
                    System.out.println("Tutti i sensori e moduli sono stati resettati.");
                }

                case 3-> {
                    FileManager.clearDataFiles();
                    System.out.println("Sistema resettato.");
                }

                case 0 -> back = true;

                default -> System.out.println("Scelta non valida.");
            }
        }
    }

    // =========================
    // MODALITÀ ATTIVATO
    // =========================
    private static void activeMode(HomeSystem system) {
        system.setState(new ActiveModeState());
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
                case 1 -> system.simulateSensorCycle(); // HomeSystem gestisce internamente le letture e gli allarmi
                case 2 -> system.showSensors(); // HomeSystem gestisce la stampa dei sensori
                case 3 -> system.showStatistics();
                case 0 -> back = true;
                default -> System.out.println("Scelta non valida.");
            }
        }
    }
}