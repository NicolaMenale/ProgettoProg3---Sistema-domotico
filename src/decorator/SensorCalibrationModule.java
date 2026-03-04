package decorator;

import model.Sensor;

public class SensorCalibrationModule extends SensorDecorator {

    public SensorCalibrationModule(Sensor sensor) {
        super(sensor);
        addModule("SensorCalibrationModule");
    }

    @Override
    public String getModuleName() {
        return "SensorCalibrationModule";
    }
}