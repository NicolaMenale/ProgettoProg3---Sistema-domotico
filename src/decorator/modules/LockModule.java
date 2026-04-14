package decorator.modules;

import decorator.SensorDecorator;
import models.*;

public class LockModule extends SensorDecorator {

    public LockModule(Sensor sensor) {
        super(sensor);
        addModule("LockModule");
    }

    @Override
    public String getModuleName() {
        return "LockModule";
    }
}