package com.example.projektoop;

import com.example.projektoop.device.SmartDevice;

import java.util.*;

public class ReadySmartDeviceList extends ArrayList<SmartDevice> {
    @Override
    public boolean add(SmartDevice smartDevice) {
        boolean added = super.add(smartDevice);
        this.sortByPriorityAndPower();
        return added;
    }


    @Override
    public boolean addAll(Collection<? extends SmartDevice> c) {
        boolean added = super.addAll(c);
        this.sortByPriorityAndPower();
        return added;
    }

    // Sort the list by priorityValue, then by connectedPower
    private void sortByPriorityAndPower() {
        Collections.sort(this, Comparator
                .comparingInt(SmartDevice::getPriorityValue) // Primary sort by priorityValue
                .thenComparingDouble(SmartDevice::getConnectedPower)); // Secondary sort by connectedPower
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (SmartDevice smartDevice : this) {
            sb.append(smartDevice.getName()).append(" ").append(smartDevice.getPriorityValue()).append(" ").append(smartDevice.getConnectedPower()).append("\n");
        }
        return sb.toString();
    }
}
