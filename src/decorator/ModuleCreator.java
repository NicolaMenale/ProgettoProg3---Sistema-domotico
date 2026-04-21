package decorator;

import models.Sensor;

/**
 * Factory funzionale per la creazione di moduli (decoratori) associati a un sensore.
 * Ogni implementazione concreta di questa interfaccia è responsabile della creazione
 * di uno specifico tipo di modulo che avvolge un sensore, secondo il pattern Decorator.
 *
 * Serve a separare la logica di istanziazione dei moduli dalla logica di utilizzo,
 * rendendo più flessibile l'aggiunta di nuovi comportamenti ai sensori.
 */
public interface ModuleCreator {

    /**
     * Crea un nuovo modulo (decoratore) applicato al sensore fornito.
     */
    Sensor create(Sensor sensor);
}