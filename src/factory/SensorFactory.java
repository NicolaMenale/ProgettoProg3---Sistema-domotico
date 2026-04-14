package factory;

import models.*;

// ==============================
// CLASSE ASTRATTA: FACTORY SENSOR
// ==============================
//
// Definisce il metodo factory per creare sensori senza conoscere
// il tipo concreto. Le sottoclassi implementano la logica specifica.
//
public abstract class SensorFactory {

    // ==============================
    // METODO PUBBLICO DI CREAZIONE
    // ==============================
    // Factory Method: crea un sensore a partire dall'ID
    public Sensor createSensor(String id) {
        // Chiama il metodo astratto che sarà implementato nelle sottoclassi
        return instantiateSensor(id);
    }

    // ==============================
    // METODO ASTRATTO
    // ==============================
    // Le sottoclassi devono implementare questo metodo per
    // restituire un sensore concreto (Monitoring o Intervention)
    protected abstract Sensor instantiateSensor(String id);
}