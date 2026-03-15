package main;

import model.*;

import java.time.LocalDateTime;
//import java.time.LocalDateTime;
import java.util.*;
import data.*;
import decorator.SensorDecorator;
import factory.*;

public class HomeSystem {
    private Map<Sensor, Sensor> monitoringToIntervention = new HashMap<>();
    private List<Sensor> sensors = new ArrayList<>();
    private SystemState currentState;
    private Queue<Sensor> alarmQueue = new LinkedList<>();
    private Queue<Sensor> stopAlarmQueue = new LinkedList<>();
    List<Sensor> currentAlarms;

    // ===== Costruttore =====
    public HomeSystem() {
        sensors = new ArrayList<>();
    }

    // ===== Cambio stato =====
    public void setState(SystemState state) {
        this.currentState = state;
    }

    public boolean isActivated() {
        return currentState.isActivated();
    }

    // =========================
    // MODALITÀ COLLAUDO
    // =========================

    public void setCollaudoMode() {
        System.out.println("Modalità Collaudo: tutti i sensori disattivati.");
        for (Sensor s : sensors) {
            s.setModeString("OFFLINE");
            s.deactivate();
        }
    }

    // =========================
    // INSTALLAZIONE SENSORI
    // =========================
    public void installMonitoringPair(String monitorType, String interventionType, int threshold) {
        // Crea i sensori tramite Factory internamente
        SensorFactory monitorFactory = SensorFactoryProvider.getFactory(monitorType, threshold);
        Sensor monitorSensor = monitorFactory.createSensor(monitorType + (countSensorsByType(monitorType) + 1));

        SensorFactory interventionFactory = SensorFactoryProvider.getFactory(interventionType);
        Sensor interventionSensor = interventionFactory
                .createSensor(interventionType + (countSensorsByType(interventionType) + 1));

        // Installa i sensori
        installSensor(monitorSensor);
        installSensor(interventionSensor);

        // Registra la coppia
        addMonitoringPair(monitorSensor, interventionSensor);

        System.out.println("Coppia installata: " + monitorSensor.getId() + " ↔ " + interventionSensor.getId());
    }

    public void installSensor(Sensor sensor) {
        if (currentState == null) {
            throw new IllegalStateException("Stato del sistema non impostato!");
        }
        currentState.installSensor(this, sensor);
    }

    public void addMonitoringPair(Sensor monitor, Sensor intervention) {
        if (monitoringToIntervention.containsKey(monitor)) {
            throw new IllegalArgumentException("Monitoraggio già associato.");
        }

        if (monitoringToIntervention.containsValue(intervention)) {
            throw new IllegalArgumentException("Intervento già associato ad un altro monitoraggio.");
        }
        monitoringToIntervention.put(monitor, intervention);
    }

    // ===== Conteggio sensori di un certo tipo =====
    public long countSensorsByType(String prefix) {
        return sensors.stream()
                .filter(s -> s.getId().startsWith(prefix))
                .count();
    }

    public void addSensorInternal(Sensor sensor) {
        sensors.add(sensor);
    }

    // =========================
    // MOSTRA SENSORI INSTALLATI
    // =========================

