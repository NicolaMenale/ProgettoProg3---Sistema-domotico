package main;

import model.Sensor;

public class ActiveModeState extends SystemState {
    @Override
    public boolean isActivated() {
        return true;
    }

    @Override
    public void installSensor(HomeSystem system, Sensor sensor) {
        // In modalità attivato non si possono installare nuovi sensori
        System.out.println("Impossibile installare sensori in modalità ATTIVATO");
    }

    @Override
    public void resetSensors(HomeSystem system) {
        // In modalità attivato non si possono resettare sensori
        System.out.println("Impossibile resettare sensori in modalità ATTIVATO");
    }

    @Override
    public void handleAlarm(HomeSystem system, Sensor monitor) {
        system.enqueueAlarm(monitor);
        system.processAlarms();
    }

    @Override
    public void handleStopAlarm(HomeSystem system, Sensor monitor) {
        system.enqueueStopAlarm(monitor);
        system.processStopAlarms();
    }
}