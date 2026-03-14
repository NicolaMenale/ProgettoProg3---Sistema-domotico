package decorator;

import model.Sensor;
import java.util.*;

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
    public String getStatistics() {

        // 1️⃣ prendi il sensore base
        Sensor base = getBaseSensor(this);

        StringBuilder sb = new StringBuilder();

        // 2️⃣ aggiungi le statistiche del sensore base
        sb.append(base.getStatistics());

        // 3️⃣ raccogli tutti i moduli decoratori
        List<String> modules = new ArrayList<>();
        Sensor current = this;

        while (current instanceof SensorDecorator dec) {
            modules.add(dec.getModuleName());
            current = dec.getWrappedSensor();
        }

        // 4️⃣ ordine corretto (base → ultimo modulo)
        Collections.reverse(modules);

        // 5️⃣ aggiungi i moduli alla stringa
        sb.append("  Moduli installati: ")
                .append(String.join(", ", modules))
                .append("\n");

        return sb.toString();
    }

    private Sensor getBaseSensor(Sensor sensor) {
        Sensor current = sensor;
        while (current instanceof SensorDecorator decorator) {
            current = decorator.getWrappedSensor();
        }
        return current;
    }
}