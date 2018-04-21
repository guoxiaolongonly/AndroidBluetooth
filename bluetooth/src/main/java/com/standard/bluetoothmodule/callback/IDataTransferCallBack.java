package com.standard.bluetoothmodule.callback;


import com.standard.bluetoothmodule.dataprocess.ResponseData;

/**
 * @author xiaolong
 * @version v1.0
 * @function <描述功能>
 * @date: 2017/10/20 09:32
 */

public interface IDataTransferCallBack  {


    /**
     * 发送消息成功
     *
     * @param data
     */
    void sendDataSuccess(byte[] data);

    /**
     * 收到数据
     *
     * @param result
     */
    void receiveData(ResponseData result);

    /**
     * 接收进度
     *
     * @param current
     * @param all
     */
    void onReceiving(int current, int all);


    /**
     * 接收超时
     */
    void onReceiveOutOfTime();

}
