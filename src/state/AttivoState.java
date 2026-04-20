package state;

import java.util.List;

import main.HomeSystem;

// ==============================
// CLASSE: ACTIVE MODE STATE
// ==============================

/**
 * Rappresenta lo stato “ATTIVATO” del sistema domotico.
 * In questa modalità il sistema è pienamente operativo.
 * I sensori sono attivi e alcune operazioni di configurazione
 * (installazione / reset) possono essere bloccate.
 */
public class AttivoState extends SystemState {

    // ==============================
    // OPERAZIONI SUI SENSORI
    // ==============================

    /** Mostra tutti i sensori installati. */
    @Override
    public void showSensors(HomeSystem system) {
        system.showSensors();
    }

    /** Mostra le statistiche dei sensori. */
    @Override
    public void showStatisticsS(HomeSystem system) {
        system.showStatistics();
    }

    /**
     * Simula un ciclo di funzionamento dei sensori (letture, allarmi e interventi).
     */
    @Override
    public List<String> simulateSensorCycleS(HomeSystem system) {
        return system.simulateSensorCycle();
    }
}