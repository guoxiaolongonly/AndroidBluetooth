package com.standard.bluetoothmodule.dataprocess.send;

import android.util.Log;

import com.standard.bluetoothmodule.BluetoothTransferService;
import com.standard.bluetoothmodule.callback.IDataTransferCallBack;
import com.standard.bluetoothmodule.util.PackUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

/**
 * @author xiaolong
 * @version v1.0
 * @function <描述功能>
 * @date: 2017/11/20 10:07
 */

public class FileSendDataProcess extends BaseSendDataProcess {
    private FileInputStream mFio;
    private File mFile;
    protected long currentTransferLength = 0L;
    //数据传输回调
    private int currentPack;
    private int allPack;
    private long fileSize;

    public FileSendDataProcess(File file, BluetoothTransferService bluetoothTransferService) {
        super(bluetoothTransferService);
        this.mFile = file;
        try {
            mFio = new FileInputStream(mFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        currentPack = 0;
        fileSize = file.length();
        allPack = (int) (fileSize / DEFAULT_DATA_SIZE) + 1;
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
        return mFile.length();
    }

    @Override
    byte[] getNextPack() {
        currentPack++;
        if (currentPack > allPack) {
            return new byte[]{};
        }
        byte[] buffer = new byte[DEFAULT_DATA_SIZE];
        int length = 0;
        try {
            length = mFio.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (currentPack == allPack) {
            try {
                mFio.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (length < DEFAULT_DATA_SIZE) {
            buffer = Arrays.copyOf(buffer, length);
        }
        currentTransferLength += length;
        byte [] fileNameBytes=mFile.getName().getBytes();
        return PackUtil.pack(fileNameBytes.length,fileNameBytes, length, currentPack, allPack, buffer);
    }

    @Override
    public boolean hasNextPack() {
        return currentPack < allPack;
    }

    /**
     * 当前传输进度
     *
     * @return
     */
    @Override
    public long getCurrentTransferLength() {
        return currentTransferLength;
    }


}
