package cn.xiaolong.bluetoothconnectdemo.bluetooth.callback;

import java.io.File;

/**
 * @author xiaolong
 * @version v1.0
 * @function <状态回调>
 * @date: 2017/10/12 14:34
 */

public interface IConnectStateCallBack {
    //连接状态有四种
    //连接中
    void connecting();

    //已连接
    void connected();

    //未连接
    void disConnect();

    //等待连接
    void waitForConnect();


    //发送消息成功
    void sendDataSuccess(byte[] data);

    //连接到设备名称
    void connectedToDeviceName(String deviceName);

    //收到文件
    void receiveData(File file);

    //正在接受
    void onReceiving(int current, int all);
}
