package com.standard.bluetoothmodule;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.standard.bluetoothmodule.callback.IConnectStateCallBack;
import com.standard.bluetoothmodule.callback.IDataTransferCallBack;
import com.standard.bluetoothmodule.callback.IScanCallback;
import com.standard.bluetoothmodule.constant.Constants;
import com.standard.bluetoothmodule.dataprocess.send.BaseSendDataProcess;
import com.standard.bluetoothmodule.dataprocess.send.ByteSendDataSendProcess;
import com.standard.bluetoothmodule.dataprocess.send.FileSendDataProcess;

import java.io.File;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;


/**
 * @author xiaolong
 */
public class BtManager {

    private File mFileCache;
    private static final int REQUEST_ENABLE_BT = 1;
    private static final String TAG = BtManager.class.getSimpleName();
    private BluetoothAdapter mBtAdapter;
    private Activity mActivity;
    private BluetoothTransferService mTransferService;
    private BluetoothStatusReceiver mBluetoothStatusReceiver;
    private BluetoothScanReceiver mBluetoothScanReceiver;
    private IScanCallback mIScanCallback;
    private static BtManager instance = null;
    private DeviceHandler mDeviceHandler;
    private PriorityQueue<BaseSendDataProcess> mDataSendQueue;
    private BaseSendDataProcess mCurrentDataSendProcess;

    /**
     * 单实例蓝牙管理器，由于普通蓝牙，在整个声明周期只有一个连接。
     * 如果是一直需要使用蓝牙对象，没必要每次去构建新对象，创建新的连接。
     *
     * @return
     */
    public static BtManager getInstance() {
        if (instance == null) {
            throw new RuntimeException("在使用BtManager之前请先用BtManager.init()进行初始化");
        }
        return instance;
    }

    /**
     * 构建一个单例的instance
     *
     * @param activity
     */
    public static void init(Activity activity) {
        if (instance == null) {
            synchronized (BtManager.class) {
                if (instance == null) {
                    instance = new BtManager(activity);
                }
            }
        }
    }

