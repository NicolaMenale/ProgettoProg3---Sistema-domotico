package decorator;

import java.util.*;

import models.*;
import decorator.modules.*;

// ==============================
// CLASSE: ModuleRegistry
// ==============================
//
// Gestisce tutti i moduli aggiuntivi disponibili per i sensori
// e crea decoratori per installarli sui sensori.
// Contiene:
//   - mappatura sensori -> moduli disponibili
//   - factory per creare i decoratori
//
public class ModuleRegistry {

    // ==============================
    // MODULI DISPONIBILI PER TIPO SENSORE
    // ==============================

    // Mappa tra tipo base sensore e lista dei moduli disponibili
    private static final Map<String, List<String>> SENSOR_MODULES = new HashMap<>();
    static {
        // Sensori di monitoraggio
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

        // Sensori di intervento
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
    // METODI PUBBLICI
    // ==============================

    /**
     * Restituisce la lista dei moduli disponibili per un sensore
     * Esclude quelli già installati
     */
    public static List<String> getAvailableModules(Sensor sensor) {
        // ottiene il sensore base senza decoratori
        Sensor baseSensor = getBaseSensor(sensor);

        // prende il prefisso dell'ID (es. TEMPERATURE1 -> TEMPERATURE)
        String prefix = baseSensor.getId().replaceAll("\\d+$", "");

        // prende tutti i moduli disponibili per il tipo e rimuove quelli già installati
        List<String> available = new ArrayList<>(SENSOR_MODULES.getOrDefault(prefix, Collections.<String>emptyList()));
        available.removeAll(getInstalledModules(sensor));

        return available;
    }

    // ==============================
    // FACTORY DEI DECORATOR
    // ==============================
    /**
     * Crea un modulo decoratore a partire dal nome del modulo
     */
    public static Sensor createModule(String moduleName, Sensor sensor) {
        if (moduleName.equals("AudioModule")) {
            return new AudioModule(sensor);
        } else if (moduleName.equals("VideoModule")) {
            return new VideoModule(sensor);
        } else if (moduleName.equals("NotificationModule")) {
            return new NotificationModule(sensor);
        } else if (moduleName.equals("LoggingModule")) {
            return new LoggingModule(sensor);
        } else if (moduleName.equals("SensorCalibrationModule")) {
            return new SensorCalibrationModule(sensor);
        } else if (moduleName.equals("AnalyticsModule")) {
            return new AnalyticsModule(sensor);
        } else if (moduleName.equals("SmokeDetectionModule")) {
            return new SmokeDetectionModule(sensor);
        } else if (moduleName.equals("GasLeakModule")) {
            return new GasLeakModule(sensor);
        } else if (moduleName.equals("RemoteControlModule")) {
            return new RemoteControlModule(sensor);
        } else if (moduleName.equals("AlarmModule")) {
            return new AlarmModule(sensor);
        } else if (moduleName.equals("FlowMonitoringModule")) {
            return new FlowMonitoringModule(sensor);
        } else if (moduleName.equals("VentModule")) {
            return new VentModule(sensor);
        } else if (moduleName.equals("LockModule")) {
            return new LockModule(sensor);
        } else if (moduleName.equals("PowerCalibrationModule")) {
            return new PowerCalibrationModule(sensor);
        } else if (moduleName.equals("PowerModule")) {
            return new PowerModule(sensor);
        } else {
            return null;
        }
    }

    // ==============================
    // METODI PRIVATI / HELPERS
    // ==============================

    /**
     * Restituisce il sensore base senza i decoratori
     */
    private static Sensor getBaseSensor(Sensor sensor) {
        Sensor current = sensor;

        // rimuove tutti i decoratori fino a tornare al sensore originale
        while (current instanceof SensorDecorator) {
            current = ((SensorDecorator) current).getWrappedSensor();
        }

        return current;
    }

    /**
     * Restituisce la lista dei moduli installati su un sensore
     */
    public static List<String> getInstalledModules(Sensor sensor) {
        if (sensor instanceof SensorDecorator sd) {
            return sd.getModules(); // prende i moduli dal decoratore
        }
        return new ArrayList<>(); // sensore base senza moduli
    }
}