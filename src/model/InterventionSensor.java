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
    public void printStatistics() {
        System.out.println("- Sensore ID: " + id);
        System.out.println("  Tipo: Intervento");
        System.out.println("  Stato: " + modeString);
        System.out.println("  Stato attivo: " + (active ? "SI" : "NO"));
        System.out.println("  Numero attivazioni: " + activationCount);
        System.out.println("  Storico attivazioni: " + activationHistory);
    }
}