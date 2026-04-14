package models;

import java.time.LocalDateTime;
import java.util.*;
// =============================
// CLASSE: INTERVENTION SENSOR
// =============================
/**
 * Rappresenta un sensore di intervento.
 * Memorizza attivazioni, storico e statistiche.
 * Estende la classe astratta Sensor.
 */
public class InterventionSensor extends Sensor {

    // =============================
    // ATTRIBUTI SPECIFICI DEL SENSORE DI INTERVENTO
    // =============================

    /** numero totale di attivazioni del sensore */
    private int activationCount = 0;

    /** data e ora dell'ultima attivazione */
    private LocalDateTime lastActivationTime;

    /** storico di tutte le attivazioni */
    private List<LocalDateTime> activationHistory;

    // =============================
    // COSTRUTTORE
    // =============================

    /**
     * Crea un sensore di intervento con ID
     * Inizializza contatore e storico attivazioni
     */
    public InterventionSensor(String id) {
        super(id);                             // chiama il costruttore della superclasse
        this.activationCount = 0;              // inizializza contatore
        this.activationHistory = new ArrayList<>(); // inizializza lista storico
        this.active = false;                    // sensore inizialmente disattivo
    }

    // =============================
    // GETTER
    // =============================

    /**
     * Restituisce il numero totale di attivazioni
     */
    public int getActivationCount() {
        return activationCount;
    }

    /**
     * Restituisce la data e ora dell'ultima attivazione
     */
    public LocalDateTime getLastActivationTime() {
        return lastActivationTime;
    }

    /**
     * Restituisce lo storico completo delle attivazioni
     */
    public List<LocalDateTime> getActivationHistory() {
        return activationHistory;
    }

    // =============================
    // CONTROLLO STATO DEL SENSORE
    // =============================

    /**
     * Attiva il sensore
     */
    @Override
    public void activate() {
        active = true;
    }

    /**
     * Disattiva il sensore solo se era attivo
     */
    @Override
    public void deactivate() {
        if (active) {
            active = false;
        }
    }

    // =============================
    // LOGICA DI INTERVENTO
    // =============================

    /**
     * Attiva il sensore di intervento
     * Registra l'attivazione solo se il sensore non era già attivo
     */
    public void trigger() {
        if (!active) {
            active = true;                     // attiva il sensore
            activationCount++;                  // incrementa contatore attivazioni

            lastActivationTime = LocalDateTime.now(); // registra data/ora
            activationHistory.add(lastActivationTime); // aggiunge all storico

            System.out.println("Intervento attivato su sensore " + id + " alle ore " + lastActivationTime);
        }
    }

    // =============================
    // RESET DEL SENSORE
    // =============================

    /**
     * Riporta il sensore allo stato iniziale
     */
    @Override
    public void reset() {
        activationCount = 0;                    // reset contatore attivazioni
        lastActivationTime = null;              // rimuove ultima attivazione
        activationHistory.clear();              // cancella storico
        deactivate();                            // disattiva il sensore
    }

    // =============================
    // STATISTICHE DEL SENSORE
    // =============================

    /**
     * Restituisce una stringa con informazioni e statistiche del sensore
     */
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

    /**
     * Imposta manualmente lo stato del sensore
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Imposta manualmente il numero di attivazioni
     */
    public void setNumberOfActivations(int activationCount) {
        this.activationCount = activationCount;
    }
}