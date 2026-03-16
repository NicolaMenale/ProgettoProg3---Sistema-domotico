package decorator;

import model.Sensor;
import java.util.*;

// ==============================
// DECORATOR ASTRACT PER SENSORI
// ==============================
//
// Permette di aggiungere moduli ai sensori senza modificare
// le classi concrete dei sensori stessi.
// Mantiene lista dei moduli installati e riferimento al sensore originale.
//
public abstract class SensorDecorator extends Sensor {

    protected Sensor wrappedSensor; // sensore originale decorato
    protected List<String> modules; // lista dei moduli installati

    // ==============================
    // COSTRUTTORE
    // ==============================
    // Inizializza il decoratore a partire da un sensore
    public SensorDecorator(Sensor sensor) {
        super(sensor.getId());
        this.wrappedSensor = sensor;

        // eredita eventuali moduli già installati se il sensore è un decoratore
        if (sensor instanceof SensorDecorator sd) {
            this.modules = new ArrayList<>(sd.modules);
        } else {
            this.modules = new ArrayList<>();
        }
    }

    // ==============================
    // GETTER MODULI
    // ==============================

    // Restituisce il nome del modulo corrente (ultimo aggiunto)
    public String getModuleName() {
        if (modules.isEmpty())
            return "Decoratore generico";
        return modules.get(modules.size() - 1);
    }

    // Restituisce il sensore decorato
    public Sensor getWrappedSensor() {
        return wrappedSensor;
    }

    // Restituisce tutti i moduli installati, incluso questo decoratore
    public List<String> getModules() {
        List<String> modules = new ArrayList<>();
        if (wrappedSensor instanceof SensorDecorator decorator) {
            modules.addAll(decorator.getModules());
        }
        modules.add(getModuleName());
        return modules;
    }

    // Aggiunge un modulo alla lista dei moduli
    public void addModule(String moduleName) {
        modules.add(moduleName);
    }

    // Restituisce il sensore base senza decoratori
    public Sensor getBaseSensor() {
        Sensor current = this;
        while (current instanceof SensorDecorator decorator) {
            current = decorator.getWrappedSensor();
        }
        return current;
    }

    // ==============================
    // RESET
    // ==============================

    // Reset completo del sensore base
    @Override
    public void reset() {
        wrappedSensor.reset();
    }

    // ==============================
    // STATISTICHE
    // ==============================

    // Restituisce statistiche complete del sensore e moduli
    @Override
    public String getStatistics() {

        // prendi il sensore base
        Sensor base = getBaseSensor(this);

        StringBuilder sb = new StringBuilder();

        // aggiungi statistiche del sensore base
        sb.append(base.getStatistics());

        // raccogli tutti i moduli decoratori
        List<String> modules = new ArrayList<>();
        Sensor current = this;

        while (current instanceof SensorDecorator dec) {
            modules.add(dec.getModuleName());
            current = dec.getWrappedSensor();
        }

        // ordine corretto (base → ultimo modulo)
        Collections.reverse(modules);

        // aggiungi i moduli alla stringa
        sb.append("  Moduli installati: ").append(String.join(", ", modules)).append("\n");

        return sb.toString();
    }

    // ==============================
    // METODO PRIVATO DI SUPPORTO
    // ==============================

    // Restituisce il sensore base a partire da un sensore generico
    private Sensor getBaseSensor(Sensor sensor) {
        Sensor current = sensor;
        while (current instanceof SensorDecorator decorator) {
            current = decorator.getWrappedSensor();
        }
        return current;
    }
}