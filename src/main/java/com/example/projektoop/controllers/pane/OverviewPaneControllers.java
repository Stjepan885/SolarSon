package com.example.projektoop.controllers.pane;

import com.example.projektoop.controllers.DeviceController;
import com.example.projektoop.device.SmartDevice;
import com.example.projektoop.models.InverterModel;
import com.example.projektoop.preferences.SettingsManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.abs;

public class OverviewPaneControllers {
    @FXML
    private Label power_out;
    @FXML
    private Label power_in;
    @FXML
    private Label power_Battery;
    @FXML
    private Label battery_capa;
    @FXML
    private Pane chartPane;
    @FXML
    private Label inverter_temperature;
    @FXML
    private Label grid_power;
    @FXML
    private Label battery_power;
    @FXML
    private ImageView battery_png;
    @FXML
    private Image bla;
    @FXML
    private Line line_solar;
    @FXML
    private Line line_grid;
    @FXML
    private Line line_battery;
    @FXML
    private ImageView grid_png;
    @FXML
    private ImageView solar_panel_png;
    @FXML
    public VBox pnDevices;
    @FXML
    private Label pv1_power_label;
    @FXML
    private Label pv2_power_label;
    @FXML
    private Label pv1_voltage_label;
    @FXML
    private Label pv2_voltage_label;
    @FXML
    private Label active_power_label;
    @FXML
    private Label apparent_power_label;
    @FXML
    private Label load_label;


    private NumberAxis xAxis;
    private NumberAxis yAxis;

    private LineChart<Number,Number> lineChart;
    private XYChart.Series<Number,Number> series;
    private XYChart.Series<Number,Number> seriesOutput;
    private XYChart.Series<Number,Number> batteryOutput;
    private XYChart.Series<Number,Number> batteryInput;
    private int counter;

    int resetChartValue;
    int chartUpdateInterval;

    private Map<String, DeviceController> deviceControllers = new HashMap<>();

    @FXML
    private void initialize() {
        String preferenceValue = SettingsManager.getUserPreference("chart_reset", "24");
        resetChartValue = Integer.parseInt(preferenceValue);
        preferenceValue = SettingsManager.getUserPreference("chart_update", "15");
        chartUpdateInterval = Integer.parseInt(preferenceValue);

        //counter for x axis
        counter = 0;

        xAxis = new NumberAxis();
        yAxis = new NumberAxis();

        yAxis.tickLabelFontProperty().set(Font.font(15));

        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.lookup(".chart-plot-background").setStyle("-fx-background-color: transparent;");
        lineChart.setMaxWidth(700);
        lineChart.setMinWidth(700);
        lineChart.setMaxHeight(300);

        series = new XYChart.Series<>();
        seriesOutput = new XYChart.Series<>();
        batteryOutput = new XYChart.Series<>();
        batteryInput = new XYChart.Series<>();

        series.setName("Input Power");
        seriesOutput.setName("Output Power");
        batteryOutput.setName("Output Battery");
        batteryInput.setName("Input Battery");

        lineChart.getData().addAll(seriesOutput, batteryOutput ,series , batteryInput);
        lineChart.setCreateSymbols(false);

        chartPane.getChildren().add(lineChart);
        grid_png.setOpacity(0.25);
        line_grid.setOpacity(0.2);

    }

