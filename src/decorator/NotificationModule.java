package decorator;

import model.Sensor;

public class NotificationModule extends SensorDecorator {

    public NotificationModule(Sensor sensor) {
        super(sensor);
        addModule("NotificationModule");
    }

    @Override
    public String getModuleName() {
        return "NotificationModule";
    }
}