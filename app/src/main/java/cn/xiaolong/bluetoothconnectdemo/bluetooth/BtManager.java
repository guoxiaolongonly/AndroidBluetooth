package cn.xiaolong.bluetoothconnectdemo.bluetooth;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;


import com.orhanobut.logger.Logger;

import java.io.File;
import java.util.List;
import java.util.Set;

import cn.xiaolong.bluetoothconnectdemo.R;
import cn.xiaolong.bluetoothconnectdemo.bluetooth.callback.IConnectStateCallBack;
import cn.xiaolong.bluetoothconnectdemo.bluetooth.callback.IScanCallback;

import static cn.xiaolong.bluetoothconnectdemo.bluetooth.Constants.DEVICE_NAME;

public class BtManager implements IScanCallback<BluetoothDevice> {
    private File cacheDir;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final String TAG = BtManager.class.getSimpleName();
    private BluetoothAdapter mBtAdapter;
    private Activity activity;
    private BluetoothTransferService mService;
    private BluetoothReceiver mBleReceiver;
    private BluetoothScanReceiver mBluetoothScanReceiver;
    private IScanCallback<BluetoothDevice> mIScanCallback;
    private static BtManager instance = null;
    private DeviceHandler mDeviceHandler;

    public static BtManager getInstance(Activity activity) {
        if (instance == null) { // 1
            synchronized (BtManager.class) {
                if (instance == null) {
                    instance = new BtManager(activity); // 2
                }
            }
        }
        return instance;
    }

