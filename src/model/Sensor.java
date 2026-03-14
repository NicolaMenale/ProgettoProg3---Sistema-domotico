package model;

public abstract class Sensor {

    // =============================
    // ATTRIBUTI BASE DEL SENSORE
    // =============================

    protected String id;          // identificativo univoco del sensore
    protected boolean active;     // indica se il sensore è attivo o disattivo
    protected String modeString = "OFFLINE"; // stato testuale del sensore (default: modalità collaudo)

    // =============================
    // COSTRUTTORE
    // =============================

    // Inizializza il sensore con un ID.
    // Alla creazione il sensore parte disattivo.
    public Sensor(String id) {
        this.id = id;
        this.active = false;
    }

    // =============================
    // SETTER
    // =============================

    // Imposta la modalità del sensore come stringa
    // (es: OFFLINE, ONLINE)
    public void setModeString(String mode) {
        this.modeString = mode;
    }

    // =============================
    // GETTER
    // =============================

    // Restituisce la modalità attuale del sensore
    public String getModeString() {
        return modeString;
    }

    // Restituisce l'identificativo del sensore
    public String getId() {
        return id;
    }

    // Restituisce lo stato di attivazione del sensore
    // true = attivo
    // false = disattivo
    public boolean isActive() {
        return active;
    }

    // =============================
    // CONTROLLO STATO DEL SENSORE
    // =============================

    // Attiva il sensore
    public void activate() {
        active = true;
    }

    // Disattiva il sensore
    public void deactivate() {
        active = false;
    }

    // =============================
    // METODI ASTRATTI
    // =============================

    // Reset del sensore.
    // Deve essere implementato dalle sottoclassi (es. sensori di monitoraggio o intervento)
    // e serve a riportare il sensore allo stato iniziale.
    public abstract void reset();

    // Restituisce le statistiche del sensore
    // (es. numero di allarmi, interventi eseguiti, ecc.)
    // Ogni tipo di sensore implementa le proprie statistiche.
    public abstract String getStatistics();
}