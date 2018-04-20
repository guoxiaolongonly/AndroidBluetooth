package com.standard.bluetoothmodule.dataprocess;

/**
 * @author xiaolong
 * @version v1.0
 * @function <这个类应该是已经结束的类。>
 * @date: 2017/10/19 10:24
 */

public class ResponseData<T> {
    public T data;
    public String receiveTime;

    public ResponseData(T data) {
        this.data = data;
    }

    public ResponseData() {
    }
}
