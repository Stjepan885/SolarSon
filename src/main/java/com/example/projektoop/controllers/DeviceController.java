package com.example.projektoop.controllers;

import com.example.projektoop.controllers.pane.OverviewPaneControllers;
import javafx.fxml.FXML;

import javafx.scene.control.Label;

public class DeviceController {
    @FXML
    private Label labelActive;
    private String name;

    private OverviewPaneControllers overviewPaneControllers;

    @FXML
    public void initialize() {
    }

    public void setOverviewPaneControllers(OverviewPaneControllers overviewPaneControllers) {
        this.overviewPaneControllers = overviewPaneControllers;
    }

    public void setName(String name) {
        this.name = name;
        labelActive.setText(name);
    }

    public String getName() {
        return name;
    }
}