    public void showSensors() {
        if (sensors.isEmpty()) {
            System.out.println("Nessun sensore installato.");
            return;
        }

        // ORDINA PER TIPO E POI PER ID
        sensors.sort((s1, s2) -> {
            Sensor base1 = (s1 instanceof SensorDecorator sd1) ? sd1.getBaseSensor() : s1;
            Sensor base2 = (s2 instanceof SensorDecorator sd2) ? sd2.getBaseSensor() : s2;

            boolean isMonitoring1 = base1 instanceof MonitoringSensor;
            boolean isMonitoring2 = base2 instanceof MonitoringSensor;

            if (isMonitoring1 && !isMonitoring2)
                return -1;
            if (!isMonitoring1 && isMonitoring2)
                return 1;

            return base1.getId().compareTo(base2.getId());
        });

        System.out.println("\n--- SENSORI INSTALLATI ---");
        for (Sensor s : sensors) {
            Sensor base = s;
            List<String> modules = new ArrayList<>();

            if (s instanceof SensorDecorator sd) {
                base = sd.getBaseSensor();
                modules = sd.getModules();
            }

            String type = (base instanceof MonitoringSensor) ? "Monitoraggio" : "Intervento";

            StringBuilder sb = new StringBuilder();
            sb.append("- ").append(base.getId())
                    .append(" | Tipo: ").append(type);

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

    public boolean resetSensorById(String id) {
        for (int i = 0; i < sensors.size(); i++) {
            Sensor s = getBaseSensor(sensors.get(i));
            if (s.getId().equals(id)) {
                sensors.get(i).reset(); // chiama reset anche su eventuali decorator
                return true;
            }
        }
        return false;
    }

    public void resetSensors() {
        if (currentState == null) {
            throw new IllegalStateException("Stato del sistema non impostato!");
        }
        currentState.resetSensors(this);
    }

    // =========================
    // AGGIUNTA MODULI A SENSORI
    // =========================

    public boolean addModuleToSensor(String sensorId, String moduleName) {
        int index = -1;
        Sensor selectedSensor = null;

        for (int i = 0; i < sensors.size(); i++) {
            Sensor s = sensors.get(i);
            if (getBaseSensor(s).getId().equals(sensorId)) {
                index = i;
                selectedSensor = s;
                break;
            }
        }

        if (selectedSensor == null)
            return false;

        Sensor decorated = ModuleRegistry.createModule(moduleName, selectedSensor);

        if (decorated != null) {
            sensors.set(index, decorated);
            return true;
        }

        return false;
    }

    // =========================
    // MOSTRA STATISTICHE SENSORI
    // =========================

    public void getAllStatistics() {

        if (sensors.isEmpty()) {
            System.out.println("Nessun sensore installato.");
        }

        StringBuilder sb = new StringBuilder();
        sb.append("\n--- STATISTICHE SENSORI ---\n\n");

        for (Sensor s : sensors) {
            sb.append(s.getStatistics()).append("\n");
        }

        System.out.println(sb.toString());
    }

    // =========================
    // MODALITÀ ATTIVATO
    // =========================

    public void setActiveMode() {
        System.out.println("Modalità Attivato: tutti i sensori di monitoraggio attivi.");
        for (Sensor monitor : monitoringToIntervention.keySet()) {
            Sensor intervention = monitoringToIntervention.get(monitor);

            Sensor baseMonitor = getBaseSensor(monitor);
            Sensor baseIntervention = getBaseSensor(intervention);

            monitor.setModeString("ONLINE");
            intervention.setModeString("ONLINE");

            if (baseMonitor instanceof MonitoringSensor ms && baseIntervention instanceof InterventionSensor is) {
                boolean active = false;
                if (!ms.getReadings().isEmpty()) {
                    double lastReading = ms.getReadings().get(ms.getReadings().size() - 1);
                    active = lastReading > ms.getThreshold();
                }

                ms.setActive(active);
                is.setActive(active);

                if (active) {
                    enqueueAlarm(ms);
                    // imposta lastAlarmTime al valore più recente nello storico
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
    // SIMULAZIONE MONITORAGGIO/INTERVENTI
    // ========================

    public List<String> simulateSensorCycle() {
        List<String> logs = new ArrayList<>();

        if (!currentState.isActivated()) {
            logs.add("Modalità Collaudo. Simulazione non permessa.");
            return logs;
        }

        List<Sensor> shuffled = new ArrayList<>(sensors);
        Collections.shuffle(shuffled);

        for (Sensor s : shuffled) {
            Sensor base = getBaseSensor(s);
            if (base instanceof MonitoringSensor ms) {
                int value = (int) Math.round((Math.random() < 0.5)
                        ? ms.getThreshold() + Math.random() * 5
                        : ms.getThreshold() - Math.random() * 5);

                boolean wasActive = ms.isActive();
                ms.addReading(value); // aggiorna lo stato del sensore
                if (!wasActive && ms.isActive()) {
                    enqueueAlarm(ms);
                    logs.add("⚠ ALLARME ATTIVATO su sensore " + ms.getId());
                } else if (wasActive && !ms.isActive()) {
                    enqueueStopAlarm(ms);
                    logs.add("✔ ALLARME DISATTIVATO su sensore " + ms.getId());
                }
                // logs.add("Sensore " + ms.getId() + " lettura: " + value);
            }
        }
        for (String log : logs) {
            System.out.println(log);
        }

        currentAlarms = new ArrayList<>(alarmQueue);

        processAlarms();
        processStopAlarms();

        FileManager.saveSensors(sensors);
        FileManager.saveStatistics(sensors);
        logs.add("------------------------------------------");
        System.out.println("------------------------------------------");
        return logs;
    }

    public List<Sensor> getAlarmQueue() {
        return new ArrayList<>(currentAlarms);
    }

    public void enqueueAlarm(Sensor monitor) {
        if (!alarmQueue.contains(monitor) && !stopAlarmQueue.contains(monitor)) {
            alarmQueue.add(monitor);
        }
    }

    public void enqueueStopAlarm(Sensor monitor) {
        if (!stopAlarmQueue.contains(monitor)) {
            stopAlarmQueue.add(monitor);
        }
    }

    // Processa gli allarmi attivi in ordine FIFO temporale
    public void processAlarms() {
        System.out.println("---- UNSORTED PA----");
        for (Sensor s : alarmQueue) {
            MonitoringSensor ms = (MonitoringSensor) getBaseSensor(s);
            System.out.println(ms.getId() + " -> " + ms.getLastAlarmTime());
        }

        // Copia della queue
        List<Sensor> sortedAlarms = new ArrayList<>(alarmQueue);
        // Ordinamento per timestamp
        sortedAlarms.sort(Comparator.comparing(s -> ((MonitoringSensor) getBaseSensor(s)).getLastAlarmTime()));

        // STAMPA ORDINATA
        System.out.println("---- SORTED PA----");
        for (Sensor s : sortedAlarms) {
            MonitoringSensor ms = (MonitoringSensor) getBaseSensor(s);
            System.out.println(ms.getId() + " -> " + ms.getLastAlarmTime());
        }

        for (Sensor monitor : sortedAlarms) {
            Sensor base = getBaseSensor(monitor);
            Sensor intervention = monitoringToIntervention.get(base);
            if (intervention instanceof InterventionSensor inter && base instanceof MonitoringSensor ms) {
                // Attiva l’intervento solo se non è già attivo
                if (!inter.isActive()) {
                    inter.trigger(); // aggiorna statistiche e timestamp
                    System.out.println("⚠ ALLARME ATTIVATO su sensore " + ms.getId());
                    System.out.println("Intervento attivato su sensore " + inter.getId() + " alle ore "
                            + inter.getLastActivationTime());
                }
            }
        }
    }

    // Processa gli allarmi cessati in ordine FIFO temporale
    public void processStopAlarms() {
        System.out.println("---- UNSORTED PA----");
        for (Sensor s : stopAlarmQueue) {
            MonitoringSensor ms = (MonitoringSensor) getBaseSensor(s);
            System.out.println(ms.getId() + " -> " + ms.getLastAlarmTime());
        }

        // Ordina la lista in base al tempo di attivazione originale dell'allarme
        List<Sensor> sortedStops = new ArrayList<>(stopAlarmQueue);
        sortedStops.sort(Comparator.comparing(s -> ((MonitoringSensor) s).getLastAlarmTime()));

        // STAMPA ORDINATA
        System.out.println("---- SORTED PA----");
        for (Sensor s : sortedStops) {
            MonitoringSensor ms = (MonitoringSensor) getBaseSensor(s);
            System.out.println(ms.getId() + " -> " + ms.getLastAlarmTime());
        }

        for (Sensor monitor : sortedStops) {
            Sensor base = getBaseSensor(monitor);
            Sensor intervention = monitoringToIntervention.get(base);
            if (intervention instanceof InterventionSensor inter && base instanceof MonitoringSensor ms) {
                // Disattiva l’intervento solo se è attivo
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

    public void stopAlarm(String monitorId) {
        Sensor monitor = sensors.stream().filter(s -> s.getId().equals(monitorId)).findFirst().orElse(null);
        if (monitor == null) {
            System.out.println("Sensore non trovato.");
            return;
        }
        currentState.handleStopAlarm(this, monitor);
    }

    public void clearAlarmQueues() {
        alarmQueue.clear();
        stopAlarmQueue.clear();
        System.out.println("Code di allarme resettate.");
    }

    // =========================
    // METODI HELPER
    // ========================

    public void setSensors(List<Sensor> sensors) {
        this.sensors = sensors;
    }

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
        for (int i = 0; i < Math.min(monitoringSensors.size(), interventionSensors.size()); i++) {
            addMonitoringPair(monitoringSensors.get(i), interventionSensors.get(i));
        }
    }

    public List<String> getSensorInfo() {
        List<String> info = new ArrayList<>();
        for (Sensor s : sensors) {
            Sensor base = getBaseSensor(s);
            String type = (base instanceof MonitoringSensor) ? "Monitoraggio" : "Intervento";
            info.add(base.getId() + "  | Tipo: " + type);
        }
        return info;
    }

    public Sensor getBaseSensor(Sensor sensor) {
        Sensor base = sensor;
        while (base instanceof SensorDecorator decorator) {
            base = decorator.getWrappedSensor();
        }
        return base;
    }

    public String getSensorIdByIndex(int index) {
        if (index < 0 || index >= sensors.size())
            return null;
        return getBaseSensor(sensors.get(index)).getId();
    }

    public List<String> getAvailableModules(String sensorId) {
        Sensor sensor = sensors.stream()
                .filter(s -> getBaseSensor(s).getId().equals(sensorId))
                .findFirst()
                .orElse(null);

        if (sensor == null)
            return new ArrayList<>();

        // supponiamo che HomeSystem tenga internamente un ModuleRegistry
        return ModuleRegistry.getAvailableModules(sensor);
    }

    public List<Sensor> getSensors() {
        return sensors;
    }
}