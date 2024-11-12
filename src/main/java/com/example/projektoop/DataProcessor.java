package com.example.projektoop;

import com.example.projektoop.database.CsvFileChecker;
import com.example.projektoop.database.CsvWriter;
import com.example.projektoop.device.SmartDevice;
import com.example.projektoop.models.InverterModel;
import com.example.projektoop.preferences.SettingsManager;

import java.util.*;
import java.util.function.Consumer;

public class DataProcessor implements ActiveSmartDevicesUpdate {
    private final String pathCsv = "device_data.csv";

    private boolean day = true;
    private int nightCounter = 2;
    private int saveInterval;

    private int saveDayInterval;
    private int saveNightInterval;
    private int chartUpdateInterval;

    private List<InverterModel> inverterModelListSave;
    private List<InverterModel> inverterModelListChartUpdate;

    private long saveTime;
    private long chartUpdateTime;
    private final int scanInterval = 5;
    private boolean saveBool = false;
    private boolean chartUdateBool = false;

    private List<SmartDevice> activeDevices = new ArrayList<>();
    private List<SmartDevice> readyDevices = new ArrayList<>();

    private SmartDeviceService smartDeviceService;

    private ReadySmartDeviceList readySmartDeviceList = new ReadySmartDeviceList();
    private ReadySmartDeviceList activeSmartDeviceList = new ReadySmartDeviceList();

    private double lastMaxPower = 0.0;
    private long lastMaxPowerTime = 0;

    private boolean recentlyOff = false;
    private boolean recentlyOn = false;

    private int offTime;
    private int onTime;
    private int chargingDifference;
    private int maxPower;
    private int maxCurrent;




    public DataProcessor() {
        setIntervals();
        inverterModelListSave = new ArrayList<>();
        inverterModelListChartUpdate = new ArrayList<>();
        saveInterval = saveDayInterval;
        saveTime = System.currentTimeMillis() + saveInterval;
        chartUpdateTime = System.currentTimeMillis() + chartUpdateInterval;

        smartDeviceService = SmartDeviceService.getInstance();
        smartDeviceService.addListener(this);

        readyDevices = smartDeviceService.getReadyDevices();
        refreshDevicePriority(readyDevices);

        getPreferences();

        String preferenceValue = SettingsManager.getUserPreference("off_time", "18");
        offTime = Integer.parseInt(preferenceValue);
        preferenceValue = SettingsManager.getUserPreference("on_time", "9");
        onTime = Integer.parseInt(preferenceValue);
    }

    private void getPreferences() {
        String preferenceValue = SettingsManager.getUserPreference("off_time", "18");
        offTime = Integer.parseInt(preferenceValue);
        preferenceValue = SettingsManager.getUserPreference("on_time", "9");
        onTime = Integer.parseInt(preferenceValue);
        preferenceValue = SettingsManager.getUserPreference("charging_difference", "100");
        chargingDifference = Integer.parseInt(preferenceValue);
        preferenceValue = SettingsManager.getUserPreference("max_power", "3000");
        maxPower = Integer.parseInt(preferenceValue);
        preferenceValue = SettingsManager.getUserPreference("max_current", "10");
        maxCurrent = Integer.parseInt(preferenceValue);

    }

    private void refreshDevicePriority(List<SmartDevice> readyDevices) {
        for (SmartDevice device : readyDevices) {
            readySmartDeviceList.add(device);
        }
    }

    public List<Boolean> updateData(InverterModel data) {
        long timeStamp = data.getTimestamp();

        inverterModelListSave.add(data);
        inverterModelListChartUpdate.add(data);

        if ((timeStamp + scanInterval) > chartUpdateTime) {
            chartUdateBool = true;

            if (day) {
                activateDevices(data);
            }
        }
        if ((timeStamp + scanInterval) > saveTime) {
            saveBool = true;

            if (data.getInputPower() < 5.0 && day) {
                nightCounter -= 1;
                if (nightCounter <= 0) {
                    saveInterval = saveNightInterval;
                    day = false;
                    nightCounter = 20;
                }
            } else if (!day) {
                if (data.getInputPower() > 10.0) {
                    day = true;
                    saveInterval = saveDayInterval;
                }}
        }

        List<Boolean> result = new ArrayList<>();
        result.add(saveBool);
        result.add(chartUdateBool);

        return result;
    }

