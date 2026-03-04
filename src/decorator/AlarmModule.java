package decorator;

import model.Sensor;

public class AlarmModule extends SensorDecorator {

    public AlarmModule(Sensor sensor) {
        super(sensor);
        addModule("AlarmModule");
    }

    @Override
    public String getModuleName() {
        return "AlarmModule";
    }
}
