package factory;

public class SensorFactoryProvider {

    public static SensorFactory getFactory(String id) {
        String prefix = id.replaceAll("\\d+$", "");

        return switch (prefix) {
            case "TEMPERATURE", "ELECTRICITY", "SMOKE", "GAS", "MOVEMENT" -> new MonitoringSensorFactory(0); // default 0 se non noto
            case "AIRCONDITIONER", "POWERCUT", "SIREN", "VENT", "LOCK" -> new InterventionSensorFactory();
            default -> throw new IllegalArgumentException("Nessuna factory per il sensore: " + prefix);
        };
    }

    // Nuovo metodo per passare il threshold manuale
    public static SensorFactory getFactory(String prefix, int threshold) {
        return switch (prefix) {
            case "TEMPERATURE", "ELECTRICITY", "SMOKE", "GAS", "MOVEMENT" -> new MonitoringSensorFactory(threshold);
            case "AIRCONDITIONER", "POWERCUT", "SIREN", "VENT", "LOCK" -> new InterventionSensorFactory();
            default -> throw new IllegalArgumentException("Nessuna factory per il sensore: " + prefix);
        };
    }
}