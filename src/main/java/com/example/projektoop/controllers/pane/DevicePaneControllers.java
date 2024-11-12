package com.example.projektoop.controllers.pane;

import com.example.projektoop.SmartDeviceService;
import com.example.projektoop.SmartDeviceUpdateListener;
import com.example.projektoop.controllers.SmartDeviceController;
import com.example.projektoop.device.SmartDevice;
import com.github.realzimboguy.ewelink.api.model.home.OutletSwitch;
import com.github.realzimboguy.ewelink.api.wss.wssrsp.WssRspMsg;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DevicePaneControllers implements SmartDeviceUpdateListener {

    @FXML
    public VBox itemContainer;
    @FXML
    public ChoiceBox deviceNameList;
    @FXML
    public TextField name1TexBox;
    @FXML
    public TextField name2TextBox;
    @FXML
    public TextField maxPowerTextBox;
    @FXML
    public TextField connectedPowerTextBox;
    @FXML
    public Button addButton;
    @FXML
    public HBox dualBox;
    @FXML
    public TextField connectedPowerTextBox1;
    @FXML
    public TextField connectedPowerTextBox2;


    private Map<String, SmartDeviceController> itemControllers = new HashMap<>();
    private SmartDeviceService smartDeviceService;

    @FXML
    public void initialize() {
        addButton.setOnAction(actionEvent -> handleAddButton());
        dualBox.setVisible(false);

        smartDeviceService = SmartDeviceService.getInstance();
        smartDeviceService.addListener(this);

        List<SmartDevice> devices = smartDeviceService.getSmartDevices();
        insertIntoChoiceBox();
        for (SmartDevice deviceName : devices) {
            loadItemController(deviceName);
        }

        deviceNameList.getSelectionModel().selectedItemProperty().addListener((Observable, oldValue, newValue) -> {

            String deviceName = newValue.toString();
            String thingId = smartDeviceService.getIdOfThings(deviceName);

            //if smart device is saved in database
            if (smartDeviceService.checkIfSavedDeviceName(deviceName)) {
                dualBox.setVisible(false);
                SmartDevice device = smartDeviceService.getSmartDevice(deviceName);

                maxPowerTextBox.setText(String.valueOf(device.getMaxPower()));
                connectedPowerTextBox.setText(device.getConnectedPower() + "");

            } else if(smartDeviceService.checkIfSavedDeviceId(thingId+"0")) {

                SmartDevice device1 = smartDeviceService.getSmartDeviceById(thingId+"0");
                SmartDevice device2 = smartDeviceService.getSmartDeviceById(thingId+"1");
                name1TexBox.setText(device1.getName());
                name2TextBox.setText(device2.getName());
                connectedPowerTextBox1.setText(device1.getConnectedPower() + "");
                connectedPowerTextBox2.setText(device2.getConnectedPower() + "");
                connectedPowerTextBox.setText(device1.getConnectedPower() + device2.getConnectedPower() + "");
                maxPowerTextBox.setText(String.valueOf(device1.getMaxPower()));
                dualBox.setVisible(true);

            }   else if (!smartDeviceService.chcekIfIsDual(deviceName)){
                dualBox.setVisible(false);
                maxPowerTextBox.setText("");
                connectedPowerTextBox.setText("");
                connectedPowerTextBox1.setText("");
                connectedPowerTextBox2.setText("");
                name1TexBox.setText("");
                name2TextBox.setText("");
            }else {
                dualBox.setVisible(true);
                maxPowerTextBox.setText("");
                connectedPowerTextBox.setText("");
                connectedPowerTextBox1.setText("");
                connectedPowerTextBox2.setText("");
                name1TexBox.setText("");
                name2TextBox.setText("");
            }

        });
    }

    private void loadItemController(SmartDevice deviceName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/projektoop/smart_device.fxml"));
            HBox item = loader.load();

            SmartDeviceController itemController = loader.getController();
            itemController.setId(deviceName.getId());
            itemController.setOnlineDot(deviceName.isOnline());
            itemController.setStatusButton(deviceName.isStatus());
            itemController.setName(deviceName.getName());
            itemController.setDevicePaneControllers(this);
            itemController.setPower_label(deviceName.getConnectedPower() + " W");

            itemControllers.put(deviceName.getName(), itemController);
            itemContainer.getChildren().add(item);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void insertIntoChoiceBox() {
        ObservableList<String> deviceList = FXCollections.observableArrayList();
        deviceNameList.setItems(deviceList);

        List<String> names = smartDeviceService.getNameList();

        for (String name : names) {
            deviceList.add(name);
        }
    }

    @Override
    public void onDeviceUpdate(WssRspMsg rsp) {
        Platform.runLater(() -> {
            StringBuilder sb = new StringBuilder();
            sb.append("Device:").append(rsp.getDeviceid()).append(" - ");
            if (rsp.getParams() != null) {
                sb.append("Switch:").append(rsp.getParams().getSwitch()).append(" - ");
                sb.append("Voltage:").append(rsp.getParams().getVoltage()).append(" - ");
                sb.append("Power:").append(rsp.getParams().getPower()).append(" - ");
                sb.append("Current:").append(rsp.getParams().getCurrent()).append(" - ");
            }
            if (rsp.getParams().getPower() != null) {
                updateDeviceValue(rsp.getDeviceid(), rsp.getParams().getPower());
            }else if (rsp.getParams().getSwitch() != null) {
                updateDeviceValue(rsp.getDeviceid(), rsp.getParams().getSwitch().equals("on"));
            }else if (rsp.getParams().getSwitches() != null) {
                OutletSwitch outletSwitch = rsp.getParams().getSwitches().getFirst();
                OutletSwitch outletSwitch1 = rsp.getParams().getSwitches().getLast();
                updateDeviceValue1(rsp.getDeviceid()+"0", outletSwitch.get_switch().equals("on"));
                updateDeviceValue1(rsp.getDeviceid()+"1", outletSwitch1.get_switch().equals("on"));
            }
        });
    }

    @Override
    public void onDeviceUpdate(String id, boolean deviceState) {
        SmartDeviceController controller = itemControllers.get(smartDeviceService.getNameFromId1(id));
        if (controller != null) {
            controller.setStatusButton(deviceState);
        }
    }

    public void handleItemButtonClick(String id) {
        smartDeviceService.turnOn(id);
    }

    //add button
    public void handleAddButton() {
        String name = deviceNameList.getValue().toString();
        String id = smartDeviceService.getIdOfThings(name);
        boolean isDual = smartDeviceService.chcekIfIsDual(name);

        double maxPower = Double.parseDouble(maxPowerTextBox.getText());

        double connectedPower = 0;


        if(smartDeviceService.checkIfSavedDeviceName(name)){
            connectedPower = Double.parseDouble(connectedPowerTextBox.getText());
            smartDeviceService.editDevice(id, maxPower, connectedPower);
            return;

        } else if (smartDeviceService.checkIfSavedDeviceId(id+"0")) {
            smartDeviceService.editDevice(id+"0", maxPower, Double.parseDouble(connectedPowerTextBox1.getText()));
            smartDeviceService.editDevice(id+"1", maxPower, Double.parseDouble(connectedPowerTextBox2.getText()));
        } else {

            SmartDevice device;
            if (!isDual) {
                connectedPower = Double.parseDouble(connectedPowerTextBox.getText());
                SmartDevice sd = new SmartDevice(name, id, false, maxPower, connectedPower, 2, 0);
                smartDeviceService.addToDb(sd);
                device = sd;
                addToScreen(device);
            } else {
                SmartDevice sd1 = new SmartDevice(name1TexBox.getText(), id + "0", true, maxPower, Double.parseDouble(connectedPowerTextBox1.getText()), 2, 0);
                SmartDevice sd2 = new SmartDevice(name2TextBox.getText(), id + "1", true, maxPower, Double.parseDouble(connectedPowerTextBox2.getText()), 2, 0);
                smartDeviceService.addToDb(sd1);
                smartDeviceService.addToDb(sd2);
                sd1.setOnline(true);
                sd2.setOnline(true);
                addToScreen(sd1);
                addToScreen(sd2);
            }
        }
        dualBox.setVisible(false);
        maxPowerTextBox.setText("");
        connectedPowerTextBox.setText("");
        connectedPowerTextBox1.setText("");
        connectedPowerTextBox2.setText("");
        name1TexBox.setText("");
        name2TextBox.setText("");
    }

    public void addToScreen(SmartDevice device) {
        loadItemController(device);
    }

    public void updateDeviceValue(String id, String newValue) {
        SmartDeviceController controller = itemControllers.get(smartDeviceService.getNameFromId(id));
        if (controller != null) {
            controller.setPower_label(newValue);
        }
    }
    public void updateDeviceValue(String id, boolean newValue){
        SmartDeviceController controller = itemControllers.get(smartDeviceService.getNameFromId(id));
        if (controller != null) {
            controller.setStatusButton(newValue);
        }
    }
    public void updateDeviceValue1(String id, boolean newValue){
        SmartDeviceController controller = itemControllers.get(smartDeviceService.getNameFromId1(id));
        if (controller != null) {
            controller.setStatusButton(newValue);
        }
    }
    public boolean handleActivationButtonClick(String id, String text) {
        return smartDeviceService.activation(id, text);
    }
    public void editPriority(String id, String string) {
        smartDeviceService.editPriority(id, string);
    }
}
