package main;

import model.Sensor;

public abstract class SystemState {

    // Installa un sensore nel sistema
    public abstract void installSensor(HomeSystem system, Sensor sensor);

    // Resetta tutti i sensori
    public abstract void resetSensors(HomeSystem system);

    public abstract void handleAlarm(HomeSystem system, Sensor monitor);

    public abstract void handleStopAlarm(HomeSystem system, Sensor monitor);
}