    //device activation
    private void activateDevices(InverterModel data) {
        if (readySmartDeviceList.isEmpty() && activeSmartDeviceList.isEmpty()) {
            return;
        }

        //turn off ?
        if (data.getInputPower() < data.getOutputPower()) {
            //turn off delay: if battery current is greater than -10 A,turn off device. in case of battery discharge to high
            if (recentlyOff && data.getBatteryCurrent() > -maxCurrent) {
                recentlyOff = false;
                return;
            } else {
                recentlyOff = false;
            }
            if (recentlyOn){
                recentlyOn = false;
                return;
            }

            if (!activeSmartDeviceList.isEmpty()) {
                if (data.getBatteryCurrent() < -1 || data.getBatteryCapacity() < 100.0) {
                    automaticTurnOffDevice(data);
                    recentlyOff = true;
                }
            }
            //turn on
        } else {
            Date timeStampDate = new Date(data.getTimestamp());
            int hour = timeStampDate.getHours();
            if (hour >= offTime) {
                return;
            }
            //turn on delay to settle down the power
            if (recentlyOn){
                recentlyOn = false;
                return;
            }
            // if output power is greater than 2500 W (lower than max input power), do not turn on any device
            if (data.getOutputPower() > maxPower){
                return;
            }
            if (!readySmartDeviceList.isEmpty()) {
                if (data.getBatteryCurrent() > maxCurrent && data.getBatteryCapacity() < 100) {
                    automaticTurnOnDevice(data, true);
                    recentlyOn = true;
                } else if (data.getBatteryCapacity() == 100) {
                    automaticTurnOnDevice(data, false);
                    recentlyOn = true;
                }
            }
        }
    }
    private void automaticTurnOnDevice(InverterModel data, boolean charging) {

        if (readySmartDeviceList.isEmpty()) {
            return;
        }
        SmartDevice deviceToTurnOn = readySmartDeviceList.getFirst();

        if (charging) {
            for (SmartDevice device : readySmartDeviceList) {
                if (device.getConnectedPower() <  data.getInputPower() - data.getOutputPower() + chargingDifference) {
                    deviceToTurnOn = device;
                    break;
                }
            }
        } else {
            if (lastMaxPowerTime !=0 && lastMaxPowerTime + 60000 > data.getTimestamp()){
                return;
            }
            if (lastMaxPowerTime + 60000 < data.getTimestamp()) {
                for (SmartDevice device : readySmartDeviceList) {
                    if (device.getConnectedPower() + data.getOutputPower() - 300 < lastMaxPower) {
                        deviceToTurnOn = device;
                        break;
                    }
                }
            }
        }


        smartDeviceService.turnOn(deviceToTurnOn.getId());
        smartDeviceService.turnTextOn(deviceToTurnOn.getId(), true);
        activeSmartDeviceList.add(deviceToTurnOn);
        readySmartDeviceList.remove(deviceToTurnOn);


        System.out.println("Ready devices " + readySmartDeviceList.toString());
        System.out.println("Active devices " + activeSmartDeviceList.toString());
    }
    private void automaticTurnOffDevice(InverterModel data) {

        if (activeSmartDeviceList.isEmpty()) {
            System.out.println("lists empty active device");
            return;
        }
        SmartDevice deviceToOff = activeSmartDeviceList.getLast();


        for (int i = activeDevices.size() - 1; i >= 0; i--) {
            SmartDevice device = activeDevices.get(i);
            if (data.getOutputPower() - device.getConnectedPower() < data.getInputPower()) {
                deviceToOff = device;
                break;
            }
        }

        smartDeviceService.turnOn(deviceToOff.getId());
        smartDeviceService.turnTextOn(deviceToOff.getId(), false);
        activeSmartDeviceList.remove(deviceToOff);
        readySmartDeviceList.add(deviceToOff);

        System.out.println("Ready devices " + readySmartDeviceList.toString());
        System.out.println("Active devices " + activeSmartDeviceList.toString());


        lastMaxPower = data.getOutputPower();
        lastMaxPowerTime = data.getTimestamp();

    }

    public InverterModel updateChart() {
        InverterModel avgModel = InverterModel.calculateAverage(inverterModelListChartUpdate);
        inverterModelListChartUpdate.clear();
        chartUpdateTime += saveInterval;
        chartUdateBool = false;
        return avgModel;
    }

    public void saveData() {
        saveCsv(InverterModel.calculateAverage(inverterModelListSave));
        inverterModelListSave.clear();
        saveTime += saveInterval;
        saveBool = false;
    }

    private void setIntervals() {
        String preferenceValue = SettingsManager.getUserPreference("save", "15");
        saveDayInterval = Integer.parseInt(preferenceValue) * 1000;
        preferenceValue = SettingsManager.getUserPreference("save_night", "60");
        saveNightInterval = Integer.parseInt(preferenceValue) * 1000;
        preferenceValue = SettingsManager.getUserPreference("chart_update", "15");
        chartUpdateInterval = Integer.parseInt(preferenceValue) * 1000;
    }

    private void saveCsv(InverterModel data) {
        CsvWriter.writeToCsv(pathCsv, data, CsvFileChecker.doesFileExist(pathCsv));
    }

    @Override
    public void onActiveSmartDevicesUpdate(List<SmartDevice> readyDevices, SmartDevice newDevice) {
        SmartDevice deviceToDelete = null;

        for (SmartDevice device : readySmartDeviceList) {
            if (device.getId().equals(newDevice.getId())) {
                deviceToDelete = device;
            }
        }
        if (deviceToDelete != null) {
            readySmartDeviceList.remove(deviceToDelete);
            return;
        }
        for (SmartDevice device : activeSmartDeviceList) {
            if (device.getId().equals(newDevice.getId())) {
                smartDeviceService.turnOn(device.getId());
                smartDeviceService.turnTextOn(device.getId(), false);
                deviceToDelete = device;
            }
        }
        if (deviceToDelete != null) {
            activeSmartDeviceList.remove(deviceToDelete);
            return;
        }

        readySmartDeviceList.add(newDevice);

    }

    public List<SmartDevice> getSmartDevices() {
        return activeSmartDeviceList;
    }
}


