package cn.xiaolong.bluetoothconnectdemo.bluetooth.callback;

import java.util.List;

public interface IScanCallback<T> {
    void discoverDevice(T t);
    void scanTimeout();
    void scanFinish(List<T> tList);
}
