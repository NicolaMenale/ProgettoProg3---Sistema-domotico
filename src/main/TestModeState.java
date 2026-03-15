package main;

import model.*;

public class TestModeState extends SystemState {
    @Override
    public boolean isActivated() {
        return false;
    }

    @Override
    public void installSensor(HomeSystem system, Sensor sensor) {
        system.addSensorInternal(sensor);
    }

    @Override
    public void resetSensors(HomeSystem system) {
        system.getSensors().forEach(Sensor::reset);
        System.out.println("Tutti i sensori sono stati resettati (Collaudo)");
    }

    
}