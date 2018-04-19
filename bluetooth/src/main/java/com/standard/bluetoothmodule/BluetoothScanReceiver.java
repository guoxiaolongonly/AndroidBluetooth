package com.standard.bluetoothmodule;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.ArrayMap;

import com.standard.bluetoothmodule.callback.IScanCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * @author xiaolong
 * @version v1.0
 * @function <描述功能>
 * @date: 2017/10/10 17:05
 */

public class BluetoothScanReceiver extends BroadcastReceiver {
    private boolean searching = false;
    private IScanCallback mScanCallBack;
    private HashMap<String, BluetoothDevice> mDeviceMap = new HashMap<>();

    public BluetoothScanReceiver(IScanCallback scanCallback) {
        mScanCallBack = scanCallback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (mScanCallBack == null) {
            return;
        }
        if (intent.getAction().equals(BluetoothDevice.ACTION_FOUND)) {
            searching = true;
            //扫描到蓝牙设备
            BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (bluetoothDevice == null) {
                return;
            }
            if (!mDeviceMap.containsKey(bluetoothDevice.getAddress())) {
                mDeviceMap.put(bluetoothDevice.getAddress(), bluetoothDevice);
            }
            mScanCallBack.discoverDevice(bluetoothDevice, intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI));
        } else if (intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
            //扫描设备结束
            final List<BluetoothDevice> deviceList = new ArrayList<>(mDeviceMap.values());
            if (deviceList != null && deviceList.size() > 0) {
                if (searching) {
                    mScanCallBack.scanFinish(deviceList);
                    searching = false;
                }
            } else {
                mScanCallBack.scanTimeout();
            }

        }
    }
}
