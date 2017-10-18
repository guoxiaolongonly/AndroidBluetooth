package cn.xiaolong.bluetoothconnectdemo.bluetooth;

import java.util.UUID;

/**
 * Created by CCB on 2016/11/18.
 */

public final class Constants {
    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "STD_DEVICE";
    public static final String TOAST = "toast";
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_READING = 3;
    public static final int MESSAGE_WRITE = 4;
    public static final int MESSAGE_DEVICE_NAME = 5;
    public static final int MESSAGE_TOAST = 6;

    public static String cacheDir = "";
    /**
     * 传输的数据类型
     */
//    public static final byte TYPE_TEXT = 0b00000000;
//    public static final byte TYPE_FILE = 0b00000001;
}
