package cn.xiaolong.bluetoothconnectdemo;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.xiaolong.bluetoothconnectdemo.bluetooth.BtManager;
import cn.xiaolong.bluetoothconnectdemo.bluetooth.callback.IConnectStateCallBack;
import cn.xiaolong.bluetoothconnectdemo.bluetooth.callback.IScanCallback;

public class TestActivity extends AppCompatActivity {

    private static final int PERMISSION_LOCATION = 102;
    private TextView tvConnectState;
    private TextView tvTransferState;
    private Button btnSendFile;
    private Button btnScan;
    private RecyclerView rvState;
    private BtManager btManager;
    private List<BluetoothDevice> bondDeviceList;
    private DataAdapter dataAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        init();
        setListener();
        setData();
    }

    private void init() {
        rvState = (RecyclerView) findViewById(R.id.rvState);
        tvConnectState = (TextView) findViewById(R.id.tvConnectState);
        tvTransferState = (TextView) findViewById(R.id.tvTransferState);
        btnSendFile = (Button) findViewById(R.id.btnSendFile);
        btnScan = (Button) findViewById(R.id.btnScan);
        btManager = BtManager.getInstance(this);
    }

    private void setListener() {

        btManager.setIScanCallback(new IScanCallback<BluetoothDevice>() {
            @Override
            public void discoverDevice(BluetoothDevice bluetoothDevice) {
                bondDeviceList.add(bluetoothDevice);
                dataAdapter.notifyDataSetChanged();
            }

            @Override
            public void scanTimeout() {
                Toast.makeText(TestActivity.this, "扫描超时，请重试！", Toast.LENGTH_LONG).show();
            }

            @Override
            public void scanFinish(List<BluetoothDevice> bluetoothDevices) {
                Toast.makeText(TestActivity.this, "扫描完成！", Toast.LENGTH_LONG).show();
            }
        });
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionAndScan();
            }
        });
        btManager.setConnectStateCallback(new IConnectStateCallBack() {
            @Override
            public void connecting() {
                Toast.makeText(TestActivity.this, "正在连接", Toast.LENGTH_LONG).show();
            }

            @Override
            public void connected() {
                Toast.makeText(TestActivity.this, "已连接", Toast.LENGTH_LONG).show();
            }

            @Override
            public void disConnect() {
                Toast.makeText(TestActivity.this, "连接中断", Toast.LENGTH_LONG).show();
            }

            @Override
            public void waitForConnect() {
                Toast.makeText(TestActivity.this, "等待接入", Toast.LENGTH_LONG).show();
            }

            @Override
            public void sendDataSuccess(byte[] data) {
                Toast.makeText(TestActivity.this, "发送消息成功", Toast.LENGTH_LONG).show();
            }

            @Override
            public void connectedToDeviceName(String deviceName) {
                Toast.makeText(TestActivity.this, "连接到设备：" + deviceName, Toast.LENGTH_LONG).show();
            }

            @Override
            public void receiveData(File file) {
                Toast.makeText(TestActivity.this, "收到文件：" + file.getPath(), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onReceiving(int current, int all) {
                Toast.makeText(TestActivity.this, "文件接手中", Toast.LENGTH_LONG).show();
            }
        });
        btManager.setPath(getExternalCacheDir());
    }

    private void checkPermissionAndScan() {
        //权限相关
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_LOCATION);
            } else {
                scan();
            }
        } else {
            scan();
        }
    }

    private void scan() {
        bondDeviceList.clear();
        for (BluetoothDevice bluetoothDevice : btManager.getBondBlueToothList()) {
            bondDeviceList.add(bluetoothDevice);
        }
        dataAdapter.notifyDataSetChanged();
        btManager.waitForScan();
    }

    private void setData() {
        bondDeviceList = new ArrayList();
        dataAdapter = new DataAdapter(this, bondDeviceList);
        dataAdapter.setOnItemCLickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btManager.connectToDevice((BluetoothDevice) v.getTag());
            }
        });
        rvState.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvState.setAdapter(dataAdapter);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    scan();
                } else {
                    Toast.makeText(this, "您拒绝了权限申请，将无法正常使用蓝牙", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        btManager.disconnected();
        btManager = null;
        super.onDestroy();
    }
}
