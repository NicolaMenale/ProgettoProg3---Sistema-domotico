package decorator;

import model.Sensor;

public class PowerCalibrationModule extends SensorDecorator {

    public PowerCalibrationModule(Sensor sensor) {
        super(sensor);
        addModule("PowerCalibrationModule");
    }

    @Override
    public String getModuleName() {
        return "PowerCalibrationModule";
    }
}