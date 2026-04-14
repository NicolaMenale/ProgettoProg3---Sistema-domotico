package decorator.modules;

import decorator.SensorDecorator;
import models.*;

public class LoggingModule extends SensorDecorator {

    public LoggingModule(Sensor sensor) {
        super(sensor);
        addModule("LoggingModule");
    }

    @Override
    public String getModuleName() {
        return "LoggingModule";
    }
}