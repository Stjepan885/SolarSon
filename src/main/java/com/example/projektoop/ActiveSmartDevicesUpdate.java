package com.example.projektoop;

import com.example.projektoop.device.SmartDevice;

import java.util.List;

public interface ActiveSmartDevicesUpdate {
    void onActiveSmartDevicesUpdate(List<SmartDevice> activeDevices, SmartDevice newDevice);
}
