package main;

import model.Sensor;

// =============================
// CLASSE ASTRATTA: SYSTEM STATE
// =============================
//
// Rappresenta lo "stato globale" del sistema domotico.
// Ogni sottoclasse definisce comportamenti diversi in base alla modalità
// (es. Attivato, Collaudo, Manutenzione, ecc.).
//
public abstract class SystemState {

    // =============================
    // GETTER / STATO
    // =============================

    // Indica se il sistema è in modalità attiva
    public abstract boolean isActivated();

    // =============================
    // COMPORTAMENTI SUL SISTEMA
    // =============================

    // Installa un sensore nel sistema
    // Ogni stato può decidere se permettere o bloccare l'installazione
    public abstract void installSensor(HomeSystem system, Sensor sensor);

    // Resetta tutti i sensori
    // Ogni stato può decidere se permettere o bloccare il reset
    public abstract void resetSensors(HomeSystem system);


    

    // =============================
    // NOTE SULL’UTILIZZO
    // =============================
    //
    // I metodi relativi agli allarmi (handleAlarm / handleStopAlarm)
    // non sono presenti qui perché la logica degli allarmi è centralizzata
    // in HomeSystem. Gli stati servono solo a gestire:
    //   - Permessi globali sulle operazioni
    //   - Comportamenti “speciali” per modalità diverse
    //
}