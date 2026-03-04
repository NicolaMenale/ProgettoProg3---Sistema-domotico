package decorator;

import model.Sensor;

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