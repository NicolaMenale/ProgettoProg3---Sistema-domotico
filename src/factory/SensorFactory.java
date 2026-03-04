package factory;

import model.Sensor;

public abstract class SensorFactory {

    // Factory Method
    public Sensor createSensor(String id) {
        return instantiateSensor(id);
    }

    // Metodo astratto che le sottoclassi implementeranno
    protected abstract Sensor instantiateSensor(String id);
}