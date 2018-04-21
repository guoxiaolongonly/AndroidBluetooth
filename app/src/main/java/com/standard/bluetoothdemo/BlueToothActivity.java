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
import com.standard.bluetoothdemo.widget.ExpandableLayout;
import com.standard.bluetoothmodule.BtManager;
import com.standard.bluetoothmodule.callback.IConnectStateCallBack;
import com.standard.bluetoothmodule.callback.IScanCallback;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author xiaolong 719243738@qq.com
 * @version v1.0
 * @function <描述功能>
 * @date: 2018/4/20 16:55
 */

public class BlueToothActivity extends AppCompatActivity {
    private BtManager btManager;
    private TextView tvTitle;
    private BluetoothAdapter mSurroundBluetoothAdapter;
    private BluetoothAdapter mBondBluetoothAdapter;
    private BluetoothDevice currentConnectDevice;
    private ExpandableLayout exlBond;
    private ExpandableLayout exlSurround;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        initView();
        initBluetooth();
        setListener();
    }


    private void initView() {
        tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText("蓝牙列表");
        exlBond = findViewById(R.id.exlBond);
        exlSurround = findViewById(R.id.exlSurround);
        initList();
    }

    private void initList() {
        RecyclerView rvSurroundBluthoothList = exlSurround.findViewById(R.id.rvContent);
        TextView surroundTitle = exlSurround.getHeaderLayout().findViewById(R.id.tvHeader);
        surroundTitle.setText("附近蓝牙列表（选择连接）");
        mSurroundBluetoothAdapter = new BluetoothAdapter(this);
        rvSurroundBluthoothList.setAdapter(mSurroundBluetoothAdapter);
        rvSurroundBluthoothList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        RecyclerView rvBondBluetoothList = exlBond.findViewById(R.id.rvContent);
        TextView bondTitle = exlBond.getHeaderLayout().findViewById(R.id.tvHeader);
        bondTitle.setText("已连接过蓝牙列表");
        mBondBluetoothAdapter = new BluetoothAdapter(this);
        rvBondBluetoothList.setAdapter(mBondBluetoothAdapter);
        rvBondBluetoothList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

    private void initBluetooth() {
        BtManager.init(this);
        btManager = BtManager.getInstance();
    }

    private void setListener() {
        btManager.setIScanCallback(new IScanCallback() {
            @Override
            public void discoverDevice(BluetoothDevice bluetoothDevice, short rssi) {
                mSurroundBluetoothAdapter.addItem(bluetoothDevice);
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
                currentConnectDevice = null;
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
        scan();
        mSurroundBluetoothAdapter.setOnClickListener(new View.OnClickListener() {
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
        findViewById(R.id.tvRefresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scan();
            }
        });
    }

    private void scan() {
        mSurroundBluetoothAdapter.clearData();
        btManager.scanBluetooth();
        List<BluetoothDevice> bluetoothDevices = new ArrayList<>();
        Iterator<BluetoothDevice> iterator = btManager.getBondBlueToothList().iterator();
        while (iterator.hasNext()) {
            bluetoothDevices.add(iterator.next());
        }
        mBondBluetoothAdapter.addAll(bluetoothDevices);
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
