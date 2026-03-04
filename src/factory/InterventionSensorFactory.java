package factory;
import model.*;

public class InterventionSensorFactory extends SensorFactory {

    @Override
    protected Sensor instantiateSensor(String id) {
        String prefix = id.replaceAll("\\d+$", ""); // WATER1 -> WATER
        return switch (prefix) {
            case "AIRCONDITIONER", "POWERCUT", "SIREN", "VENT", "LOCK" -> new InterventionSensor(id);
            default -> throw new IllegalArgumentException("Tipo sensore intervento non valido: " + prefix);
        };
    }
}