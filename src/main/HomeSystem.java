package main;

import model.*;

import java.time.LocalDateTime;
import java.util.*;
import data.*;
import decorator.SensorDecorator;
import factory.*;

// =============================
// SISTEMA DOMOTICO PRINCIPALE
// =============================
//
// Gestisce sensori, coppie monitor/intervento, modalità e allarmi.
// Si occupa anche dell’interfaccia tra Factory e Decorator.
//
public class HomeSystem {

    private Map<Sensor, Sensor> monitoringToIntervention = new HashMap<>(); // mappa monitor -> intervento
    private List<Sensor> sensors = new ArrayList<>(); // tutti i sensori installati
    private SystemState currentState; // stato corrente del sistema (Collaudo, Attivato, ecc.)
    private Queue<Sensor> alarmQueue = new LinkedList<>(); // coda degli allarmi attivi
    private Queue<Sensor> stopAlarmQueue = new LinkedList<>(); // coda per stop allarmi
    List<Sensor> currentAlarms; // lista degli allarmi correnti

    // ==============================
    // COSTRUTTORE
    // ==============================
    public HomeSystem() {
        sensors = new ArrayList<>();
    }

    // ==============================
    // STATO DEL SISTEMA
    // ==============================
    public void setState(SystemState state) {
        this.currentState = state; // imposta lo stato corrente
    }

    public boolean isActivated() {
        return currentState.isActivated(); // true se in modalità attivata
    }

    // ==============================
    // MODALITÀ COLLAUDO
    // ==============================
    public void setCollaudoMode() {
        System.out.println("Modalità Collaudo: tutti i sensori disattivati.");
        for (Sensor s : sensors) {
            s.setModeString("OFFLINE"); // imposta stato sensore a offline
            s.deactivate(); // disattiva il sensore
        }
    }

    // ==============================
    // INSTALLAZIONE SENSORI
    // ==============================

    // Richiama la logica dello stato corrente
    public void installSensors() {
        currentState.installSensors(this);
    }

    // Installa una coppia monitor/intervento con threshold
    public void installMonitoringPair(String monitorType, String interventionType, int threshold) {

        // Crea il sensore di monitoraggio tramite factory
        SensorFactory monitorFactory = SensorFactoryProvider.getFactory(monitorType, threshold);
        Sensor monitorSensor = monitorFactory.createSensor(monitorType + (countSensorsByType(monitorType) + 1)); // ID
                                                                                                                 // progressivo

        // Crea il sensore di intervento tramite factory
        SensorFactory interventionFactory = SensorFactoryProvider.getFactory(interventionType);
        Sensor interventionSensor = interventionFactory
                .createSensor(interventionType + (countSensorsByType(interventionType) + 1)); // ID progressivo

        // Aggiunge i sensori alla lista del sistema
        sensors.add(monitorSensor);
        sensors.add(interventionSensor);

        // Registra la coppia monitor -> intervento
        addMonitoringPair(monitorSensor, interventionSensor);

        System.out.println("Coppia installata: " + monitorSensor.getId() + " ↔ " + interventionSensor.getId());
    }

    // Aggiunge una coppia monitor/intervento alla mappa interna
    public void addMonitoringPair(Sensor monitor, Sensor intervention) {

        // Controllo se il monitor è già associato
        if (monitoringToIntervention.containsKey(monitor)) {
            throw new IllegalArgumentException("Monitoraggio già associato.");
        }

        // Controllo se l'intervento è già associato
        if (monitoringToIntervention.containsValue(intervention)) {
            throw new IllegalArgumentException("Intervento già associato ad un altro monitoraggio.");
        }

        // Inserimento nella mappa
        monitoringToIntervention.put(monitor, intervention);
    }

    // Conta quanti sensori di un certo tipo sono presenti
    public long countSensorsByType(String prefix) {
        return sensors.stream().filter(s -> s.getId().startsWith(prefix)).count();
    }

    // =========================
    // MOSTRA SENSORI INSTALLATI
    // =========================

    // Chiama lo stato corrente per mostrare i sensori (delegazione allo State)
    public void showSensorsS() {
        currentState.showSensors(this);
    }