    public void updateChart(InverterModel data) {

        long timeStamp = data.getTimestamp();
        Date timeStampDate = new Date(timeStamp);

        int hour = timeStampDate.getHours();
        int minute = timeStampDate.getMinutes();
        int second = timeStampDate.getSeconds();

        if (minute == 0) {
            switch (resetChartValue) {
                case 12:
                    if (hour == 6 || hour == 18) {
                        resetChart();}
                    break;
                case 6:
                    if (hour % 6 == 0) {
                        resetChart();}
                    break;
                case 3:
                    if (hour % 3 == 0) {
                        resetChart();}
                    break;
                case 1:
                    resetChart();
                    break;
                default:
                    if (hour == 0) {
                        resetChart();}
            }
            System.out.println("Chart reset at " + hour + ":" + minute + ":" + second);
            return;
        }


        series.getData().add(new XYChart.Data<>(counter, data.getInputPower()));
        seriesOutput.getData().add(new XYChart.Data<>(counter, data.getOutputPower()));

        int batteryCapacity = data.getBatteryCapacity();
        double outputPower = data.getOutputPower();
        double inputPower = data.getInputPower();
        double batteryPower = data.getBatteryVoltage() * data.getBatteryCurrent();


        if (batteryPower < -50){
            batteryOutput.getData().add(new XYChart.Data<>(counter, -batteryPower));
            batteryInput.getData().add(new XYChart.Data<>(counter, 0));
        }else if(batteryPower > 50){
            batteryOutput.getData().add(new XYChart.Data<>(counter, 0));
            batteryInput.getData().add(new XYChart.Data<>(counter, batteryPower));
        } else {
            batteryOutput.getData().add(new XYChart.Data<>(counter, 0));
            batteryInput.getData().add(new XYChart.Data<>(counter, 0));

        }

        counter++;

        try {
            line_battery.setOpacity(1);
            if (batteryCapacity < 50) {
                Image image = new Image(getClass().getResourceAsStream("/com/example/projektoop/Images/empty-battery.png"));
                battery_png.setImage(image);
                line_battery.setOpacity(0.3);
            } else if (batteryCapacity >= 95) {
                Image image = new Image(getClass().getResourceAsStream("/com/example/projektoop/Images/full-battery.png"));
                battery_png.setImage(image);
            } else if (batteryCapacity > 80) {
                Image image = new Image(getClass().getResourceAsStream("/com/example/projektoop/Images/battery.png"));
                battery_png.setImage(image);
            }else if (batteryCapacity > 70){
                Image image = new Image(getClass().getResourceAsStream("/com/example/projektoop/Images/half-battery.png"));
                battery_png.setImage(image);
            }else {
                Image image = new Image(getClass().getResourceAsStream("/com/example/projektoop/Images/low-battery.png"));
                battery_png.setImage(image);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        double line_opacity = line_solar.getOpacity();
        if (inputPower < 10 && line_opacity == 1){
            line_solar.setOpacity(0.3);
            solar_panel_png.setOpacity(0.3);
        } else if (inputPower > 10 && line_opacity != 1){
            line_solar.setOpacity(1);
            solar_panel_png.setOpacity(1);
        }

        line_opacity = line_grid.getOpacity();

        if (outputPower < (inputPower + abs(batteryPower)+ 200)){
            if (line_opacity == 1){
                line_grid.setOpacity(0.2);
                grid_png.setOpacity(0.2);
                grid_power.setText("0 W");

            }
        } else {
            if (line_opacity != 1){
                line_grid.setOpacity(1);
                grid_png.setOpacity(1);
            }
            grid_power.setText(outputPower + " W");
        }

        //all info
        updateMoreInfo(data);

    }

    private void resetChart() {
        series.getData().clear();
        seriesOutput.getData().clear();
        batteryInput.getData().clear();
        batteryOutput.getData().clear();
        counter = 0;

    }

    private void updateMoreInfo(InverterModel data) {
        load_label.setText(data.getLoadPercentage() + " %");
        apparent_power_label.setText(data.getOutputApparentPower() + " VA");
        active_power_label.setText(data.getOutputPower() + " W");
        pv1_power_label.setText(data.getPv1InputPower() + " W");
        pv2_power_label.setText(data.getPv2InputPower() + " W");
        pv1_voltage_label.setText(data.getPv1InputVoltage() + " V");
        pv2_voltage_label.setText(data.getPv2InputVoltage() + " V");

    }

    public void  updatePlane1(InverterModel data){
        Platform.runLater(() ->
        {
            power_in.setText((int)data.getInputPower() + " W");
            power_out.setText((int)data.getOutputPower() + " W");
            power_Battery.setText(data.getBatteryCurrent() + " A");
            battery_capa.setText(data.getBatteryCapacity() + " %");
            inverter_temperature.setText(data.getHeatSinkTemperature() + "°C");
            BigDecimal db = BigDecimal.valueOf(data.getBatteryVoltage() * data.getBatteryCurrent()).setScale(1, RoundingMode.HALF_UP);
            battery_power.setText(db + " W");
        });

    }

    public void updateSmartDevice(List<SmartDevice> data) {
            // Find devices to remove from UI and controller map
            List<String> currentDeviceNames = new ArrayList<>(deviceControllers.keySet());
            for (String deviceName : currentDeviceNames) {
                boolean deviceStillExists = data.stream()
                        .anyMatch(device -> device.getName().equals(deviceName));
                if (!deviceStillExists) {
                    // Remove from the controller map
                    deviceControllers.remove(deviceName);

                    // Find and remove the corresponding UI element
                    pnDevices.getChildren().removeIf(node -> {
                        DeviceController controller = (DeviceController) node.getUserData();
                        return controller != null && controller.getName().equals(deviceName);
                    });
                }
            }

            // Add new devices that are not yet in the UI
            for (SmartDevice device : data) {
                if (!deviceControllers.containsKey(device.getName())) {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projektoop/Pane/Device.fxml"));
                        HBox node = loader.load();
                        DeviceController controller = loader.getController();
                        controller.setName(device.getName());
                        controller.setOverviewPaneControllers(this);

                        // Store the controller in the HBox user data for easy lookup
                        node.setUserData(controller);

                        deviceControllers.put(device.getName(), controller);
                        pnDevices.getChildren().add(node);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
    }
}