    /**
     * 初始化，判断蓝牙状态
     *
     * @param activity
     */
    private BtManager(Activity activity) {
        this.activity = activity;
        registerReceiver(activity);
        BluetoothManager btManager = (BluetoothManager) activity
                .getSystemService(Context.BLUETOOTH_SERVICE);
        mBtAdapter = btManager.getAdapter();
        if (mBtAdapter == null) {
            Toast.makeText(activity, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            return;
        }
    }
    /**
     * 注册蓝牙扫描广播接收器
     */
    private void registerReceiver(Activity activity) {
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        mBleReceiver = new BluetoothReceiver();
        activity.registerReceiver(mBleReceiver, filter);
        mBluetoothScanReceiver = new BluetoothScanReceiver(this);
        //注册蓝牙扫描监听器
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        activity.registerReceiver(mBluetoothScanReceiver, intentFilter);
    }


    public void setConnectStateCallback(IConnectStateCallBack iConnectStateCallBack)
    {
        mDeviceHandler = new DeviceHandler(iConnectStateCallBack);
    }

    public void setDeviceName(String deviceName) {
        if (!mBtAdapter.getName().equals(deviceName)) {
            mBtAdapter.setName(deviceName);
        }
    }

    public void setIScanCallback(IScanCallback<BluetoothDevice> iScanCallback) {
        this.mIScanCallback = iScanCallback;
    }

    /**
     * 开始扫描
     */
    public void waitForScan() {
        if (!mBtAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        mBtAdapter.startDiscovery();
    }

    public void disconnected() {
        if (mService != null) {
            mService.stop();
        }
        instance = null;
        destroyReceiver();
    }

    @Override
    public void discoverDevice(BluetoothDevice bluetoothDevice) {
        if (mIScanCallback != null) {
            mIScanCallback.discoverDevice(bluetoothDevice);
        }
    }

    /**
     * 获取已绑定设备列表
     *
     * @return
     */
    public Set<BluetoothDevice> getBondBlueToothList() {
        if (mBtAdapter == null) {
            Logger.d("mBtAdapter未初始化");
            return null;
        }
        return mBtAdapter.getBondedDevices();
    }

    /**
     * 列表的方法开放了，连接的方法也开放给用户实现。
     *
     * @param bluetoothDevice
     */
    public void connectToDevice(BluetoothDevice bluetoothDevice) {
        if (cacheDir == null) {
            throw new RuntimeException("需先配置一个缓存路径,参见：BtManager.setPath()");
        }
        //发现设备
        mService = new BluetoothTransferService(mDeviceHandler, cacheDir);
        mService.connect(bluetoothDevice, true);
    }

    public void setPath(File externalCacheDir) {
        cacheDir = externalCacheDir;
    }

    public class DeviceHandler extends Handler {
        private IConnectStateCallBack mIConnectStateCallBack;

        public DeviceHandler(IConnectStateCallBack iConnectStateCallBack) {
            this.mIConnectStateCallBack = iConnectStateCallBack;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothTransferService.STATE_CONNECTED:
                            Logger.d("已连接上");
                            mIConnectStateCallBack.connected();
                            break;
                        case BluetoothTransferService.STATE_CONNECTING:
                            Logger.d("连接中");
                            mIConnectStateCallBack.connecting();
                            break;
                        case BluetoothTransferService.STATE_LISTEN:
                            Logger.d("等待连接");
                            mIConnectStateCallBack.waitForConnect();
                            break;
                        case BluetoothTransferService.STATE_NONE:
                            Logger.d("未连接");
                            mIConnectStateCallBack.disConnect();
                            mService.start();//在连接失败后重新等待连接
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    Logger.d("sendMessage");
                    mIConnectStateCallBack.sendDataSuccess((byte[]) msg.obj);
                    break;
                case Constants.MESSAGE_READ:
                    Logger.d("receiveMessage");
                    mIConnectStateCallBack.receiveData((File) msg.obj);
                    break;
                case Constants.MESSAGE_READING:
//                    Logger.d("receiveing" + "总长：" + msg.arg2 + "___已传输：" + msg.arg1);
                    mIConnectStateCallBack.onReceiving(msg.arg1, msg.arg2);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    mIConnectStateCallBack.connectedToDeviceName(msg.getData().getString(DEVICE_NAME));
                    break;
                case Constants.MESSAGE_TOAST:
                    Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }

//    /**
//     * Sends a message.
//     *
//     * @param message A string of text to send.
//     */
//    public void sendMessage(String message) {
//        // Check that we're actually connected before trying anything
//        if (mService == null || mService.getState() != BluetoothChatService.STATE_CONNECTED) {
//            Logger.d("蓝牙未链接！");
//            return;
//        }
//
//        // Check that there's actually something to send
//        if (message.length() > 0) {
//            // Get the message bytes and tell the BluetoothChatService to write
//
//            byte[] send = message.getBytes();
//            byte[] result = ByteUtil.concat(new byte[]{Constants.TYPE_TEXT}, send);
//            mService.write(result);
//        }
//    }

    public void sendFile(File file) {
        if (mService == null || mService.getState() != BluetoothTransferService.STATE_CONNECTED) {
            Logger.d("蓝牙未链接！");
            return;
        }

        mService.write(file);
    }

    @Override
    public void scanTimeout() {
        if (mIScanCallback != null) {
            mIScanCallback.scanTimeout();
        }
    }

    @Override
    public void scanFinish(List<BluetoothDevice> bluetoothDevices) {
        //执行到这里判断服务是否已经启动了。未启动说明设备列表中不存在该设备
        if (mService == null) {
            mService = new BluetoothTransferService(mDeviceHandler, cacheDir);
            mService.start();
        }
        mIScanCallback.scanFinish(bluetoothDevices);
    }


    /**
     * 蓝牙状态接收器
     */
    private class BluetoothReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_OFF) {
                    Logger.e("检测到蓝牙关闭");
                } else if (state == BluetoothAdapter.STATE_ON) {
                    Logger.e("检测到蓝牙打开");
                    mBtAdapter = ((BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE)).getAdapter();
//                    //保证蓝牙已打开，否则会报 android.os.DeadObjectException
                    waitForScan();
                }
            }
        }


    }

    /**
     * 解除广播接收器
     */
    private void destroyReceiver() {
        if (mBleReceiver != null) {
            activity.unregisterReceiver(mBleReceiver);
            activity.unregisterReceiver(mBluetoothScanReceiver);
        }
    }


}

