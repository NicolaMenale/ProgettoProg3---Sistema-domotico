package decorator;

import model.Sensor;

public class PowerModule extends SensorDecorator {

    public PowerModule(Sensor sensor) {
        super(sensor);
        addModule("PowerModule");
    }

    @Override
    public String getModuleName() {
        return "PowerModule";
    }
}