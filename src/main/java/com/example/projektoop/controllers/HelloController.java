package com.example.projektoop.controllers;


import com.example.projektoop.controllers.pane.OverviewPaneControllers;
import com.example.projektoop.device.SmartDevice;
import com.example.projektoop.models.InverterModel;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.List;

public class HelloController {

    @FXML
    private Button btnOverview;
    @FXML
    private Button btnMenus;
    @FXML
    private Button btnSettings;
    @FXML
    private Button btnSignout;
    @FXML
    private Pane pnlOverview;
    @FXML
    private Pane pnlMenus;
    @FXML
    private Label cloud_cover;
    @FXML
    private Label temperature;

    @FXML
    public Pane pnlSettings;

    private OverviewPaneControllers overviewPaneControllers;

    @FXML
    private void initialize() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projektoop/Pane/overview-pane.fxml"));
            Parent overviewPaneRoot = loader.load();
            overviewPaneControllers = loader.getController();
            pnlOverview.getChildren().add(overviewPaneRoot);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    public void updatePlane(InverterModel data) {
        overviewPaneControllers.updatePlane1(data);
    }

    public void updateChart(InverterModel data) {
        overviewPaneControllers.updateChart(data);
    }

    public void updateSmartDevice(List<SmartDevice> data) {
        overviewPaneControllers.updateSmartDevice(data);
    }

    public void handleClicks(ActionEvent actionEvent) {
        if (actionEvent.getSource() == btnOverview) {
            pnlOverview.toFront();
            pnlOverview.setVisible(true);
            pnlMenus.setVisible(false);
            pnlSettings.setVisible(false);
        }
        if (actionEvent.getSource() == btnMenus) {
            pnlMenus.toFront();
            pnlOverview.setVisible(false);
            pnlMenus.setVisible(true);
            pnlSettings.setVisible(false);
        }
        if (actionEvent.getSource() == btnSettings) {
            pnlSettings.toFront();
            pnlOverview.setVisible(false);
            pnlMenus.setVisible(false);
            pnlSettings.setVisible(true);
        }
        if (actionEvent.getSource() == btnSignout) {
            Platform.exit();
        }
    }
}