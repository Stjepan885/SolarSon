package com.example.projektoop.controllers.pane;

import com.example.projektoop.preferences.SettingsManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class SettingsPaneControllers {
    @FXML
    private ChoiceBox<String> scan_interval;
    @FXML
    private ChoiceBox<String> save_interval;
    @FXML
    private ChoiceBox<String> save_night_interval;
    @FXML
    private ChoiceBox<String> chart_update_interval;
    @FXML
    private ChoiceBox<String> chart_reset_interval;
    @FXML
    private TextField off_time;
    @FXML
    private TextField on_time;
    @FXML
    private TextField charging_difference;
    @FXML
    private TextField max_power;
    @FXML
    private TextField max_current;
    @FXML
    private TextField email;
    @FXML
    private TextField port;
    @FXML
    private PasswordField password;
    @FXML
    private Button save_button;


    @FXML
    public void initialize() {
        save_button.setOnAction(event -> {
            handleSaveButton();
        });

        String preferenceValue = SettingsManager.getUserPreference("save", "15");
        save_interval.setValue(preferenceValue);
        preferenceValue = SettingsManager.getUserPreference("save_night", "60");
        save_night_interval.setValue(preferenceValue);
        preferenceValue = SettingsManager.getUserPreference("chart_update", "15");
        chart_update_interval.setValue(preferenceValue);
        preferenceValue = SettingsManager.getUserPreference("chart_reset", "24");
        chart_reset_interval.setValue(preferenceValue);

        preferenceValue = SettingsManager.getUserPreference("off_time", "18");
        off_time.setText(preferenceValue);
        preferenceValue = SettingsManager.getUserPreference("on_time", "9");
        on_time.setText(preferenceValue);
        preferenceValue = SettingsManager.getUserPreference("charging_difference", "100");
        charging_difference.setText(preferenceValue);
        preferenceValue = SettingsManager.getUserPreference("max_power", "2500");
        max_power.setText(preferenceValue);
        preferenceValue = SettingsManager.getUserPreference("max_current", "10");
        max_current.setText(preferenceValue);
        preferenceValue = SettingsManager.getUserPreference("email", "");
        email.setText(preferenceValue);
        preferenceValue = SettingsManager.getUserPreference("port", "/dev/hidraw0");
        port.setText(preferenceValue);

        // Set up a listener to react to selection changes
        scan_interval.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            SettingsManager.saveUserPreference("scan", newValue);
            System.out.println("Scan interval set to: " + newValue);
        });
        save_interval.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            SettingsManager.saveUserPreference("save", newValue);
            System.out.println("Selected item: " + newValue);
        });
        save_night_interval.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            SettingsManager.saveUserPreference("save_night", newValue);
            System.out.println("Selected item: " + newValue);
        });
        chart_update_interval.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            SettingsManager.saveUserPreference("chart_update", newValue);
            System.out.println("Selected item: " + newValue);
        });
        chart_reset_interval.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            SettingsManager.saveUserPreference("chart_reset", newValue);
            System.out.println("Selected item: " + newValue);
        });



        off_time.textProperty().addListener((observable, oldValue, newValue) -> {
            SettingsManager.saveUserPreference("off_time", newValue);
            System.out.println("Off time set to: " + newValue);
        });
        on_time.textProperty().addListener((observable, oldValue, newValue) -> {
            SettingsManager.saveUserPreference("on_time", newValue);
            System.out.println("On time set to: " + newValue);
        });
        charging_difference.textProperty().addListener((observable, oldValue, newValue) -> {
            SettingsManager.saveUserPreference("charging_difference", newValue);
            System.out.println("Charging difference set to: " + newValue);
        });
        max_power.textProperty().addListener((observable, oldValue, newValue) -> {
            SettingsManager.saveUserPreference("max_power", newValue);
            System.out.println("Max power set to: " + newValue);
        });
        max_current.textProperty().addListener((observable, oldValue, newValue) -> {
            SettingsManager.saveUserPreference("max_current", newValue);
            System.out.println("Max current set to: " + newValue);
        });

    }

    private void handleSaveButton() {
        SettingsManager.saveUserPreference("email", email.getText());
        SettingsManager.saveUserPreference("port", port.getText());
        if (!password.getText().isEmpty())
            SettingsManager.saveUserPreference("password", password.getText());
    }

    public String getSelectedChoice() {
        return scan_interval.getSelectionModel().getSelectedItem();
    }

}
