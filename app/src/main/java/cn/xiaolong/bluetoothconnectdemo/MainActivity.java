package cn.xiaolong.bluetoothconnectdemo;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import cn.xiaolong.bluetoothconnectdemo.bluetooth.BtManager;
import cn.xiaolong.bluetoothconnectdemo.bluetooth.Constants;
import cn.xiaolong.bluetoothconnectdemo.bluetooth.callback.IConnectStateCallBack;
import cn.xiaolong.bluetoothconnectdemo.bluetooth.callback.IScanCallback;
import cn.xiaolong.bluetoothconnectdemo.bluetooth.util.ByteUtil;

public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_LOCATION = 102;
    private BtManager btManager;
    private Button btnMessageSend;
    private TextView tvLucky;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvLucky = (TextView) findViewById(R.id.tvLucky);
        setUpBtManager();
        btnMessageSend = (Button) findViewById(R.id.btnMessageSend);
        btnMessageSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    File path = ByteUtil.fileFromAsset(MainActivity.this, "sample.pdf");
                    btManager.sendFile(path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void setUpBtManager() {
        btManager = BtManager.getInstance(this);
        btManager.setConnectStateCallback(new IConnectStateCallBack() {
            @Override
            public void connecting() {
                tvLucky.append("正在连接\n");
            }

            @Override
            public void connected() {
                tvLucky.append("已连接\n");
            }

            @Override
            public void disConnect() {
                tvLucky.append("连接断开\n");
            }

            @Override
            public void waitForConnect() {
                tvLucky.append("等待连接\n");
            }

            @Override
            public void sendDataSuccess(byte[] data) {
                tvLucky.append("发送文件：" + new String(Arrays.copyOfRange(data, 0, 25)).replace("/", "") + "文件大小：" + new String(Arrays.copyOfRange(data, 25, 35)));
            }

            @Override
            public void connectedToDeviceName(String deviceName) {
                tvLucky.append("连接到设备：" + deviceName + "\n");
            }

            @Override
            public void receiveData(File file) {
                Toast.makeText(MainActivity.this, "文件已存放到：" + file.getPath(), Toast.LENGTH_LONG).show();
                Logger.d(file.getPath());
            }

            @Override
            public void onReceiving(int current, int all) {

            }
        });
        btManager.setPath(getExternalCacheDir());
        btManager.setIScanCallback(new IScanCallback<BluetoothDevice>() {
            @Override
            public void discoverDevice(BluetoothDevice bluetoothDevice) {
                if (Constants.DEVICE_NAME.equals(bluetoothDevice.getName())) {
                    btManager.connectToDevice(bluetoothDevice);
                }
            }

            @Override
            public void scanTimeout() {

            }

            @Override
            public void scanFinish(List<BluetoothDevice> list) {

            }
        });
        btManager.setDeviceName(Constants.DEVICE_NAME);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                        PERMISSION_LOCATION);
            } else {
                btManager.waitForScan();
            }
        } else {
            btManager.waitForScan();
        }
    }


    void disconnectBle() {
        btManager.disconnected();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    btManager.waitForScan();
                } else {
                    Toast.makeText(this, "您拒绝了权限申请，将无法正常使用蓝牙", Toast.LENGTH_LONG).show();
                }
            }
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnectBle();
    }
}
