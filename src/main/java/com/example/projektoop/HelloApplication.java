package com.example.projektoop;

import com.example.projektoop.controllers.HelloController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    private InverterService inverterService;
    private double x, y;

    @Override
    public void start(Stage primaryStage) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);

        HelloController controller = fxmlLoader.getController();

        inverterService = new InverterService(5);
        inverterService.setOnDataFetched(controller::updatePlane);
        inverterService.setOnChartDataFetched(controller::updateChart);
        inverterService.setOnSmartDeviceFetched(controller::updateSmartDevice);

        inverterService.startCommunication();

        primaryStage.setTitle("Efficient Solar");
        primaryStage.setScene(scene);


        root.setOnMousePressed(mouseEvent -> {
            x = mouseEvent.getSceneX();
            y = mouseEvent.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            primaryStage.setX(event.getScreenX() - x);
            primaryStage.setY(event.getScreenY() - y);
        });


        primaryStage.show();
    }

    @Override
    public void stop() {
        if (inverterService != null) {
            inverterService.stopCommunication();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        launch(args);

    }
}