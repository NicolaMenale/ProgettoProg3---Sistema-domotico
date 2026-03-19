package main;

// =============================
// CLASSE: TEST MODE STATE
// =============================
//
// Rappresenta lo stato “COLLAUDO” (TEST) del sistema domotico.
// In questa modalità, è possibile installare sensori, resettarli,
// aggiungere moduli e visualizzare statistiche liberamente.
// Tutte le operazioni sono consentite.
//
// Questa classe definisce le regole per la modalità test senza
// blocchi o restrizioni come in ActiveModeState.
//
public class TestModeState extends SystemState {

    // =============================
    // INSTALLAZIONE SENSORI
    // =============================

    // In modalità test, l’installazione dei sensori va avanti normalmente
    @Override
    public void installSensors(HomeSystem system){
        // Nessuna restrizione: HomeSystem gestisce l’installazione
    }

    // =============================
    // VISUALIZZAZIONE SENSORI
    // =============================

    // Mostra i sensori presenti nel sistema
    @Override
    public void showSensors(HomeSystem system){
        system.showSensors();
    }

    // =============================
    // RESET SINGOLO SENSOR
    // =============================

    // In modalità test, il reset di un singolo sensore va avanti normalmente
    @Override
    public void resetSensorByIdS(HomeSystem system){
        // Nessuna restrizione: HomeSystem gestisce il reset
    }

    // =============================
    // RESET TUTTI I SENSORI
    // =============================

    // In modalità test, il reset di tutti i sensori è consentito
    @Override
    public void resetSensorsS(HomeSystem system){
        system.resetSensors(system);
    }

    // =============================
    // INSTALLAZIONE MODULI
    // =============================

    // In modalità test, l’installazione di moduli va avanti normalmente
    @Override
    public void installModules(HomeSystem system){
        // Nessuna restrizione: HomeSystem gestisce l’installazione
    }

    // =============================
    // STATISTICHE SENSORI
    // =============================

    // Mostra le statistiche di tutti i sensori
    @Override
    public void showStatisticsS(HomeSystem system){
        system.showStatistics();
    }
}