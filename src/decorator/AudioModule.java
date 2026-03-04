package decorator;

import model.Sensor;

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