package decorator;

import model.Sensor;

public class GasLeakModule extends SensorDecorator {

    public GasLeakModule(Sensor sensor) {
        super(sensor);
        addModule("GasLeakModule");
    }

    @Override
    public String getModuleName() {
        return "GasLeakModule";
    }
}