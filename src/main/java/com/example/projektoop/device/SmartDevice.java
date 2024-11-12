package com.example.projektoop.device;

import com.github.realzimboguy.ewelink.api.model.home.OutletSwitch;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class SmartDevice extends Device {

    private boolean isDual;
    private double maxPower;
    private double connectedPower;
    private boolean status;
    private boolean priorityStatus;
    private int priorityValue;
    private int isActive;

    public SmartDevice(String name, String id, boolean isDual, double maxPower, double connectedPower, int priorityValue, int isActive) {
        super(name, id);
        this.isDual = isDual;
        this.maxPower = maxPower;
        this.connectedPower = connectedPower;
        this.priorityValue = priorityValue;
        this.priorityStatus = false;
        this.isActive = isActive;
    }
    private boolean isOnline = false;
    public boolean isDual() {
        return isDual;
    }
    public boolean isOnline() {
        return isOnline;
    }
    public void setOnline(boolean online) {
        isOnline = online;
    }
    public int getPriorityValue() {
        return priorityValue;
    }
    public double getMaxPower() {
        return maxPower;
    }
    public double getConnectedPower() {
        return connectedPower;
    }
    public boolean isStatus() {
        return status;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setPriorityValue(int priorityValue) {
        this.priorityValue = priorityValue;
    }

    public void setPriorityStatus(boolean priorityStatus) {
        this.priorityStatus = priorityStatus;
    }

    public void setMaxPower(double maxPower) {
        this.maxPower = maxPower;
    }

    public void setConnectedPower(double connectedPower) {
        this.connectedPower = connectedPower;
    }

    @Override
    public String toString() {
        return "SmartDevice{" +
                "maxPower=" + maxPower +
                ", connectedPower=" + connectedPower +
                ", status=" + status +
                ", isDual=" + isDual +
                ", connectedPower=" + connectedPower + "  dsa" + super.toString() +
                '}';
    }
}
