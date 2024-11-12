package com.example.projektoop;

import com.example.projektoop.dao.InverterTableDAO;
import com.example.projektoop.device.SmartDevice;
import com.example.projektoop.preferences.SettingsManager;
import com.github.realzimboguy.ewelink.api.EweLink;
import com.github.realzimboguy.ewelink.api.model.home.OutletSwitch;
import com.github.realzimboguy.ewelink.api.model.home.Thing;
import com.github.realzimboguy.ewelink.api.wss.WssResponse;
import com.github.realzimboguy.ewelink.api.wss.wssrsp.WssRspMsg;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class SmartDeviceService {
    public static  String PASSWORD = "";
    private static String email = "";
    private EweLink eweLink;
    private List<SmartDevice> smartDevicesList;
    private List<Thing> things;
    private List<SmartDeviceUpdateListener> listeners = new ArrayList<>();
    private List<ActiveSmartDevicesUpdate> activeListeners = new ArrayList<>();
    private List<String> names = new ArrayList<>();
    private static SmartDeviceService instance;
    private List<SmartDevice> activeDevices = new ArrayList<>();

    private SmartDeviceService() {
        email = SettingsManager.getUserPreference("email", "");
        PASSWORD = SettingsManager.getUserPreference("password", "");
        this.eweLink = new EweLink("eu", email, PASSWORD, "+385", 60);
        startService();
    }

    public static SmartDeviceService getInstance() {
        if (instance == null) {
            synchronized (SmartDeviceService.class) {
                if (instance == null) {
                    instance = new SmartDeviceService();
                }}}
        return instance;
    }

    public void addListener(SmartDeviceUpdateListener listener) {
        listeners.add(listener);
    }
    public void addListener(ActiveSmartDevicesUpdate listener) {
        activeListeners.add(listener);
    }
    private void notifyListeners(WssRspMsg rsp) {
        for (SmartDeviceUpdateListener listener : listeners) {
            listener.onDeviceUpdate(rsp);
        }
    }
    private void notifyListeners(String id, boolean deviceState) {
        for (SmartDeviceUpdateListener listener : listeners) {
            listener.onDeviceUpdate(id, deviceState);
        }
    }
    private void notifyActiveListeners(SmartDevice newDevice) {
        for (ActiveSmartDevicesUpdate listener : activeListeners) {
            listener.onActiveSmartDevicesUpdate(activeDevices, newDevice);
        }
    }

    private void startService() {
        InverterTableDAO dao = new InverterTableDAO();
        dao.createTable();
        try {
            smartDevicesList = dao.getAll();
        } catch (Exception e) {
            System.out.println("no switches in db");
            smartDevicesList = new ArrayList<>();
        }

        try {
            eweLink.login();
            things = eweLink.getThings();

            for (Thing thing : things) {
                names.add(thing.getItemData().getName());
            }

            if (!smartDevicesList.isEmpty()) {
                for (SmartDevice device : smartDevicesList) {
                    if (device.isDual()) {
                        String id1 = device.getId();
                        String id = id1.substring(0, id1.length() - 1);
                        int i = id1.charAt(id1.length()-1) - '0';

                        for (Thing thing : things) {
                            if (thing.getItemData().getDeviceid().equals(id)) {
                                device.setOnline(true);
                                //check if the device is on or off
                                if (thing.getItemData().getParams().getSwitches().get(i).get_switch().equals("on")) {
                                    device.setStatus(true);
                                } else {
                                    device.setStatus(false);
                                }
                                device.setPriorityValue(2);
                                device.setPriorityStatus(false);
                            }
                        }
                    } else {
                        for (Thing thing : things) {
                            if (thing.getItemData().getDeviceid().equals(device.getId())) {
                                device.setOnline(true);
                                //check if the device is on or off
                                if (thing.getItemData().getParams().getSwitch().equals("on")) {
                                    device.setStatus(true);
                                } else {
                                    device.setStatus(false);
                                }
                                device.setPriorityValue(2);
                                device.setPriorityStatus(false);
                            }}}
                }
            }

            eweLink.getWebSocket(new WssResponse() {
                @Override
                public void onMessage(String s) {
                    //System.out.println("on message in test raw:" + s);
                }

                @Override
                public void onMessageParsed(WssRspMsg rsp) {
                    if (rsp.getError() == null) {
                        updateDevice(rsp);
                        notifyListeners(rsp);
                    } else if (rsp.getError() == 0) {
                        System.out.println("login success");
                    } else if (rsp.getError() > 0) {
                        System.out.println("login error:" + rsp.toString());
                    }
                }

                private void updateDevice(WssRspMsg rsp) {
                    for (SmartDevice device : smartDevicesList) {
                        if (device.getId().equals(rsp.getDeviceid())) {
                            if (rsp.getParams().getSwitch() != null)
                                if (rsp.getParams().getSwitch().equals("on")) {
                                    device.setStatus(true);
                                } else {
                                    device.setStatus(false);
                                }}}
                }

                @Override
                public void onError(String s) {
                    System.out.println("onError in test, this should never be called");
                    System.out.println(s);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean activation(String id, String isActivated) {
        if (isActivated.equals("ON")) {
            SmartDevice sd = null;
            for (SmartDevice device : activeDevices) {
                if (device.getId().equals(id)) {
                    sd = device;
                    device.setPriorityStatus(false);
                }
            }
            activeDevices.remove(sd);
            notifyActiveListeners(getSmartDeviceById(id));
            return false;
        } else {
            SmartDevice sd = getSmartDeviceById(id);
            sd.setPriorityStatus(true);
            activeDevices.add(sd);
        }
        notifyActiveListeners(getSmartDeviceById(id));
        return true;
    }

    public void turnOn(String id) {
        if (!getSmartDeviceById(id).isDual()) {
            try {
                for (SmartDevice device : smartDevicesList) {
                    if (device.getId().equals(id)) {
                        if (device.isStatus()) {
                            System.out.println("turning off id: " + id);
                            System.out.println(eweLink.setDeviceStatus(id, "off"));
                            device.setStatus(false);
                        } else {
                            System.out.println(eweLink.setDeviceStatus(id, "on"));
                            device.setStatus(true);
                        }}}
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String id1 = id.substring(0, id.length() - 1);
            char id2 = id.charAt(id.length()-1);
            int i = id2 - '0';

            List<OutletSwitch> outletSwitches = new ArrayList<>();
            for (Thing thing : things) {
                if (thing.getItemData().getDeviceid().equals(id1)) {
                    outletSwitches = thing.getItemData().getParams().getSwitches();
                }
            }

            for (OutletSwitch outletSwitch : outletSwitches) {
                if (outletSwitch.getOutlet() == i) {
                    if (outletSwitch.get_switch().equals("on")) {
                        outletSwitch.set_switch("off");
                    } else {
                        outletSwitch.set_switch("on");
                    }
                }
            }

            try {
                System.out.println(eweLink.setMultiDeviceStatus(id1, outletSwitches));
                //eweLink.closeConn();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void turnTextOn(String id, boolean b) {
        notifyListeners(id, b);
    }


    public void addToDb(SmartDevice sd) {
        InverterTableDAO dao = new InverterTableDAO();
        dao.insert(sd);
        smartDevicesList.add(sd);
        setDeviceStatus(sd);
    }
    public void setDeviceStatus(SmartDevice device) {
        for (Thing thing : things) {
            if (thing.getItemData().getDeviceid().equals(device.getId())) {
                device.setOnline(true);
                //check if the device is on or off
                if (thing.getItemData().getParams().getSwitch().equals("on")) {
                    device.setStatus(true);
                } else {
                    device.setStatus(false);
                }
            }
            //System.out.println(thing.getItemData().getParams().getSwitch() + " " + thing.getItemData().getName() + " " + thing.getItemData().getDeviceid() + " ");
        }
    }
    public void editPriority(String id, String string) {
        int priority = 0;
        SmartDevice deviceToEdit = null;
        for (SmartDevice device : smartDevicesList) {
            if (device.getId().equals(id)) {
                deviceToEdit = device;
                switch (string) {
                    case "HIGH":
                        device.setPriorityValue(1);
                        priority = 1;
                        break;
                    case "MODERATE":
                        device.setPriorityValue(2);
                        priority = 2;
                        break;
                    case "LOW":
                        priority = 3;
                        device.setPriorityValue(3);
                        break;
                }
            }
        }

        if (deviceToEdit != null) {
            System.out.println("id: " + id + " priority: " + priority);
            InverterTableDAO dao = new InverterTableDAO();
            dao.changePriorityValue(id, priority);
            deviceToEdit.setPriorityValue(priority);
        }
    }
    public void editDevice(String s, double maxPower, double connectedPower) {
        for (SmartDevice device : smartDevicesList) {
            if (device.getId().equals(s)) {
                device.setMaxPower(maxPower);
                device.setConnectedPower(connectedPower);
            }
        }
        InverterTableDAO dao = new InverterTableDAO();
        dao.changeMaxPower(s, maxPower);
        dao.changeConnectedPower(s, connectedPower);
    }

    //get device
    public List<SmartDevice> getReadyDevices() {
        return activeDevices;
    }
    public List<SmartDevice> getSmartDevices() {
        return smartDevicesList;
    }
    public SmartDevice getSmartDevice(String name) {
        for (SmartDevice device : smartDevicesList) {
            if (name.equals(device.getName())) {
                return device;
            }
        }
        return null;
    }
    public SmartDevice getSmartDeviceById(String id) {
        for (SmartDevice device : smartDevicesList) {
            if (device.getId().equals(id)) {
                return device;
            }
        }
        return null;
    }

    //get name
    public List<String> getNameList() {
        return names;
    }
    public String getNameFromId(String id) {
        for (Thing thing : things) {
            if (thing.getItemData().getDeviceid().equals(id)) {
                return thing.getItemData().getName();
            }
        }
        return null;
    }
    public String getNameFromId1(String id) {
        for (SmartDevice device : smartDevicesList) {
            if (device.getId().equals(id)) {
                return device.getName();
            }
        }
        return null;
    }

    //get id
    public String getIdOfThings(String name) {
        for (Thing thing : things) {
            if (Objects.equals(thing.getItemData().getName().toString(), name.toString())) {
                return thing.getItemData().getDeviceid();
            }
        }
        return null;
    }

    //checkers
    public boolean checkIfSavedDeviceId(String thingId) {
        for (SmartDevice device : smartDevicesList) {
            if (thingId.equals(device.getId())) {
                return true;
            }
        }
        return false;
    }
    public boolean checkIfSavedDeviceName(String deviceName) {
        for (SmartDevice device : smartDevicesList) {
            if (deviceName.equals(device.getName())) {
                return true;
            }
        }
        return false;
    }
    public boolean chcekIfIsDual(String string) {
        for (Thing thing : things) {
            if (thing.getItemData().getName().equals(string)) {
                if (thing.getItemData().getParams().getSwitches() != null) {
                    return true;
                }
                return false;
            }
        }
        return false;
    }
}
