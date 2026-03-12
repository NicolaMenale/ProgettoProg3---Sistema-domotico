package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MonitoringSensor extends Sensor {

    private int threshold; // aggiunto
    private List<Double> readings;
    private List<LocalDateTime> alarmHistory;
    private LocalDateTime lastAlarmTime;
    private int alarmCount = 0;

    public MonitoringSensor(String id, int threshold) {
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

    public int getThreshold() {
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
    public String getStatistics() {

        StringBuilder sb = new StringBuilder();

        sb.append("- Sensore ID: ").append(id).append("\n");
        sb.append("  Tipo: Monitoraggio\n");
        sb.append("  Stato: ").append(modeString).append("\n");
        sb.append("  Stato attivo: ").append(active ? "SI" : "NO").append("\n");
        sb.append("  Threshold: ").append(threshold).append("\n");
        sb.append("  Letture: ").append(readings).append("\n");
        sb.append("  Numero allarmi: ").append(alarmCount).append("\n");
        sb.append("  Storico allarmi: ").append(alarmHistory).append("\n");

        return sb.toString();
    }

    // Imposta se il sensore è attivo (allarme in corso)
    public void setActive(boolean active) {
        this.active = active;
    }

    // Imposta il valore della soglia
    public void setThreshold(int threshold) {
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