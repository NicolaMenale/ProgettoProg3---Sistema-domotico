package main;

// ==============================
// CLASSE: ACTIVE MODE STATE
// ==============================
//
// Rappresenta lo stato “ATTIVATO” del sistema domotico.
// In questa modalità il sistema è pienamente operativo.
// I sensori sono attivi e alcune operazioni di configurazione
// (installazione / reset) possono essere bloccate.
// 
public class ActiveModeState extends SystemState {

    // ==============================
    // OPERAZIONI SUI SENSORI
    // ==============================

    // Mostra tutti i sensori installati tramite HomeSystem
    @Override
    public void showSensors(HomeSystem system) {
        system.showSensors();
    }

    // Mostra le statistiche di tutti i sensori tramite HomeSystem
    @Override
    public void showStatisticsS(HomeSystem system) {
        system.showStatistics();
    }

    // Simula un ciclo dei sensori (letture, allarmi, interventi)
    @Override
    public void simulateSensorCycleS(HomeSystem system) {
        system.simulateSensorCycle();
    }
}