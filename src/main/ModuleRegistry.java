package main;

import java.util.*;
import java.util.function.Function;
import model.*;
import decorator.*;

public class ModuleRegistry {

    // ==============================
    // MODULI DISPONIBILI PER TIPO SENSORE
    // ==============================

    private static final Map<String, List<String>> SENSOR_MODULES = new HashMap<>();
    static {
        SENSOR_MODULES.put("TEMPERATURE", Arrays.asList("AudioModule", "VideoModule", "NotificationModule",
                "LoggingModule", "SensorCalibrationModule"));

        SENSOR_MODULES.put("ELECTRICITY", Arrays.asList("AudioModule", "NotificationModule",
                "LoggingModule", "PowerCalibrationModule", "AnalyticsModule"));

        SENSOR_MODULES.put("SMOKE", Arrays.asList("AudioModule", "NotificationModule",
                "LoggingModule", "SmokeDetectionModule", "SensorCalibrationModule"));

        SENSOR_MODULES.put("GAS", Arrays.asList("AudioModule", "NotificationModule",
                "LoggingModule", "GasLeakModule", "SensorCalibrationModule"));

        SENSOR_MODULES.put("MOVEMENT", Arrays.asList("AudioModule", "VideoModule", "NotificationModule",
                "LoggingModule", "SensorCalibrationModule"));

        SENSOR_MODULES.put("AIRCONDITIONER", Arrays.asList("RemoteControlModule", "AlarmModule",
                "LoggingModule", "FlowMonitoringModule", "NotificationModule"));

        SENSOR_MODULES.put("POWERCUT", Arrays.asList("RemoteControlModule", "AlarmModule",
                "LoggingModule", "PowerModule", "NotificationModule"));

        SENSOR_MODULES.put("SIREN", Arrays.asList("RemoteControlModule", "AlarmModule",
                "LoggingModule", "AudioModule", "NotificationModule"));

        SENSOR_MODULES.put("VENT", Arrays.asList("RemoteControlModule", "AlarmModule",
                "LoggingModule", "VentModule", "NotificationModule"));

        SENSOR_MODULES.put("LOCK", Arrays.asList("RemoteControlModule", "AlarmModule",
                "LoggingModule", "LockModule", "NotificationModule"));
    }

    // ==============================
    // FACTORY DEI DECORATOR
    // ==============================

    private static final Map<String, Function<Sensor, Sensor>> MODULE_CREATORS = new HashMap<>();
    static {
        MODULE_CREATORS.put("AudioModule", new Function<Sensor, Sensor>() {
            public Sensor apply(Sensor s) {
                return new AudioModule(s);
            }
        });
        MODULE_CREATORS.put("VideoModule", new Function<Sensor, Sensor>() {
            public Sensor apply(Sensor s) {
                return new VideoModule(s);
            }
        });
        MODULE_CREATORS.put("NotificationModule", new Function<Sensor, Sensor>() {
            public Sensor apply(Sensor s) {
                return new NotificationModule(s);
            }
        });
        MODULE_CREATORS.put("LoggingModule", new Function<Sensor, Sensor>() {
            public Sensor apply(Sensor s) {
                return new LoggingModule(s);
            }
        });
        MODULE_CREATORS.put("SensorCalibrationModule", new Function<Sensor, Sensor>() {
            public Sensor apply(Sensor s) {
                return new SensorCalibrationModule(s);
            }
        });
        MODULE_CREATORS.put("AnalyticsModule", new Function<Sensor, Sensor>() {
            public Sensor apply(Sensor s) {
                return new AnalyticsModule(s);
            }
        });
        MODULE_CREATORS.put("SmokeDetectionModule", new Function<Sensor, Sensor>() {
            public Sensor apply(Sensor s) {
                return new SmokeDetectionModule(s);
            }
        });
        MODULE_CREATORS.put("GasLeakModule", new Function<Sensor, Sensor>() {
            public Sensor apply(Sensor s) {
                return new GasLeakModule(s);
            }
        });
        MODULE_CREATORS.put("RemoteControlModule", new Function<Sensor, Sensor>() {
            public Sensor apply(Sensor s) {
                return new RemoteControlModule(s);
            }
        });
        MODULE_CREATORS.put("AlarmModule", new Function<Sensor, Sensor>() {
            public Sensor apply(Sensor s) {
                return new AlarmModule(s);
            }
        });
        MODULE_CREATORS.put("FlowMonitoringModule", new Function<Sensor, Sensor>() {
            public Sensor apply(Sensor s) {
                return new FlowMonitoringModule(s);
            }
        });
        MODULE_CREATORS.put("VentModule", new Function<Sensor, Sensor>() {
            public Sensor apply(Sensor s) {
                return new VentModule(s);
            }
        });
        MODULE_CREATORS.put("LockModule", new Function<Sensor, Sensor>() {
            public Sensor apply(Sensor s) {
                return new LockModule(s);
            }
        });
        MODULE_CREATORS.put("PowerCalibrationModule", new Function<Sensor, Sensor>() {
            public Sensor apply(Sensor s) {
                return new PowerCalibrationModule(s);
            }
        });
        MODULE_CREATORS.put("PowerModule", new Function<Sensor, Sensor>() {
            public Sensor apply(Sensor s) {
                return new PowerModule(s);
            }
        });
    }

    /**
     * Restituisce i moduli disponibili per un sensore,
     * escludendo quelli già installati.
     */
    public static List<String> getAvailableModules(Sensor sensor) {
        Sensor baseSensor = getBaseSensor(sensor);
        String prefix = baseSensor.getId().replaceAll("\\d+$", "");

        List<String> available = new ArrayList<>(SENSOR_MODULES.getOrDefault(prefix, Collections.<String>emptyList()));
        available.removeAll(getInstalledModules(sensor));
        return available;
    }

    /**
     * Crea un modulo decoratore a partire dal nome.
     */
    public static Sensor createModule(String moduleName, Sensor sensor) {
        Function<Sensor, Sensor> creator = MODULE_CREATORS.get(moduleName);
        if (creator == null)
            return null;
        return creator.apply(sensor);
    }

    // Ottiene il sensore base senza decoratori
    private static Sensor getBaseSensor(Sensor sensor) {
        Sensor current = sensor;
        while (current instanceof SensorDecorator) {
            current = ((SensorDecorator) current).getWrappedSensor();
        }
        return current;
    }

    // Tutti i moduli installati (da tutti i decoratori)
    public static List<String> getInstalledModules(Sensor sensor) {
        if (sensor instanceof SensorDecorator sd) {
            return sd.getModules();
        }
        return new ArrayList<>();
    }
}