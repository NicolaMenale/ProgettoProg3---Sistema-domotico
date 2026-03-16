package factory;

import model.*;

// ==============================
// FACTORY PER SENSORI DI MONITORAGGIO
// ==============================
//
// Questa factory crea sensori di tipo MonitoringSensor
// impostando una soglia (threshold) specifica.
//
public class MonitoringSensorFactory extends SensorFactory {

    // ==============================
    // ATTRIBUTI
    // ==============================
    // Threshold predefinito da assegnare ai sensori creati
    private int threshold;

    // ==============================
    // COSTRUTTORE
    // ==============================
    // Accetta la soglia da assegnare ai sensori di monitoraggio
    public MonitoringSensorFactory(int threshold) {
        this.threshold = threshold;
    }

    // ==============================
    // IMPLEMENTAZIONE FACTORY METHOD
    // ==============================
    // Crea un sensore di monitoraggio a partire dall'ID
    @Override
    protected Sensor instantiateSensor(String id) {

        // Estrae il prefisso dall'ID per determinare il tipo di sensore
        String prefix = id.replaceAll("\\d+$", "");

        // Usa il threshold memorizzato nella factory
        int threshold = this.threshold;

        // Restituisce un oggetto MonitoringSensor corretto in base al tipo
        return switch (prefix) {
            case "TEMPERATURE", "ELECTRICITY", "SMOKE", "GAS", "MOVEMENT" ->
                new MonitoringSensor(id, threshold);

            // Se il prefisso non è valido, genera eccezione
            default ->
                throw new IllegalArgumentException("Tipo sensore monitoraggio non valido: " + prefix);
        };
    }
}