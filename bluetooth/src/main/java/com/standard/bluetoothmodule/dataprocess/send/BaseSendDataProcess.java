package com.standard.bluetoothmodule.dataprocess.send;


import com.standard.bluetoothmodule.BluetoothTransferService;
import com.standard.bluetoothmodule.callback.IDataTransferCallBack;


/**
 * @author xiaolong
 * @version v1.0
 * @function <基础的数据传输处理类>
 * @date: 2017/11/20 09:41
 */

public abstract class BaseSendDataProcess {
    public static final int DEFAULT_DATA_SIZE = 32000;
    protected byte[] currentTransferData;
    protected BluetoothTransferService mBluetoothTransferService;
    private IDataTransferCallBack mIDataTransferCallBack;

    public BaseSendDataProcess(BluetoothTransferService bluetoothTransferService) {
        mBluetoothTransferService = bluetoothTransferService;
    }

    public IDataTransferCallBack getIDataTransferCallBack() {
        return mIDataTransferCallBack;
    }

    /**
     * 获取数据长度
     *
     * @return
     */
    abstract int getAllPackSize();

    /**
     * 获取当前传输的长度
     *
     * @return
     */
    abstract int getCurrentPack();

    /**
     * 获取数据长度
     *
     * @return
     */
    abstract long getDataLength();

    /**
     * 获取当前传输的长度
     *
     * @return
     */
    abstract long getCurrentTransferLength();

    /**
     * 获得当前正在传输的数据
     *
     * @return
     */
    public byte[] getCurrentTransferPack() {
        return currentTransferData;
    }

    /**
     * 发送当前包..一般用于重新发送
     */
    public void sendCurrentPack() {
        mBluetoothTransferService.write(currentTransferData);
    }

    /**
     * 下一次传输包
     */
    public void sendNextPack() {
        currentTransferData = getNextPack();
        sendCurrentPack();
    }


    /**
     * 获取下次传输的包
     *
     * @return
     */
    abstract byte[] getNextPack();


    /**
     * @return
     */
    public abstract boolean hasNextPack();
}
