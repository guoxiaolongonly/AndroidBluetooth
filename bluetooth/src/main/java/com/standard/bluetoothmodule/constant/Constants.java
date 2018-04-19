package com.standard.bluetoothmodule.constant;

public final class Constants {

    public static final byte[] HEADER = {0x7e, 0x7e};
    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "Lucky";
    public static final String TOAST = "toast";
    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_READING = 3;
    public static final int MESSAGE_WRITING = 3;
    public static final int MESSAGE_WRITE = 4;
    public static final int MESSAGE_DEVICE_NAME = 5;
    public static final int MESSAGE_TOAST = 6;
    public static final int MESSAGE_OUTOFTIME = 7;
}
