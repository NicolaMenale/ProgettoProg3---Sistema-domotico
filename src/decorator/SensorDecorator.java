package decorator;

import model.Sensor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class SensorDecorator extends Sensor {

    protected Sensor wrappedSensor;
    protected List<String> modules; // lista dei moduli installati

    public SensorDecorator(Sensor sensor) {
        super(sensor.getId());
        this.wrappedSensor = sensor;

        // ereditiamo eventuali moduli già installati
        if (sensor instanceof SensorDecorator sd) {
            this.modules = new ArrayList<>(sd.modules);
        } else {
            this.modules = new ArrayList<>();
        }
    }

    public String getModuleName() {
        // Restituisce l'ultimo modulo aggiunto (per convenzione)
        if (modules.isEmpty())
            return "Decoratore generico";
        return modules.get(modules.size() - 1);
    }

    public Sensor getWrappedSensor() {
        return wrappedSensor;
    }

    public List<String> getModules() {
        List<String> modules = new ArrayList<>();
        if (wrappedSensor instanceof SensorDecorator decorator) {
            modules.addAll(decorator.getModules());
        }
        modules.add(getModuleName()); // il modulo di questo decoratore
        return modules;
    }

    public void addModule(String moduleName) {
        modules.add(moduleName);
    }

    public Sensor getBaseSensor() {
        Sensor current = this;
        while (current instanceof SensorDecorator decorator) {
            current = decorator.getWrappedSensor();
        }
        return current;
    }

    // =========================
    // RESET
    // =========================

    // Reset completo (base + modulo)
    @Override
    public void reset() {
        wrappedSensor.reset();
    }

    @Override
    public void printStatistics() {
        // 1️⃣ stampa statistiche del sensore base
        Sensor base = getBaseSensor(this);
        base.printStatistics();

        // 2️⃣ raccogli tutti i moduli decoratori
        List<String> modules = new ArrayList<>();
        Sensor current = this;
        while (current instanceof SensorDecorator dec) {
            modules.add(dec.getModuleName());
            current = dec.getWrappedSensor();
        }

        // 3️⃣ inverti per ordine base -> ultimo modulo
        Collections.reverse(modules);

        // 4️⃣ stampa una sola volta tutti i moduli
        System.out.println("  Moduli installati: " + String.join(", ", modules));
    }

    private Sensor getBaseSensor(Sensor sensor) {
        Sensor current = sensor;
        while (current instanceof SensorDecorator decorator) {
            current = decorator.getWrappedSensor();
        }
        return current;
    }
}