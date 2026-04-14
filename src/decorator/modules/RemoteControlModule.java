package decorator.modules;

import decorator.SensorDecorator;
import models.*;

public class RemoteControlModule extends SensorDecorator {

    public RemoteControlModule(Sensor sensor) {
        super(sensor);
        addModule("RemoteControlModule");
    }

    @Override
    public String getModuleName() {
        return "RemoteControlModule";
    }
}