    // Stampa direttamente i sensori installati
    public void showSensors() {
        if (sensors.isEmpty()) {
            System.out.println("Nessun sensore installato.");
            return;
        }

        // ORDINA PER TIPO (Monitoring prima di Intervention) e poi per ID
        sensors.sort((s1, s2) -> {
            // Ottieni sensore base senza decorator
            Sensor base1 = (s1 instanceof SensorDecorator sd1) ? sd1.getBaseSensor() : s1;
            Sensor base2 = (s2 instanceof SensorDecorator sd2) ? sd2.getBaseSensor() : s2;

            boolean isMonitoring1 = base1 instanceof MonitoringSensor;
            boolean isMonitoring2 = base2 instanceof MonitoringSensor;

            if (isMonitoring1 && !isMonitoring2)
                return -1;
            if (!isMonitoring1 && isMonitoring2)
                return 1;

            return base1.getId().compareTo(base2.getId()); // ordine alfabetico per ID
        });

        System.out.println("\n--- SENSORI INSTALLATI ---");
        for (Sensor s : sensors) {
            Sensor base = s;
            List<String> modules = new ArrayList<>();

            // Se sensore decorato, ottieni base e moduli installati
            if (s instanceof SensorDecorator sd) {
                base = sd.getBaseSensor();
                modules = sd.getModules();
            }

            String type = (base instanceof MonitoringSensor) ? "Monitoraggio" : "Intervento";

            StringBuilder sb = new StringBuilder();
            sb.append("- ").append(base.getId()).append(" | Tipo: ").append(type);

            if (base instanceof MonitoringSensor ms) {
                sb.append(" | Threshold: ").append(ms.getThreshold());
            }

            if (!modules.isEmpty()) {
                sb.append(" | Moduli: ").append(String.join(", ", modules));
            }

            System.out.println(sb.toString());
        }
    }

    // =========================
    // RESET SENSORI
    // =========================

    // Chiama lo stato corrente per gestire reset sensore singolo
    public void resetSensorByIdS() {
        currentState.resetSensorByIdS(this);
    }

    // Reset di coppia di sensori tramite ID
    public boolean resetSensorById(String id) {
        for (int j = 0; j < sensors.size(); j++) {
            Sensor s = getBaseSensor(sensors.get(j)); // ottieni sensore base
            if (s.getId().equals(id)) {
                // reset monitoraggio + moduli
                sensors.get(j).reset();

                // reset intervento associato se esiste
                Sensor intervention = monitoringToIntervention.get(s);
                if (intervention != null) {
                    // trova l’indice del sensore decorato nella lista
                    int idx = sensors.indexOf(intervention);
                    if (idx != -1) {
                        sensors.get(idx).reset(); // reset intervento + moduli (se decorato)
                    }
                }

                return true;
            }
        }
        return false; // sensore non trovato
    }

    // Chiama lo stato corrente per gestire reset di tutti i sensori
    public void resetSensorsS() {
        currentState.resetSensorsS(this);
    }

    // Reset diretto di tutti i sensori (base + decorator)
    public void resetSensors(HomeSystem system) {
        system.getSensors().forEach(Sensor::reset);
    }

    // =========================
    // AGGIUNTA MODULI A SENSORI
    // =========================

    // Delegazione allo stato corrente per eventuali controlli o permessi
    public void installModules() {
        currentState.installModules(this);
    }

    // Aggiunge un modulo decoratore a un sensore identificato da ID
    public boolean addModuleToSensor(String sensorId, String moduleName) {
        int index = -1;
        Sensor selectedSensor = null;

        // Trova il sensore corrispondente nell'elenco
        for (int i = 0; i < sensors.size(); i++) {
            Sensor s = sensors.get(i);
            if (getBaseSensor(s).getId().equals(sensorId)) {
                index = i;
                selectedSensor = s;
                break;
            }
        }

        // Se il sensore non è stato trovato
        if (selectedSensor == null)
            return false;

        // Crea il modulo tramite ModuleRegistry (pattern Decorator)
        Sensor decorated = ModuleRegistry.createModule(moduleName, selectedSensor);

        // Sostituisce il sensore originale con quello decorato
        if (decorated != null) {
            sensors.set(index, decorated);
            return true;
        }

        return false;
    }

    // =========================
    // MOSTRA STATISTICHE SENSORI
    // =========================

    // Delegazione allo stato corrente
    public void showStatisticsS() {
        currentState.showStatisticsS(this);
    }

