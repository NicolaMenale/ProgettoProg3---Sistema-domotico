package main;

import model.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import decorator.SensorDecorator;
import factory.SensorFactory;
import factory.SensorFactoryProvider;

public class HomeSystem {
    // relazione 1:1 monitoraggio → intervento
    private Map<Sensor, Sensor> monitoringToIntervention = new HashMap<>();
    private List<Sensor> sensors = new ArrayList<>();
    private SystemState currentState;
    // code FIFO
    private Queue<Sensor> alarmQueue = new PriorityQueue<>(
            Comparator.comparing(
                    s -> ((MonitoringSensor) s).getLastAlarmTime(),
                    Comparator.nullsLast(Comparator.naturalOrder())));

    private Queue<Sensor> stopAlarmQueue = new PriorityQueue<>(
            Comparator.comparing(
                    s -> ((MonitoringSensor) s).getLastAlarmTime(),
                    Comparator.nullsLast(Comparator.naturalOrder())));

    // ===== Costruttore =====
    public HomeSystem() {
        sensors = new ArrayList<>();
    }

    // ===== Cambio stato =====
    public void setState(SystemState state) {
        this.currentState = state;
    }

    /*
     * public SystemState getState() {
     * return currentState;
     * }
     */

    // ===== Metodo pubblico: delega allo stato =====
    public void installMonitoringPair(String monitorType, String interventionType, double threshold) {
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

    public void addMonitoringPair(Sensor monitor, Sensor intervention) {
        if (monitoringToIntervention.containsKey(monitor)) {
            throw new IllegalArgumentException("Monitoraggio già associato.");
        }

        if (monitoringToIntervention.containsValue(intervention)) {
            throw new IllegalArgumentException("Intervento già associato ad un altro monitoraggio.");
        }
        monitoringToIntervention.put(monitor, intervention);
    }

    public void installSensor(Sensor sensor) {
        if (currentState == null) {
            throw new IllegalStateException("Stato del sistema non impostato!");
        }
        currentState.installSensor(this, sensor);
    }

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

    // ===== Metodi interni usati dagli stati =====
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

    // Restituisce una lista di stringhe con info di ogni sensore già “base”
    // Esempio: "TEMPERATURE1 | Tipo: Monitoraggio"
    public List<String> getSensorInfo() {
        List<String> info = new ArrayList<>();
        for (Sensor s : sensors) {
            Sensor base = getBaseSensor(s);
            String type = (base instanceof MonitoringSensor) ? "Monitoraggio" : "Intervento";
            info.add(base.getId() + " | Tipo: " + type);
        }
        return info;
    }

    public String getSensorIdByIndex(int index) {
        if (index < 0 || index >= sensors.size())
            return null;
        return getBaseSensor(sensors.get(index)).getId();
    }

    public Sensor getBaseSensor(Sensor sensor) {
        Sensor base = sensor;
        while (base instanceof SensorDecorator decorator) {
            base = decorator.getWrappedSensor();
        }
        return base;
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

    public void addSensorInternal(Sensor sensor) {
        sensors.add(sensor);
    }

    public List<Sensor> getSensors() {
        return sensors;
    }

    // ===== Conteggio sensori di un certo tipo =====
    public long countSensorsByType(String prefix) {
        return sensors.stream()
                .filter(s -> s.getId().startsWith(prefix))
                .count();
    }

    public void showStatistics() {

        if (sensors.isEmpty()) {
            System.out.println("Nessun sensore installato.");
            return;
        }

        System.out.println("\n--- STATISTICHE SENSORI ---");

        for (Sensor s : sensors) {
            s.printStatistics();
            System.out.println();
        }
    }

    // ---------------------------------------------------------------------------------------------//
    // Attiva tutti i sensori

    public void simulateSensorCycle() {
        if (sensors.isEmpty()) {
            System.out.println("Nessun sensore installato.");
            return;
        }

        System.out.println("\nSimulazione letture sensori in corso...");
        // 1️⃣ Mescola l'ordine dei sensori per simulazione realistica
        List<Sensor> shuffled = new ArrayList<>(sensors);
        Collections.shuffle(shuffled);

        for (Sensor s : shuffled) {
            Sensor base = getBaseSensor(s);

            if (base instanceof MonitoringSensor ms) {
                // genera lettura casuale
                double value = (Math.random() < 0.5) ? ms.getThreshold() + Math.random() * 5
                        : ms.getThreshold() - Math.random() * 5;

                boolean alarmTriggered = ms.addReading(value);

                if (alarmTriggered) {
                    enqueueAlarm(ms);
                } else if (!alarmTriggered && !ms.isActive()) {
                    enqueueStopAlarm(ms);
                }
            }
        }

        processAlarms();
        processStopAlarms();
    }

    public void notifyAlarm(Sensor monitor) {
        if (currentState == null) {
            throw new IllegalStateException("Stato del sistema non impostato!");
        }
        currentState.handleAlarm(this, monitor);
    }

    public void notifyStopAlarm(Sensor monitor) {
        if (currentState == null) {
            throw new IllegalStateException("Stato del sistema non impostato!");
        }
        currentState.handleStopAlarm(this, monitor);
    }

    public void enqueueAlarm(Sensor monitor) {
        if (!alarmQueue.contains(monitor)) {
            alarmQueue.add(monitor);
        }
    }

    public void enqueueStopAlarm(Sensor monitor) {
        if (!stopAlarmQueue.contains(monitor)) {
            stopAlarmQueue.add(monitor);
        }
    }

    public void processAlarms() {
        while (!alarmQueue.isEmpty()) {
            Sensor monitor = alarmQueue.poll();
            Sensor base = getBaseSensor(monitor); // IMPORTANTE per decorator
            Sensor intervention = monitoringToIntervention.get(base);

            if (intervention instanceof InterventionSensor inter) {
                inter.trigger(); // Aggiorna statistiche e stampa
                System.out.println("Attivato intervento per: " + base.getId() +
                        " alle " + java.time.LocalDateTime.now());
            }
        }
    }

    public void processStopAlarms() {
        while (!stopAlarmQueue.isEmpty()) {
            Sensor monitor = stopAlarmQueue.poll();
            Sensor base = getBaseSensor(monitor); // IMPORTANTE per decorator
            Sensor intervention = monitoringToIntervention.get(base);

            if (intervention instanceof InterventionSensor inter) {
                if (inter.isActive()) {
                    inter.deactivate();
                    System.out.println(
                            "Disattivato intervento per: " + base.getId() + " alle " + java.time.LocalDateTime.now());
                }
            }
        }
    }

    public void triggerAlarm(String monitorId) {
        Sensor monitor = sensors.stream()
                .filter(s -> s.getId().equals(monitorId))
                .findFirst()
                .orElse(null);

        if (monitor == null) {
            System.out.println("Sensore non trovato.");
            return;
        }

        currentState.handleAlarm(this, monitor);
    }

    public void stopAlarm(String monitorId) {
        Sensor monitor = sensors.stream()
                .filter(s -> s.getId().equals(monitorId))
                .findFirst()
                .orElse(null);

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

    public void setCollaudoMode() {
        System.out.println("Modalità Collaudo: tutti i sensori disattivati.");
        for (Sensor s : sensors) {
            s.setModeString("OFFLINE");
            s.deactivate();
        }
        alarmQueue.clear();
        stopAlarmQueue.clear();
    }

    public void setActiveMode() {
        System.out.println("Modalità Attivato: tutti i sensori di monitoraggio attivi.");

        for (Sensor s : sensors) {
            s.setModeString("ONLINE");
        }

        // Pulizia delle code per evitare messaggi "fantasma"
        alarmQueue.clear();
        stopAlarmQueue.clear();
    }

}