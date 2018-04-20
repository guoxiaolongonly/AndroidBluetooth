package com.standard.bluetoothdemo;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.standard.bluetoothdemo.adapter.BluetoothAdapter;
import com.standard.bluetoothmodule.BtManager;
import com.standard.bluetoothmodule.callback.IConnectStateCallBack;
import com.standard.bluetoothmodule.callback.IScanCallback;

import java.util.List;

/**
 * @author xiaolong 719243738@qq.com
 * @version v1.0
 * @function <描述功能>
 * @date: 2018/4/20 16:55
 */

public class BlueToothActivity extends AppCompatActivity {
    private BtManager btManager;
    private RecyclerView rvBluetoothList;
    private TextView tvTitle;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice currentConnectDevice;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        initBluetooth();
        initView();
        setListener();
    }


    private void initView() {
        tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("基佬蓝牙好友列表");
        rvBluetoothList = findViewById(R.id.rvBluetoothList);
        mBluetoothAdapter = new BluetoothAdapter(this);
        rvBluetoothList.setAdapter(mBluetoothAdapter);
        rvBluetoothList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void initBluetooth() {
        BtManager.init(this);
        btManager = BtManager.getInstance();
    }

    private void setListener() {
        btManager.setIScanCallback(new IScanCallback() {
            @Override
            public void discoverDevice(BluetoothDevice bluetoothDevice, short rssi) {
                mBluetoothAdapter.addItem(bluetoothDevice);
            }

            @Override
            public void scanTimeout() {
                Toast.makeText(BlueToothActivity.this, "扫描超时！", Toast.LENGTH_LONG).show();
            }

            @Override
            public void scanFinish(List<BluetoothDevice> bluetoothList) {

            }
        });
        btManager.setConnectStateCallback(new IConnectStateCallBack() {
            @Override
            public void connecting() {

            }

            @Override
            public void connected() {

            }

            @Override
            public void disConnect() {

            }

            @Override
            public void waitForConnect() {

            }

            @Override
            public void connectedToDeviceName(BluetoothDevice device) {
                currentConnectDevice = device;
                launchActivity(device);
            }
        });
        btManager.scanBluetooth();
        mBluetoothAdapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BluetoothDevice bluetoothDevice = (BluetoothDevice) v.getTag();
                if (currentConnectDevice != null && currentConnectDevice.getAddress().equals(bluetoothDevice)) {
                    launchActivity(bluetoothDevice);
                } else {
                    btManager.connectToDevice(bluetoothDevice);
                }
            }
        });
    }

    private void launchActivity(BluetoothDevice bluetoothDevice) {
        Intent intent = new Intent();
        intent.putExtra("deviceName", bluetoothDevice.getName());
        intent.setClass(BlueToothActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        btManager.disconnected();
    }
}
