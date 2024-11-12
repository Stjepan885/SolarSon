package com.example.projektoop;

import com.github.realzimboguy.ewelink.api.wss.wssrsp.WssRspMsg;

public interface SmartDeviceUpdateListener {
    void onDeviceUpdate(WssRspMsg rsp);

    void onDeviceUpdate(String id, boolean deviceState);
}
