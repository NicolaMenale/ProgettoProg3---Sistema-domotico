package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MonitoringSensor extends Sensor {

    private double threshold; // aggiunto
    private List<Double> readings;
    private List<LocalDateTime> alarmHistory;
    private LocalDateTime lastAlarmTime;
    private int alarmCount = 0;

    public MonitoringSensor(String id, double threshold) {
        super(id);
        this.threshold = threshold;
        this.active = false;
        this.readings = new ArrayList<>();
        this.alarmHistory = new ArrayList<>();
        this.lastAlarmTime = null;
    }

    // =========================
    // GETTER
    // =========================

    public double getThreshold() {
        return threshold;
    }

    public List<Double> getReadings() {
        return readings;
    }

    public List<LocalDateTime> getAlarmHistory() {
        return alarmHistory;
    }

    public LocalDateTime getLastAlarmTime() {
        return lastAlarmTime;
    }

    public int getAlarmCount() {
        return alarmCount;
    }

    public boolean isMonitoring() {
        return true;
    }

    // =========================
    // SETTER
    // =========================

    @Override
    public void activate() {
        active = true;
    }

    @Override
    public void deactivate() {
        active = false;
    }

    public boolean addReading(double value) {
        readings.add(value);

        if (value > threshold && !active) {
            active = true;
            alarmCount++;
            lastAlarmTime = LocalDateTime.now();
            alarmHistory.add(lastAlarmTime);
            System.out.println("ALLARME su sensore " + getId() + " alle ore " + lastAlarmTime);
            return true; // nuovo allarme
        }

        if (value <= threshold && active) {
            active = false;
            return false; // fine allarme
        }

        return active;
    }

    @Override
    public void reset() {
        readings.clear();
        alarmHistory.clear();
        lastAlarmTime = null;
        alarmCount = 0;
        active = false;
    }

    @Override
    public void printStatistics() {
        System.out.println("- Sensore ID: " + id);
        System.out.println("  Tipo: Monitoraggio");
        System.out.println("  Stato: " + modeString);
        System.out.println("  Stato attivo: " + (active ? "SI" : "NO"));
        System.out.println("  Threshold: " + threshold);
        System.out.println("  Letture: " + readings);
        System.out.println("  Numero allarmi: " + alarmCount);
        System.out.println("  Storico allarmi: " + alarmHistory);
    }

    // Imposta se il sensore è attivo (allarme in corso)
    public void setActive(boolean active) {
        this.active = active;
    }

    // Imposta il valore della soglia
    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    // Imposta il numero di allarmi già scattati
    public void setAlarmCount(int alarmCount) {
        this.alarmCount = alarmCount;
    }

    // Imposta lo storico degli allarmi (lista di LocalDateTime)
    public void setAlarmHistory(List<LocalDateTime> alarmHistory) {
        this.alarmHistory = new ArrayList<>(alarmHistory);
    }

    // Imposta l'ultima data di allarme
    public void setLastAlarmTime(LocalDateTime lastAlarmTime) {
        this.lastAlarmTime = lastAlarmTime;
    }
}