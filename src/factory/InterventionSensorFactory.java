package factory;

import models.*;

// ==============================
// FACTORY PER SENSORI DI INTERVENTO
// ==============================
//
// Questa factory crea sensori di tipo InterventionSensor
// senza bisogno di soglia.
//
public class InterventionSensorFactory extends SensorFactory {

    // ==============================
    // IMPLEMENTAZIONE FACTORY METHOD
    // ==============================

    /**
     * Crea un sensore di intervento a partire dall'ID
     */
    @Override
    protected Sensor instantiateSensor(String id) {

        // Estrae il prefisso dall'ID per determinare il tipo di sensore
        // es: "AIRCONDITIONER1" -> "AIRCONDITIONER"
        String prefix = id.replaceAll("\\d+$", "");

        // Restituisce un oggetto InterventionSensor corretto in base al tipo
        return switch (prefix) {
            case "AIRCONDITIONER", "POWERCUT", "SIREN", "VENT", "LOCK" -> new InterventionSensor(id);

            // Se il prefisso non è valido, genera eccezione
            default -> throw new IllegalArgumentException("Tipo sensore intervento non valido: " + prefix);
        };
    }
}