    // Stampa direttamente le statistiche di tutti i sensori (base + moduli)
    public void showStatistics() {
        if (sensors.isEmpty()) {
            System.out.println("Nessun sensore installato.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n--- STATISTICHE SENSORI ---\n\n");

        // Itera su tutti i sensori, includendo eventuali moduli (decorator)
        for (Sensor s : sensors) {
            sb.append(s.getStatistics()).append("\n");
        }

        System.out.println(sb.toString());
    }

    // =========================
    // MODALITÀ ATTIVATO
    // =========================

    // Imposta lo stato del sistema in modalità "Attivato"
    public void setActiveMode() {
        System.out.println("Modalità Attivato: tutti i sensori di monitoraggio attivi.");

        // Itera su tutte le coppie monitor → intervento
        for (Sensor monitor : monitoringToIntervention.keySet()) {
            Sensor intervention = monitoringToIntervention.get(monitor);

            // Ottiene i sensori "base" senza eventuali decorator
            Sensor baseMonitor = getBaseSensor(monitor);
            Sensor baseIntervention = getBaseSensor(intervention);

            // Imposta la modalità dei sensori come ONLINE
            monitor.setModeString("ONLINE");
            intervention.setModeString("ONLINE");

            // Gestisce solo le coppie corrette (Monitoring + Intervention)
            if (baseMonitor instanceof MonitoringSensor ms && baseIntervention instanceof InterventionSensor is) {

                boolean active = false;

                // Determina se il monitor è attivo basandosi sull'ultimo valore letto rispetto
                // al threshold
                if (!ms.getReadings().isEmpty()) {
                    double lastReading = ms.getReadings().get(ms.getReadings().size() - 1);
                    active = lastReading > ms.getThreshold();
                }

                // Aggiorna lo stato dei sensori
                ms.setActive(active);
                is.setActive(active);

                // Se attivo, inserisce il monitor nella coda degli allarmi
                if (active) {
                    enqueueAlarm(ms);

                    // Aggiorna l'ultimo timestamp di allarme
                    if (!ms.getAlarmHistory().isEmpty()) {
                        ms.setLastAlarmTime(ms.getAlarmHistory().get(ms.getAlarmHistory().size() - 1));
                    } else {
                        ms.setLastAlarmTime(LocalDateTime.now());
                    }
                }
            }
        }
    }

    // =========================
    // SIMULAZIONE MONITORAGGIO / INTERVENTI
    // =========================

    // Inoltra la chiamata alla modalità corrente
    public void simulateSensorCycleS() {
        currentState.simulateSensorCycleS(this);
    }

    // Simula un ciclo di letture dei sensori
    public List<String> simulateSensorCycle() {
        simulateSensorCycleS(); // richiama lo stato corrente
        List<String> logs = new ArrayList<>();

        // Mischia la lista dei sensori per simulazione casuale
        List<Sensor> shuffled = new ArrayList<>(sensors);
        Collections.shuffle(shuffled);

        for (Sensor s : shuffled) {
            Sensor base = getBaseSensor(s);
            if (base instanceof MonitoringSensor ms) {
                // Genera una lettura casuale intorno al threshold
                int value = (int) Math.round((Math.random() < 0.5)
                        ? ms.getThreshold() + Math.random() * 5
                        : ms.getThreshold() - Math.random() * 5);

                boolean wasActive = ms.isActive();
                ms.addReading(value); // aggiorna lo stato del sensore

                // Gestione attivazione/disattivazione allarme
                if (!wasActive && ms.isActive()) {
                    enqueueAlarm(ms);
                    logs.add("⚠ ALLARME ATTIVATO su sensore " + ms.getId());
                } else if (wasActive && !ms.isActive()) {
                    enqueueStopAlarm(ms);
                    logs.add("✔ ALLARME DISATTIVATO su sensore " + ms.getId());
                }
            }
        }

        // Stampa log a console
        for (String log : logs) {
            System.out.println(log);
        }

        // Aggiorna lista allarmi correnti
        currentAlarms = new ArrayList<>(alarmQueue);

        // Processa code di allarmi attivi e cessati
        processAlarms();
        processStopAlarms();

        // Salva dati su file
        FileManager.saveSensors(sensors);
        FileManager.saveStatistics(sensors);

        logs.add("------------------------------------------");
        System.out.println("------------------------------------------");
        return logs;
    }

    // Restituisce gli allarmi attivi
    public List<Sensor> getAlarmQueue() {
        return new ArrayList<>(currentAlarms);
    }

    // Inserisce un sensore nella coda degli allarmi attivi
    public void enqueueAlarm(Sensor monitor) {
        if (!alarmQueue.contains(monitor) && !stopAlarmQueue.contains(monitor)) {
            alarmQueue.add(monitor);
        }
    }

    // Inserisce un sensore nella coda degli allarmi cessati
    public void enqueueStopAlarm(Sensor monitor) {
        if (!stopAlarmQueue.contains(monitor)) {
            stopAlarmQueue.add(monitor);
        }
    }

    // Processa gli allarmi attivi in ordine temporale
    public void processAlarms() {
        List<Sensor> sortedAlarms = new ArrayList<>(alarmQueue);
        sortedAlarms.sort(Comparator.comparing(s -> ((MonitoringSensor) getBaseSensor(s)).getLastAlarmTime()));

        for (Sensor monitor : sortedAlarms) {
            Sensor base = getBaseSensor(monitor);
            Sensor intervention = monitoringToIntervention.get(base);

            if (intervention instanceof InterventionSensor inter && base instanceof MonitoringSensor ms) {
                if (!inter.isActive()) {
                    inter.trigger(); // aggiorna statistiche e timestamp
                    System.out.println("⚠ ALLARME ATTIVATO su sensore " + ms.getId());
                    System.out.println("Intervento attivato su sensore " + inter.getId() + " alle ore "
                            + inter.getLastActivationTime());
                }
            }
        }
    }

    // Processa gli allarmi cessati
    public void processStopAlarms() {
        List<Sensor> sortedStops = new ArrayList<>(stopAlarmQueue);
        sortedStops.sort(Comparator.comparing(s -> ((MonitoringSensor) getBaseSensor(s)).getLastAlarmTime()));

        for (Sensor monitor : sortedStops) {
            Sensor base = getBaseSensor(monitor);
            Sensor intervention = monitoringToIntervention.get(base);

            if (intervention instanceof InterventionSensor inter && base instanceof MonitoringSensor ms) {
                if (inter.isActive()) {
                    inter.deactivate();
                    System.out.println("✔ ALLARME DISATTIVATO su sensore " + ms.getId());
                    System.out.println("Disattivato intervento per: " + inter.getId() + " alle ore "
                            + java.time.LocalDateTime.now());
                    alarmQueue.remove(base);
                }
            }
        }
        stopAlarmQueue.clear();
    }

    // Svuota completamente le code di allarme
    public void clearAlarmQueues() {
        alarmQueue.clear();
        stopAlarmQueue.clear();
        System.out.println("Code di allarme resettate.");
    }

    // =========================
    // METODI HELPER
    // =========================

    // Imposta la lista dei sensori (usato ad esempio al caricamento da file)
    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }

