package com.standard.bluetoothmodule.dataprocess.send;


import com.standard.bluetoothmodule.BluetoothTransferService;
import com.standard.bluetoothmodule.callback.IDataTransferCallBack;
import com.standard.bluetoothmodule.util.PackUtil;

import java.util.Arrays;

/**
 * @author xiaolong
 * @version v1.0
 * @function <字节文件发送处理>
 * @date: 2017/11/20 14:05
 */

public class ByteSendDataSendProcess extends BaseSendDataProcess {
    private byte[] mDatas;
    private long currentTransferLength = 0L;
    private int currentPack;
    private int allPack;
    private int mDataLength;


    public ByteSendDataSendProcess(byte[] datas,
                                   BluetoothTransferService bluetoothTransferService) {
        super(bluetoothTransferService);
        this.mDatas = datas;
        currentPack = 0;
        allPack = datas.length / DEFAULT_DATA_SIZE + 1;
        mDataLength = datas.length;
    }

    @Override
    int getAllPackSize() {
        return allPack;
    }

    @Override
    int getCurrentPack() {
        return currentPack;
    }

    @Override
    long getDataLength() {
        return mDataLength;
    }

    @Override
    long getCurrentTransferLength() {
        return currentTransferLength;
    }

    @Override
    byte[] getNextPack() {
        currentPack++;
        if (currentPack > allPack) {
            return new byte[]{};
        }
        byte[] dataPart = Arrays.copyOfRange(mDatas, (currentPack - 1) * DEFAULT_DATA_SIZE,
                currentPack * DEFAULT_DATA_SIZE > mDataLength ? mDataLength : currentPack * 1024);
        currentTransferLength += dataPart.length;
        return PackUtil.pack(0,new byte[]{}, dataPart.length, currentPack, allPack, dataPart);
    }

    @Override
    public boolean hasNextPack() {
        return currentPack < allPack;
    }
}
