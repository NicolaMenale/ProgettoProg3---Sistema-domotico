package decorator.modules;

import decorator.SensorDecorator;
import models.*;

public class SmokeDetectionModule extends SensorDecorator {

    public SmokeDetectionModule(Sensor sensor) {
        super(sensor);
        addModule("SmokeDetectionModule");
    }

    @Override
    public String getModuleName() {
        return "SmokeDetectionModule";
    }
}