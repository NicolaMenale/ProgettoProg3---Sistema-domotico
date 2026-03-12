package gui;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import main.HomeSystem;

public class InstallPairController {

    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private TextField thresholdField;
    private HomeSystem system;
    private final String[] monitorTypes = { "TEMPERATURE", "ELECTRICITY", "SMOKE", "GAS", "MOVEMENT" };
    private final String[] interventionTypes = { "AIRCONDITIONER", "POWERCUT", "SIREN", "VENT", "LOCK" };

    public void setSystem(HomeSystem system) {
        this.system = system;
    }

    @FXML
    public void initialize() {
        for (int i = 0; i < monitorTypes.length; i++) {
            typeComboBox.getItems().add(
                    monitorTypes[i] + " → " + interventionTypes[i]);
        }
        typeComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void installPair() {
        try {
            int choice = typeComboBox.getSelectionModel().getSelectedIndex();

            String monitorType = monitorTypes[choice];
            String interventionType = interventionTypes[choice];

            int threshold = Integer.parseInt(thresholdField.getText());

            system.installMonitoringPair(monitorType, interventionType, threshold);

            Stage stage = (Stage) thresholdField.getScene().getWindow();
            stage.close();
        } catch (NumberFormatException e) {
            System.out.println("Threshold non valido!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}