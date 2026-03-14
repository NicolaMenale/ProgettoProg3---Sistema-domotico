package model;

import java.time.LocalDateTime;
import java.util.*;

public class MonitoringSensor extends Sensor {

    // =============================
    // ATTRIBUTI SPECIFICI DEL SENSORE DI MONITORAGGIO
    // =============================

    private int threshold; // soglia oltre la quale scatta l'allarme
    private List<Double> readings; // lista delle letture rilevate dal sensore
    private List<LocalDateTime> alarmHistory; // storico delle date in cui è scattato un allarme
    private LocalDateTime lastAlarmTime; // ultima volta in cui è scattato un allarme
    private int alarmCount = 0; // numero totale di allarmi generati

    // =============================
    // COSTRUTTORE
    // =============================

    // Crea un sensore di monitoraggio con ID e soglia.
    // Inizializza le liste per letture e storico allarmi.
    public MonitoringSensor(String id, int threshold) {
        super(id);
        this.threshold = threshold;
        this.active = false;
        this.readings = new ArrayList<>();
        this.alarmHistory = new ArrayList<>();
        this.lastAlarmTime = null;
    }

    // =============================
    // GETTER
    // =============================

    // Restituisce la soglia del sensore
    public int getThreshold() {
        return threshold;
    }

    // Restituisce tutte le letture registrate
    public List<Double> getReadings() {
        return readings;
    }

    // Restituisce lo storico degli allarmi
    public List<LocalDateTime> getAlarmHistory() {
        return alarmHistory;
    }

    // Restituisce l'orario dell'ultimo allarme
    public LocalDateTime getLastAlarmTime() {
        return lastAlarmTime;
    }

    // Restituisce il numero totale di allarmi
    public int getAlarmCount() {
        return alarmCount;
    }

    // Metodo helper che indica che questo è un sensore di monitoraggio
    public boolean isMonitoring() {
        return true;
    }

    // =============================
    // CONTROLLO STATO SENSORE
    // =============================

    // Attiva il sensore
    @Override
    public void activate() {
        active = true;
    }

    // Disattiva il sensore
    @Override
    public void deactivate() {
        active = false;
    }

    // =============================
    // LOGICA DI MONITORAGGIO
    // =============================

    // Aggiunge una nuova lettura al sensore e verifica se scatta o termina un allarme
    public boolean addReading(double value) {

        // salva la lettura
        readings.add(value);

        // Se il valore supera la soglia e il sensore non era già in allarme
        if (value > threshold && !active) {

            active = true; // attiva lo stato di allarme
            alarmCount++; // incrementa contatore allarmi

            // registra data e ora dell'allarme
            lastAlarmTime = LocalDateTime.now();
            alarmHistory.add(lastAlarmTime);

            System.out.println("ALLARME su sensore " + getId() + " alle ore " + lastAlarmTime);

            return true; // nuovo allarme generato
        }

        // Se il valore torna sotto la soglia e il sensore era in allarme
        if (value <= threshold && active) {

            active = false; // termina stato di allarme
            return false; // fine allarme
        }

        // Nessun cambiamento di stato
        return active;
    }

    // =============================
    // RESET DEL SENSORE
    // =============================

    // Riporta il sensore allo stato iniziale
    @Override
    public void reset() {

        readings.clear(); // cancella tutte le letture
        alarmHistory.clear(); // cancella storico allarmi
        lastAlarmTime = null; // rimuove ultimo allarme
        alarmCount = 0; // reset contatore allarmi
        active = false; // disattiva sensore
    }

    // =============================
    // STATISTICHE DEL SENSORE
    // =============================

    // Restituisce una stringa con tutte le informazioni e statistiche del sensore
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

    // =============================
    // SETTER
    // =============================

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
}