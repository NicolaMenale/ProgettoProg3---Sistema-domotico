package main;

import java.util.ArrayList;
import java.util.List;

import decorator.SensorDecorator;
import model.MonitoringSensor;
import model.Sensor;

public class TestModeState extends SystemState {

    @Override
    public void installSensor(HomeSystem system, Sensor sensor) {
        system.addSensorInternal(sensor);
    }

    @Override
    public void resetSensors(HomeSystem system) {
        system.getSensors().forEach(Sensor::reset);
        System.out.println("Tutti i sensori sono stati resettati (Collaudo)");
    }

    public void showStatistics(HomeSystem system) {
        List<Sensor> sensors = system.getSensors();
        if (sensors.isEmpty()) {
            System.out.println("Nessun sensore installato.");
            return;
        }

        System.out.println("\n--- STATISTICHE SENSORI ---");

        for (Sensor s : sensors) {
            Sensor base = s;
            List<String> allModules = new ArrayList<>();

            // Se il sensore è decorato, ottieni i moduli e scendi fino al sensore base
            if (s instanceof SensorDecorator decorator) {
                allModules.addAll(decorator.getModules());
                base = decorator.getBaseSensor();
            }

            String type = (base instanceof MonitoringSensor) ? "Monitoraggio" : "Intervento";

            System.out.println("- " + base.getId() + " | Tipo: " + type);

            if (base instanceof MonitoringSensor ms) {
                System.out.println("  Threshold: " + ms.getThreshold());
                System.out.println("  Letture: " + ms.getReadings());
                System.out.println("  Numero allarmi: " + ms.getAlarmCount());
                System.out.println("  Storico allarmi: " + ms.getAlarmHistory());
            }

            if (!allModules.isEmpty()) {
                System.out.println("  Moduli installati: " + String.join(", ", allModules));
            }
        }
    }

    @Override
    public void handleAlarm(HomeSystem system, Sensor monitor) {
        System.out.println("Sistema in collaudo: allarme ignorato.");
    }

    @Override
    public void handleStopAlarm(HomeSystem system, Sensor monitor) {
        // niente
    }
}