    // Ricostruisce le coppie monitoraggio ↔ intervento dopo un caricamento da file
    public void rebuildMonitoringPairs() {
        List<Sensor> monitoringSensors = new ArrayList<>();
        List<Sensor> interventionSensors = new ArrayList<>();

        for (Sensor s : sensors) {
            Sensor base = getBaseSensor(s);

            if (base instanceof MonitoringSensor) {
                monitoringSensors.add(s);
            } else if (base instanceof InterventionSensor) {
                interventionSensors.add(s);
            }
        }

        // Associa i primi N sensori monitoraggio con i primi N sensori intervento
        for (int i = 0; i < Math.min(monitoringSensors.size(), interventionSensors.size()); i++) {
            addMonitoringPair(monitoringSensors.get(i), interventionSensors.get(i));
        }
    }

    // Restituisce info sintetiche dei sensori per la GUI o log
    public List<String> getSensorInfo() {
        List<String> info = new ArrayList<>();
        for (Sensor s : sensors) {
            Sensor base = getBaseSensor(s);
            String type = (base instanceof MonitoringSensor) ? "Monitoraggio" : "Intervento";
            info.add(base.getId() + "  | Tipo: " + type);
        }
        return info;
    }

    // Restituisce il sensore base, "sgrossando" eventuali decorator
    public Sensor getBaseSensor(Sensor sensor) {
        Sensor base = sensor;
        while (base instanceof SensorDecorator decorator) {
            base = decorator.getWrappedSensor();
        }
        return base;
    }

    // Restituisce l’ID del sensore dato un indice nella lista interna
    public String getSensorIdByIndex(int index) {
        if (index < 0 || index >= sensors.size())
            return null;
        return getBaseSensor(sensors.get(index)).getId();
    }

    // Restituisce i moduli disponibili per un sensore (usato dalla GUI Decorate)
    public List<String> getAvailableModules(String sensorId) {
        Sensor sensor = sensors.stream()
                .filter(s -> getBaseSensor(s).getId().equals(sensorId))
                .findFirst()
                .orElse(null);

        if (sensor == null)
            return new ArrayList<>();

        // Suppone che esista internamente un ModuleRegistry
        return ModuleRegistry.getAvailableModules(sensor);
    }

    // Restituisce la lista completa dei sensori
    public List<Sensor> getSensors() {
        return sensors;
    }

    public void printPairSensor() {
        int index = 1;
        for (Sensor monitor : monitoringToIntervention.keySet()) {
            Sensor baseMonitor = getBaseSensor(monitor);
            Sensor intervention = monitoringToIntervention.get(baseMonitor);
            Sensor baseIntervention = getBaseSensor(intervention);

            System.out.println(index + ". " + baseMonitor.getId() + " (Monitoraggio) ↔ " + baseIntervention.getId()
                    + " (Intervento)");
            index++;
        }
    }
}