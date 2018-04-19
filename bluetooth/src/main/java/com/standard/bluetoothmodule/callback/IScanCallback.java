package com.standard.bluetoothmodule.callback;

import android.bluetooth.BluetoothDevice;

import java.util.List;

public interface IScanCallback {
    void discoverDevice(BluetoothDevice bluetoothDevice, short rssi);
    void scanTimeout();
    void scanFinish(List<BluetoothDevice> bluetoothList);
}
