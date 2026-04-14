package decorator.modules;

import decorator.SensorDecorator;
import models.*;

public class AudioModule extends SensorDecorator {

    public AudioModule(Sensor sensor) {
        super(sensor);
        addModule("AudioModule");
    }

    @Override
    public String getModuleName() {
        return "AudioModule";
    }
}