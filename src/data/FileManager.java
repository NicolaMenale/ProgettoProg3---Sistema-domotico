package data;

import java.io.*;
import java.time.LocalDateTime;
import java.util.*;

import decorator.*;
import model.*;

public class FileManager {
    private static final String SENSOR_FILE = "src/data/sensors.txt";
    private static final String STAT_FILE = "src/data/statistics.txt";

    public static void saveSensors(List<Sensor> sensors) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(SENSOR_FILE))) {
            for (Sensor s : sensors) {
                Sensor base = getBaseSensor(s);
                StringBuilder line = new StringBuilder();
                if (base instanceof MonitoringSensor ms) {
                    line.append(ms.getId())
                            .append(";MONITORING;")
                            .append(ms.getThreshold());
                }

                else if (base instanceof InterventionSensor is) {
                    line.append(is.getId())
                            .append(";INTERVENTION");
                }

                // salva i decorator
                List<String> decorators = new ArrayList<>();
                Sensor current = s;
                while (current instanceof SensorDecorator dec) {
                    decorators.add(dec.getClass().getSimpleName());
                    current = dec.getWrappedSensor();
                }

                // invertiamo l'ordine
                Collections.reverse(decorators);

                // salviamo nel file
                for (String d : decorators) {
                    line.append(";").append(d);
                }
                writer.println(line.toString());
            }
        } catch (IOException e) {
            System.out.println("Errore salvataggio sensori.");
        }
    }

    private static Sensor getBaseSensor(Sensor sensor) {
        Sensor current = sensor;
        while (current instanceof SensorDecorator decorator) {
            current = decorator.getWrappedSensor();
        }
        return current;
    }

    public static List<Sensor> loadSensors() {
        List<Sensor> sensors = new ArrayList<>();
        File file = new File(SENSOR_FILE);
        if (!file.exists())
            return sensors;
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                String id = parts[0];
                String type = parts[1];
                Sensor sensor;
                if ("MONITORING".equals(type)) {
                    int threshold = (int) Math.round(Double.parseDouble(parts[2]));
                    sensor = new MonitoringSensor(id, threshold);
                    // decorator partono da indice 3
                    for (int i = 3; i < parts.length; i++) {
                        sensor = applyDecorator(parts[i], sensor);
                    }
                } else {
                    sensor = new InterventionSensor(id);
                    for (int i = 2; i < parts.length; i++) {
                        sensor = applyDecorator(parts[i], sensor);
                    }
                }
                sensors.add(sensor);
            }
        } catch (IOException e) {
            System.out.println("Errore caricamento sensori.");
        }
        return sensors;
    }

    private static Sensor applyDecorator(String moduleName, Sensor sensor) {
        try {
            moduleName = moduleName.trim();
            String className = "decorator." + moduleName;
            Class<?> clazz = Class.forName(className);
            return (Sensor) clazz.getConstructor(Sensor.class).newInstance(sensor);
        } catch (Exception e) {
            System.out.println("Errore caricamento modulo: " + moduleName);
            return sensor;
        }
    }

    public static void saveStatistics(List<Sensor> sensors) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(STAT_FILE))) {
            for (Sensor s : sensors) {
                Sensor base = getBaseSensor(s);
                StringBuilder line = new StringBuilder();

                if (base instanceof MonitoringSensor ms) {
                    line.append(ms.getId())
                            .append(";MONITORING;")
                            .append(ms.isActive() ? "SI" : "NO").append(";")
                            .append(ms.getThreshold()).append(";")
                            .append(String.join(",", ms.getReadings().stream()
                                    .map(String::valueOf).toList()))
                            .append(";")
                            .append(ms.getAlarmCount()).append(";")
                            .append(String.join(",", ms.getAlarmHistory().stream()
                                    .map(Object::toString).toList()));
                } else if (base instanceof InterventionSensor is) {
                    line.append(is.getId())
                            .append(";INTERVENTION;")
                            .append(is.isActive() ? "SI" : "NO").append(";")
                            .append(is.getActivationCount()).append(";")
                            .append(String.join(",", is.getActivationHistory().stream()
                                    .map(Object::toString).toList()));
                }

                writer.println(line.toString());
            }
        } catch (IOException e) {
            System.out.println("Errore salvataggio statistiche.");
        }
    }

    public static void loadStatistics(List<Sensor> sensors) {
        File file = new File(STAT_FILE);
        if (!file.exists())
            return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                String id = parts[0];
                Sensor s = sensors.stream()
                        .filter(sensor -> sensor.getId().equals(id))
                        .findFirst().orElse(null);
                if (s == null)
                    continue; // sensore non trovato

                if (s instanceof MonitoringSensor ms && "MONITORING".equals(parts[1])) {
                    ms.setActive(parts[2].equals("SI"));
                    ms.setThreshold((int) Math.round(Double.parseDouble(parts[3])));
                    ms.setAlarmCount(Integer.parseInt(parts[5]));

                    // Letture
                    if (!parts[4].isEmpty()) {
                        String[] readings = parts[4].split(",");
                        for (String r : readings) {
                            ms.getReadings().add(Double.parseDouble(r));
                        }
                    }

                    // Storico allarmi
                    if (parts.length > 6 && !parts[6].isEmpty()) {
                        String[] alarms = parts[6].split(",");
                        for (String a : alarms) {
                            ms.getAlarmHistory().add(LocalDateTime.parse(a));
                        }
                    }
                } else if (s instanceof InterventionSensor is && "INTERVENTION".equals(parts[1])) {
                    is.setActive(parts[2].equals("SI"));
                    is.setNumberOfActivations(Integer.parseInt(parts[3]));

                    if (parts.length > 4 && !parts[4].isEmpty()) {
                        String[] activations = parts[4].split(",");
                        for (String a : activations) {
                            is.getActivationHistory().add(LocalDateTime.parse(a));
                        }
                    }
                }
                
            }
        } catch (IOException e) {
            System.out.println("Errore caricamento statistiche.");
        }
    }

    public static void clearDataFiles() {
        File folder = new File("data");
        if (!folder.exists() || !folder.isDirectory())
            return;

        File[] files = folder.listFiles();
        if (files == null)
            return;

        for (File file : files) {
            // salta i file .java o FileManager.java
            if (file.isFile() && !file.getName().endsWith(".java" )) {
                try {
                    new FileWriter(file).close(); // svuota il file
                } catch (IOException e) {
                    System.out.println("Errore durante la pulizia del file " + file.getName() + ": " + e.getMessage());
                }
            }
        }
    }
}
