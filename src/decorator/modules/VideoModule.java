package decorator.modules;

import decorator.SensorDecorator;
import models.*;

public class VideoModule extends SensorDecorator {

    public VideoModule(Sensor sensor) {
        super(sensor);
        addModule("VideoModule");
    }

    @Override
    public String getModuleName() {
        return "VideoModule";
    }
}