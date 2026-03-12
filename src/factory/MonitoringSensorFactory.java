package factory;

import model.MonitoringSensor;
import model.Sensor;

public class MonitoringSensorFactory extends SensorFactory {

    private int threshold;

    // Costruttore che accetta la soglia
    public MonitoringSensorFactory(int threshold) {
        this.threshold = threshold;
    }

    @Override
    protected Sensor instantiateSensor(String id) {
        String prefix = id.replaceAll("\\d+$", "");

        // Recupero del threshold da qualche modo, ad esempio un campo della factory o
        // parametro
        int threshold = this.threshold; // se la factory mantiene un threshold passato

        return switch (prefix) {
            case "TEMPERATURE", "ELECTRICITY", "SMOKE", "GAS", "MOVEMENT" -> new MonitoringSensor(id, threshold);
            default -> throw new IllegalArgumentException("Tipo sensore monitoraggio non valido: " + prefix);
        };
    }
}