package decorator.modules;

import decorator.SensorDecorator;
import models.*;

public class VentModule extends SensorDecorator {

    public VentModule(Sensor sensor) {
        super(sensor);
        addModule("VentModule");
    }

    @Override
    public String getModuleName() {
        return "VentModule";
    }
}