    /**
     * init方法只需要进行一次，请确保Activity的声明周期是全局的。
     *
     * @param activity
     */
    private BtManager(Activity activity) {
        this.mActivity = activity;
        mDataSendQueue = new PriorityQueue<>();
        registerReceiver(activity);
        mDeviceHandler = new DeviceHandler();
        BluetoothManager btManager = (BluetoothManager) activity
                .getSystemService(Context.BLUETOOTH_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            mBtAdapter = btManager.getAdapter();
        } else {
            mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        if (mBtAdapter == null) {
            Toast.makeText(activity, "当前设备不支持蓝牙！", Toast.LENGTH_SHORT).show();
            return;
        }
        startService();
    }

    /**
     * 启动蓝牙接入服务
     */
    public void startService() {
        if (mTransferService == null && mBtAdapter.isEnabled()) {
            mTransferService = new BluetoothTransferService(mDeviceHandler);
            mTransferService.setFileCachePath(mFileCache);
            mTransferService.start();
        }
    }

    /**
     * 注册蓝牙扫描广播接收器
     */
    private void registerReceiver(Context context) {
        //注册蓝牙状态接收器 蓝牙开启关闭
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        mBluetoothStatusReceiver = new BluetoothStatusReceiver();
        context.registerReceiver(mBluetoothStatusReceiver, filter);
        //注册蓝牙扫描监听器
        mBluetoothScanReceiver = new BluetoothScanReceiver(new IScanCallback() {
            @Override
            public void discoverDevice(BluetoothDevice bluetoothDevice, short rssi) {
                if (mIScanCallback != null) {
                    mIScanCallback.discoverDevice(bluetoothDevice, rssi);
                }
            }

            @Override
            public void scanTimeout() {
                if (mIScanCallback != null) {
                    mIScanCallback.scanTimeout();
                }
            }

            @Override
            public void scanFinish(List<BluetoothDevice> bluetoothList) {
                if (mIScanCallback != null) {
                    mIScanCallback.scanFinish(bluetoothList);
                }
            }
        });
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(mBluetoothScanReceiver, intentFilter);
    }

    /**
     * 连接状态回调
     *
     * @param iConnectStateCallBack
     */
    public void setConnectStateCallback(IConnectStateCallBack iConnectStateCallBack) {
        mDeviceHandler.setIConnectStateCallBack(iConnectStateCallBack);
    }

    /**
     * 配置数据传输回调
     *
     * @param iDataTransferCallBack
     */
    @Deprecated
    public void setDataTransferCallBack(IDataTransferCallBack iDataTransferCallBack) {
        mDeviceHandler.setIDataTransferCallBack(iDataTransferCallBack);
    }

    /**
     * 设置当前设备名称
     *
     * @param deviceName
     */
    public void setDeviceName(String deviceName) {
        if (!mBtAdapter.getName().equals(deviceName)) {
            mBtAdapter.setName(deviceName);
        }
    }

    /**
     * 扫描回调
     *
     * @param iScanCallback
     */
    public void setIScanCallback(IScanCallback iScanCallback) {
        this.mIScanCallback = iScanCallback;
    }

    /**
     * 开始扫描
     */
    public void scanBluetooth() {
        checkAndOpenBluetooth();
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        mBtAdapter.startDiscovery();
    }


    /**
     * 检测蓝牙状态并开启蓝牙
     */
    public void checkAndOpenBluetooth() {
        if (mBtAdapter == null) {
            Toast.makeText(mActivity, "当前设备不支持蓝牙", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!mBtAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            mActivity.startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    /**
     * 获取已绑定设备列表
     *
     * @return
     */
    public Set<BluetoothDevice> getBondBlueToothList() {
        if (mBtAdapter == null) {
            Log.d(TAG, "mBtAdapter未初始化");
            return null;
        }
        return mBtAdapter.getBondedDevices();
    }

    public void connectToDevice(BluetoothDevice bluetoothDevice) {
        if (mTransferService == null) {
            //发现设备
            mTransferService = new BluetoothTransferService(mDeviceHandler);
            mTransferService.setFileCachePath(mFileCache);
        }
        mTransferService.connect(bluetoothDevice, true);
    }

    /**
     * 配置文件路径
     *
     * @param fileCache
     */
    public void setPath(File fileCache) {
        mFileCache = fileCache;
        if (mTransferService != null) {
            mTransferService.setFileCachePath(mFileCache);
        }
    }

    public class DeviceHandler extends Handler {
        private IConnectStateCallBack mIConnectStateCallBack;
        private IDataTransferCallBack mIDataTransferCallBack;

        public DeviceHandler() {
        }

        public void setIConnectStateCallBack(IConnectStateCallBack mIConnectStateCallBack) {
            this.mIConnectStateCallBack = mIConnectStateCallBack;
        }

        public void setIDataTransferCallBack(IDataTransferCallBack mIDataTransferCallBack) {
            this.mIDataTransferCallBack = mIDataTransferCallBack;
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothTransferService.STATE_CONNECTED:
                            Log.d(TAG, "已连接上");
                            mIConnectStateCallBack.connected();
                            break;
                        case BluetoothTransferService.STATE_CONNECTING:
                            Log.d(TAG, "连接中");
                            mIConnectStateCallBack.connecting();
                            break;
                        case BluetoothTransferService.STATE_LISTEN:
                            Log.d(TAG, "等待连接");
                            mIConnectStateCallBack.waitForConnect();
                            break;
                        case BluetoothTransferService.STATE_NONE:
                            Log.d(TAG, "未连接");
                            mIConnectStateCallBack.disConnect();
                            mTransferService.start();//在连接失败后重新等待连接
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    Log.d(TAG, "sendMessage");
                    mIDataTransferCallBack.sendDataSuccess((byte[]) msg.obj);
                    if (!mDataSendQueue.isEmpty()) {
                        mCurrentDataSendProcess = mDataSendQueue.poll();
                    }
                    if (mCurrentDataSendProcess != null && mCurrentDataSendProcess.hasNextPack()) {
                        mCurrentDataSendProcess.sendNextPack();
                    }
                    break;
                case Constants.MESSAGE_READ:
                    mIDataTransferCallBack.receiveData(msg.obj.toString());
                    break;
                //receiving
                case Constants.MESSAGE_READING:
                    //当前包， 总包
                    mIDataTransferCallBack.onReceiving(msg.arg1, msg.arg2);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    mIConnectStateCallBack.connectedToDeviceName((BluetoothDevice) msg.getData().getParcelable(Constants.DEVICE_NAME));
                    break;
                case Constants.MESSAGE_TOAST:
                    Toast.makeText(mActivity, msg.getData().getString(Constants.TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
                case Constants.MESSAGE_OUTOFTIME:
                    mIDataTransferCallBack.onReceiveOutOfTime();
                    break;
                default:
                    break;
            }
        }
    }


    /**
     * 蓝牙状态接收器
     */
    private class BluetoothStatusReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_OFF) {
                    Log.e(TAG, "检测到蓝牙关闭");
                } else if (state == BluetoothAdapter.STATE_ON) {
                    Log.e(TAG, "检测到蓝牙打开");
                    startService();
                    scanBluetooth();
                }
            }
        }

    }

    public void sendByteData(byte[] data) {
        if (mDataSendQueue.isEmpty()) {
            //直接发消息
            mCurrentDataSendProcess = new ByteSendDataSendProcess(data, mTransferService);
            mCurrentDataSendProcess.sendNextPack();
        } else {
            mDataSendQueue.add(new ByteSendDataSendProcess(data, mTransferService));
        }
    }

    public void sendFileData(File file) {
        if (mDataSendQueue.isEmpty()) {
            //直接发消息
            mCurrentDataSendProcess = new FileSendDataProcess(file, mTransferService);
            mCurrentDataSendProcess.sendNextPack();
        } else {
            mDataSendQueue.add(new FileSendDataProcess(file, mTransferService));
        }
    }

    public void sendStringData(String data) {
        sendByteData(data.getBytes());
    }

    public void disconnected() {
        if (mTransferService != null) {
            mTransferService.stop();
        }
        instance = null;
        destroyReceiver();
    }

    /**
     * 解除广播接收器
     */
    private void destroyReceiver() {
        if (mBluetoothStatusReceiver != null) {
            mActivity.unregisterReceiver(mBluetoothStatusReceiver);
        }
        if (mBluetoothScanReceiver != null) {
            mActivity.unregisterReceiver(mBluetoothScanReceiver);
        }
    }
}

