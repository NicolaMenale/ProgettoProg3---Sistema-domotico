package decorator.modules;

import decorator.SensorDecorator;
import models.*;

public class FlowMonitoringModule extends SensorDecorator {

    public FlowMonitoringModule(Sensor sensor) {
        super(sensor);
        addModule("FlowMonitoringModule"); // registra subito il modulo
    }

    @Override
    public String getModuleName() {
        return "FlowMonitoringModule";
    }
}