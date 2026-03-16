package main;

import java.util.*;
import java.util.function.Function;
import model.*;
import decorator.*;

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
    // FACTORY DEI DECORATOR
    // ==============================

    // Mappa tra nome modulo e funzione per creare il decoratore
    private static final Map<String, Function<Sensor, Sensor>> MODULE_CREATORS = new HashMap<>();

    static {
        // Crea ogni modulo come decoratore su un sensore
        MODULE_CREATORS.put("AudioModule", s -> new AudioModule(s));
        MODULE_CREATORS.put("VideoModule", s -> new VideoModule(s));
        MODULE_CREATORS.put("NotificationModule", s -> new NotificationModule(s));
        MODULE_CREATORS.put("LoggingModule", s -> new LoggingModule(s));
        MODULE_CREATORS.put("SensorCalibrationModule", s -> new SensorCalibrationModule(s));
        MODULE_CREATORS.put("AnalyticsModule", s -> new AnalyticsModule(s));
        MODULE_CREATORS.put("SmokeDetectionModule", s -> new SmokeDetectionModule(s));
        MODULE_CREATORS.put("GasLeakModule", s -> new GasLeakModule(s));
        MODULE_CREATORS.put("RemoteControlModule", s -> new RemoteControlModule(s));
        MODULE_CREATORS.put("AlarmModule", s -> new AlarmModule(s));
        MODULE_CREATORS.put("FlowMonitoringModule", s -> new FlowMonitoringModule(s));
        MODULE_CREATORS.put("VentModule", s -> new VentModule(s));
        MODULE_CREATORS.put("LockModule", s -> new LockModule(s));
        MODULE_CREATORS.put("PowerCalibrationModule", s -> new PowerCalibrationModule(s));
        MODULE_CREATORS.put("PowerModule", s -> new PowerModule(s));
    }

    // ==============================
    // METODI PUBBLICI
    // ==============================

    // Restituisce la lista dei moduli disponibili per un sensore
    // Esclude quelli già installati
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

    // Crea un modulo decoratore a partire dal nome del modulo
    public static Sensor createModule(String moduleName, Sensor sensor) {
        Function<Sensor, Sensor> creator = MODULE_CREATORS.get(moduleName);
        if (creator == null) // modulo non trovato
            return null;

        return creator.apply(sensor); // ritorna il sensore decorato
    }

    // ==============================
    // METODI PRIVATI / HELPERS
    // ==============================

    // Restituisce il sensore base senza i decoratori
    private static Sensor getBaseSensor(Sensor sensor) {
        Sensor current = sensor;

        // rimuove tutti i decoratori fino a tornare al sensore originale
        while (current instanceof SensorDecorator) {
            current = ((SensorDecorator) current).getWrappedSensor();
        }

        return current;
    }

    // Restituisce la lista dei moduli installati su un sensore
    public static List<String> getInstalledModules(Sensor sensor) {
        if (sensor instanceof SensorDecorator sd) {
            return sd.getModules(); // prende i moduli dal decoratore
        }
        return new ArrayList<>(); // sensore base senza moduli
    }
}