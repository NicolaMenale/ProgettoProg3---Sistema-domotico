package model;

// =============================
// CLASSE ASTRATTA: SENSOR
// =============================
//
// Rappresenta un sensore generico del sistema domotico.
// Tutti i sensori (monitoraggio o intervento) estendono questa classe.
// Contiene attributi comuni, gestione dello stato e metodi astratti
// che devono essere implementati dalle sottoclassi.
//
public abstract class Sensor {

    // =============================
    // ATTRIBUTI BASE DEL SENSORE
    // =============================

    protected String id;              // identificativo univoco del sensore
    protected boolean active;         // indica se il sensore è attivo (true) o disattivo (false)
    protected String modeString = "OFFLINE"; // stato testuale del sensore (es: OFFLINE, ONLINE)

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

    // Restituisce la modalità testuale attuale del sensore
    public String getModeString() {
        return modeString;
    }

    // Restituisce l'identificativo univoco del sensore
    public String getId() {
        return id;
    }

    // Restituisce lo stato di attivazione del sensore
    // true = attivo, false = disattivo
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

    // Riporta il sensore allo stato iniziale.
    // Deve essere implementato da tutte le sottoclassi (es. MonitoringSensor, InterventionSensor)
    public abstract void reset();

    // Restituisce le statistiche del sensore.
    // Ogni tipo di sensore può fornire informazioni specifiche
    // come numero di letture, soglia superata, interventi eseguiti ecc.
    public abstract String getStatistics();
}