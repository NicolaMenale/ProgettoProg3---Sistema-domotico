package decorator.modules;

import decorator.SensorDecorator;
import models.*;

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