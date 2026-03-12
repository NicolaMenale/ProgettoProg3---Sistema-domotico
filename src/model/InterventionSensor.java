package model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InterventionSensor extends Sensor {

    private int activationCount = 0;
    private LocalDateTime lastActivationTime;
    private List<LocalDateTime> activationHistory;

    public InterventionSensor(String id) {
        super(id);
        this.activationCount = 0;
        this.activationHistory = new ArrayList<>();
        this.active = false;
    }

    // =========================
    // GETTER
    // =========================

    public int getActivationCount() {
        return activationCount;
    }

    public LocalDateTime getLastActivationTime() {
        return lastActivationTime;
    }

    public List<LocalDateTime> getActivationHistory() {
        return activationHistory;
    }

    // =========================
    // LOGICA INTERVENTO
    // =========================
    @Override
    public void activate() {
        active = true;
    }

    @Override
    public void deactivate() {
        if (active) {
            active = false;
        }
    }

    public void trigger() {
        if (!active) {
            active = true;
            activationCount++;
            lastActivationTime = LocalDateTime.now();
            activationHistory.add(lastActivationTime);
            System.out.println("Intervento attivato su sensore " + id + " alle ore " + lastActivationTime);
        }
    }

    @Override
    public void reset() {
        activationCount = 0;
        lastActivationTime = null;
        activationHistory.clear();
        deactivate();
    }

    @Override
    public String getStatistics() {

        StringBuilder sb = new StringBuilder();

        sb.append("- Sensore ID: ").append(id).append("\n");
        sb.append("  Tipo: Intervento\n");
        sb.append("  Stato: ").append(modeString).append("\n");
        sb.append("  Stato attivo: ").append(active ? "SI" : "NO").append("\n");
        sb.append("  Numero attivazioni: ").append(activationCount).append("\n");
        sb.append("  Storico attivazioni: ").append(activationHistory).append("\n");

        return sb.toString();
    }

    // Imposta se il sensore di intervento è attivo
    public void setActive(boolean active) {
        this.active = active;
    }

    // Imposta il numero di attivazioni
    public void setNumberOfActivations(int activationCount) {
        this.activationCount = activationCount;
    }

    // Imposta lo storico delle attivazioni
    public void setActivationHistory(List<LocalDateTime> activationHistory) {
        this.activationHistory = new ArrayList<>(activationHistory);
    }
}