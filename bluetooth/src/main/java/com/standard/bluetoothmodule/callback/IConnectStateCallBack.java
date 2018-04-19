package com.standard.bluetoothmodule.callback;

/**
 * @author xiaolong
 * @version v1.0
 * @function <状态回调 >
 * @date: 2017/10/12 14:34
 */

public interface IConnectStateCallBack {
    /**
     * 当前状态为正在连接状态
     */
    void connecting();

    /**
     * 当前状态为已连接状态
     */
    void connected();

    /**
     * 当前状态为未连接状态
     */
    void disConnect();

    /**
     * 当前状态为等待连接状态
     */
    void waitForConnect();

    /**+
     * 连接到设备
     *
     * @param deviceName
     */
    void connectedToDeviceName(String deviceName);
}
