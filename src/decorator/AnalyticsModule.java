package decorator;

import model.Sensor;

public class AnalyticsModule extends SensorDecorator {

    public AnalyticsModule(Sensor sensor) {
        super(sensor);
        addModule("AnalyticsModule");
    }

    @Override
    public String getModuleName() {
        return "AnalyticsModule";
    }
}