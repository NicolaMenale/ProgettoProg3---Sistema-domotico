package decorator.modules;

import decorator.SensorDecorator;
import models.*;

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