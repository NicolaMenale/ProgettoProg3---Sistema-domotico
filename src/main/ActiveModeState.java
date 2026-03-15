package main;

import model.Sensor;

public class ActiveModeState extends SystemState {
    // =============================
    // GETTER / STATO DEL SISTEMA
    // =============================

    // Restituisce true perché questo stato rappresenta la modalità attivata
    @Override
    public boolean isActivated() {
        return true;
    }

    // =============================
    // INSTALLAZIONE / RESET SENSORI
    // =============================

    // In modalità ATTIVATO non si possono installare nuovi sensori
    @Override
    public void installSensor(HomeSystem system, Sensor sensor) {
        System.out.println("Impossibile installare sensori in modalità ATTIVATO");
    }

    // In modalità ATTIVATO non si possono resettare i sensori
    @Override
    public void resetSensors(HomeSystem system) {
        System.out.println("Impossibile resettare sensori in modalità ATTIVATO");
    }
}