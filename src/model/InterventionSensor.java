package model;

import java.time.LocalDateTime;
import java.util.*;

public class InterventionSensor extends Sensor {

    // =============================
    // ATTRIBUTI SPECIFICI DEL SENSORE DI INTERVENTO
    // =============================

    private int activationCount = 0; // numero totale di attivazioni del sensore
    private LocalDateTime lastActivationTime; // data e ora dell'ultima attivazione
    private List<LocalDateTime> activationHistory; // storico di tutte le attivazioni

    // =============================
    // COSTRUTTORE
    // =============================

    // Crea un sensore di intervento con ID.
    // Inizializza il contatore e lo storico delle attivazioni.
    public InterventionSensor(String id) {
        super(id);
        this.activationCount = 0;
        this.activationHistory = new ArrayList<>();
        this.active = false;
    }

    // =============================
    // GETTER
    // =============================

    // Restituisce il numero totale di attivazioni
    public int getActivationCount() {
        return activationCount;
    }

    // Restituisce la data e ora dell'ultima attivazione
    public LocalDateTime getLastActivationTime() {
        return lastActivationTime;
    }

    // Restituisce lo storico completo delle attivazioni
    public List<LocalDateTime> getActivationHistory() {
        return activationHistory;
    }

    // =============================
    // CONTROLLO STATO DEL SENSORE
    // =============================

    // Attiva il sensore
    @Override
    public void activate() {
        active = true;
    }

    // Disattiva il sensore solo se era attivo
    @Override
    public void deactivate() {
        if (active) {
            active = false;
        }
    }

    // =============================
    // LOGICA DI INTERVENTO
    // =============================

    // Attiva il sensore di intervento.
    // Registra l'attivazione solo se il sensore non è già attivo.
    public void trigger() {

        if (!active) {

            active = true; // attiva il sensore
            activationCount++; // incrementa il numero di attivazioni

            // registra data e ora dell'attivazione
            lastActivationTime = LocalDateTime.now();
            activationHistory.add(lastActivationTime);

            System.out.println("Intervento attivato su sensore " + id + " alle ore " + lastActivationTime);
        }
    }

    // =============================
    // RESET DEL SENSORE
    // =============================

    // Riporta il sensore allo stato iniziale
    @Override
    public void reset() {
        activationCount = 0; // reset contatore attivazioni
        lastActivationTime = null; // rimuove ultima attivazione
        activationHistory.clear(); // cancella storico attivazioni
        deactivate(); // disattiva il sensore
    }

    // =============================
    // STATISTICHE DEL SENSORE
    // =============================

    // Restituisce una stringa con tutte le informazioni e statistiche del sensore
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

    // =============================
    // SETTER
    // =============================

    // Imposta manualmente lo stato del sensore
    public void setActive(boolean active) {
        this.active = active;
    }

    // Imposta manualmente il numero di attivazioni
    public void setNumberOfActivations(int activationCount) {
        this.activationCount = activationCount;
    }
}