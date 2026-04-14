package state;

import main.HomeSystem;

// =============================
// CLASSE ASTRATTA: SYSTEM STATE
// =============================

/** Rappresenta lo "stato globale" del sistema domotico.
* Ogni sottoclasse definisce comportamenti diversi in base alla modalità corrente
* Questa classe astratta serve solo a:
* - Fornire un’interfaccia comune per gli stati
* - Bloccare operazioni non consentite in base allo stato
*
* La logica di gestione allarmi è centralizzata in HomeSystem.
*/
public abstract class SystemState {
    
    // =============================
    // COMPORTAMENTI SUL SISTEMA
    // =============================

    /** Installa sensori nel sistema
    * Ogni stato può decidere se permettere o bloccare l'installazione */
    public void installSensorsS(HomeSystem system) {
        throw new IllegalStateException("Operazione non consentita nello stato corrente");
    }

    /** Mostra i sensori del sistema */
    public void showSensors(HomeSystem system) {
        throw new IllegalStateException("Operazione non consentita nello stato corrente");
    }

    /** Resetta un singolo sensore dato l'ID */
    public void resetSensorByIdS(HomeSystem system) {
        throw new IllegalStateException("Operazione non consentita nello stato corrente");
    }

    /** Resetta tutti i sensori */
    public void resetSensorsS(HomeSystem system) {
        throw new IllegalStateException("Operazione non consentita nello stato corrente");
    }

    /** Installa moduli aggiuntivi su un sensore */
    public void installModules(HomeSystem system) {
        throw new IllegalStateException("Operazione non consentita nello stato corrente");
    }

    /** Mostra le statistiche di tutti i sensori */
    public void showStatisticsS(HomeSystem system) {
        throw new IllegalStateException("Operazione non consentita nello stato corrente");
    }

    /** Simula un ciclo di sensori (letture, allarmi, ecc.) */
    public void simulateSensorCycleS(HomeSystem system) {
        throw new IllegalStateException("Operazione non consentita nello stato corrente");
    }
}