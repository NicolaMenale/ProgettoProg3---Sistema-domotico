package decorator;

import model.Sensor;
import java.util.ArrayList;
import java.util.List;

public abstract class SensorDecorator extends Sensor {

    protected Sensor wrappedSensor;
    protected List<String> modules; // lista dei moduli installati
    protected List<String> moduleData; // spazio dati del modulo

    public SensorDecorator(Sensor sensor) {
        super(sensor.getId());
        this.wrappedSensor = sensor;
        this.moduleData = new ArrayList<>();

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
        moduleData.clear();
        wrappedSensor.reset();
    }

    // Metodo utile per aggiungere dati al modulo
    public void addModuleData(String data) {
        moduleData.add(data);
    }

    public List<String> getModuleData() {
        return moduleData;
    }

    @Override
    public void printStatistics() {
        // 1️⃣ delega al sensore wrappato (chain Decorator)
        wrappedSensor.printStatistics();

        // 2️⃣ stampa solo il modulo corrente
        System.out.println("  Modulo installato: " + getModuleName());
        System.out.println("  Dati modulo: " + moduleData);
    }
}