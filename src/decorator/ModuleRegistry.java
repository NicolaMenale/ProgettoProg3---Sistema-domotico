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

    /**
     * Mappa tra tipo base sensore e lista dei moduli disponibili
     */
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

    /**
     * Mappa di factory per la creazione dinamica dei moduli (decoratori) applicabili ai sensori.
     * Ogni chiave rappresenta il nome logico del modulo, mentre il valore è un moduleCreator che si occupa di istanziare il
     * relativo decoratore.
     *
     * Questa struttura evita l'uso di lunghe catene di if/else o switch, centralizzando la creazione
     * dei moduli e rendendo il sistema facilmente estendibile: per aggiungere un
     * nuovo modulo è sufficiente registrarlo nella mappa.
     */
    private static final Map<String, ModuleCreator> MODULE_CREATORS = new HashMap<>();
    static {
        MODULE_CREATORS.put("AlarmModule", new ModuleCreator() {
            @Override
            public Sensor create(Sensor sensor) {
                return new AlarmModule(sensor);
            }
        });
        MODULE_CREATORS.put("AnalyticsModule", new ModuleCreator() {
            @Override
            public Sensor create(Sensor sensor) {
                return new AnalyticsModule(sensor);
            }
        });
        MODULE_CREATORS.put("AudioModule", new ModuleCreator() {
            @Override
            public Sensor create(Sensor sensor) {
                return new AudioModule(sensor);
            }
        });
        MODULE_CREATORS.put("FlowMonitoringModule", new ModuleCreator() {
            @Override
            public Sensor create(Sensor sensor) {
                return new FlowMonitoringModule(sensor);
            }
        });
        MODULE_CREATORS.put("GasLeakModule", new ModuleCreator() {
            @Override
            public Sensor create(Sensor sensor) {
                return new GasLeakModule(sensor);
            }
        });
        MODULE_CREATORS.put("LockModule", new ModuleCreator() {
            @Override
            public Sensor create(Sensor sensor) {
                return new LockModule(sensor);
            }
        });
        MODULE_CREATORS.put("LoggingModule", new ModuleCreator() {
            @Override
            public Sensor create(Sensor sensor) {
                return new LoggingModule(sensor);
            }
        });
        MODULE_CREATORS.put("NotificationModule", new ModuleCreator() {
            @Override
            public Sensor create(Sensor sensor) {
                return new NotificationModule(sensor);
            }
        });
        MODULE_CREATORS.put("PowerCalibrationModule", new ModuleCreator() {
            @Override
            public Sensor create(Sensor sensor) {
                return new PowerCalibrationModule(sensor);
            }
        });
        MODULE_CREATORS.put("PowerModule", new ModuleCreator() {
            @Override
            public Sensor create(Sensor sensor) {
                return new PowerModule(sensor);
            }
        });
        MODULE_CREATORS.put("RemoteControlModule", new ModuleCreator() {
            @Override
            public Sensor create(Sensor sensor) {
                return new RemoteControlModule(sensor);
            }
        });
        MODULE_CREATORS.put("SensorCalibrationModule", new ModuleCreator() {
            @Override
            public Sensor create(Sensor sensor) {
                return new SensorCalibrationModule(sensor);
            }
        });
        MODULE_CREATORS.put("SmokeDetectionModule", new ModuleCreator() {
            @Override
            public Sensor create(Sensor sensor) {
                return new SmokeDetectionModule(sensor);
            }
        });
        MODULE_CREATORS.put("VentModule", new ModuleCreator() {
            @Override
            public Sensor create(Sensor sensor) {
                return new VentModule(sensor);
            }
        });
        MODULE_CREATORS.put("VideoModule", new ModuleCreator() {
            @Override
            public Sensor create(Sensor sensor) {
                return new VideoModule(sensor);
            }
        });
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
        ModuleCreator creator = MODULE_CREATORS.get(moduleName);
        if (creator == null) {
            System.out.println("Modulo non trovato: " + moduleName);
            return sensor; // NON null
        }
        return creator.create(sensor);
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