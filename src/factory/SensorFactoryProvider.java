package factory;

// ==============================
// PROVIDER FACTORY SENSORI
// ==============================
//
// Restituisce la factory corretta in base all'ID o al prefisso del sensore.
// Permette di creare sensori di monitoraggio o intervento senza conoscere
// il tipo concreto nella logica principale.
//
public class SensorFactoryProvider {

    // ==============================
    // OTTIENI FACTORY CON THRESHOLD SPECIFICO
    // ==============================
    public static SensorFactory getFactory(String prefix, int threshold) {

        // Restituisce la factory corrispondente e permette di passare la soglia
        return switch (prefix) {
            case "TEMPERATURE", "ELECTRICITY", "SMOKE", "GAS", "MOVEMENT" -> new MonitoringSensorFactory(threshold);
            default -> throw new IllegalArgumentException("Nessuna factory per il sensore: " + prefix);
        };
    }

    // ==============================
    // OTTIENI FACTORY DA ID SENSOR
    // ==============================
    public static SensorFactory getFactory(String id) {

        // Estrae il prefisso (es. TEMPERATURE da TEMPERATURE1)
        String prefix = id.replaceAll("\\d+$", "");

        // Restituisce la factory corrispondente in base al prefisso
        return switch (prefix) {
            case "AIRCONDITIONER", "POWERCUT", "SIREN", "VENT", "LOCK" -> new InterventionSensorFactory();
            default -> throw new IllegalArgumentException("Nessuna factory per il sensore: " + prefix);
        };
    }
}