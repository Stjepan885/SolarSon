package com.example.projektoop.controllers;

import com.example.projektoop.controllers.pane.DevicePaneControllers;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class SmartDeviceController {

    @FXML
    private Label deviceLabel;
    @FXML
    private Label power_label;
    @FXML
    private Button statusButton;
    @FXML
    private Button activationButton;
    @FXML
    private Circle onlineDot;
    @FXML
    private ChoiceBox priorityQueue;

    private String id;
    private String name;

    private DevicePaneControllers devicePaneControllers;
    @FXML
    public void initialize() {
        statusButton.setOnAction(event -> handleButtonClick());
        activationButton.setOnAction(actionEvent -> handleActivationButtonClick());

        priorityQueue.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            devicePaneControllers.editPriority(id, newValue.toString());
        });
    }
    private void handleButtonClick() {
        if (devicePaneControllers != null) {
            devicePaneControllers.handleItemButtonClick(id);
        }
        if (statusButton.getText().equals("ON")) {
            statusButton.setText("OFF");
        }else {
            statusButton.setText("ON");
        }
    }
    private void handleActivationButtonClick() {
        boolean isActivated = devicePaneControllers.handleActivationButtonClick(id, activationButton.getText());
        if (isActivated) {
            activationButton.setText("ON");
        }else {
           activationButton.setText("OFF");
        }
    }
    public void setDevicePaneControllers(DevicePaneControllers devicePaneControllers) {
        this.devicePaneControllers = devicePaneControllers;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
        deviceLabel.setText(name);
    }
    public void setId(String id) {
        this.id = id;
    }

    public void setPower_label(String power) {
        power_label.setText(power);
    }
    public void setOnlineDot(boolean online) {
        if (online) {
            onlineDot.setFill(Color.GREEN);
        }else {
            onlineDot.setFill(Color.RED);
        }
    }
    public void setStatusButton(boolean working){
        if (working) {
            statusButton.setText("ON");
        }else {
            statusButton.setText("OFF");
        }
    }
}
