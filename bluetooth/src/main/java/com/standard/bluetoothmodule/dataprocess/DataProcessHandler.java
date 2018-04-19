package com.standard.bluetoothmodule.dataprocess;

import android.os.Handler;

import com.standard.bluetoothmodule.constant.Constants;
import com.standard.bluetoothmodule.util.BitmapUtil;
import com.standard.bluetoothmodule.util.ByteUtil;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;


/**
 * @author xiaolong 719243738@qq.com
 * @version v1.0
 * @function <消息处理代理类，此类用来做消息处理和辨别消息详情>
 * @date: 2018/3/28 17:32
 */

public class DataProcessHandler {
    private static final String TAG = "DataProcessHandler";

    /**
     * 头部
     */
    private int mCurrentPack = 0;
    private int mAllPack;
    private int mDataLength;

    /**
     * 校验和
     */
    private long sumCheck = 0;
    private int currentDataLength = 0;
    private BaseProcess mDataProcess;

    private Handler mHandler;
    private File cacheFilePath;


    private int readIndex = 0;
    private byte[] cacheHeader;

    public DataProcessHandler(Handler handler) {
        mHandler = handler;
        cacheHeader = new byte[1024];
    }

    public void setCacheFilePath(File cacheFilePath) {
        this.cacheFilePath = cacheFilePath;
    }


    /**
     * 重头开始读数据
     *
     * @param header
     * @param currentPackBytes
     * @param allPackBytes
     * @param dataLengthBytes
     */
    private void readHeader(byte[] header, int fileNameBytesLength, byte[] fileNameBytes, byte[] allPackBytes, byte[] currentPackBytes, byte[] dataLengthBytes) {
        //校验和
        sumCheck = header[0] + header[1] + fileNameBytesLength;
        for (byte fileNameByte : fileNameBytes) {
            sumCheck += fileNameByte;
        }
        for (byte currentPackByte : currentPackBytes) {
            sumCheck += currentPackByte;
        }
        for (byte allPackByte : allPackBytes) {
            sumCheck += allPackByte;
        }
        for (byte dataLengthByte : dataLengthBytes) {
            sumCheck += dataLengthByte;
        }
        mCurrentPack = ByteUtil.bytesToInt2(currentPackBytes, 0);
        mAllPack = ByteUtil.bytesToInt2(allPackBytes, 0);
        mDataLength = ByteUtil.bytesToInt2(dataLengthBytes, 0);
        currentDataLength = 0;
    }

    /**
     * 读取字节
     *
     * @param data
     */
    public void readByte(byte data) {
        if (!isHeaderReadUp()) {
            if (readIndex == 0 && data == 0x7e) {
                cacheHeader[readIndex] = data;
                readIndex++;
            } else if (readIndex == 1 && cacheHeader[0] == data && data == Constants.HEADER[1]) {
                cacheHeader[readIndex] = data;
                readIndex++;
            } else if (readIndex > 1 && readIndex < cacheHeader.length) {
                if (readIndex == 2) {
                    int realHeaderLength = 9 + (data & 0xff);
                    cacheHeader = Arrays.copyOf(cacheHeader, realHeaderLength);
                }
                cacheHeader[readIndex] = data;

                if (isHeaderReadUp()) {
                    byte[] header = Arrays.copyOf(cacheHeader, 2);
                    int fileNameLength = cacheHeader[2];
                    byte[] fileNameBytes = Arrays.copyOfRange(cacheHeader, 3, fileNameLength + 3);
                    byte[] allPacks = Arrays.copyOfRange(cacheHeader, fileNameLength + 3, fileNameLength + 5);
                    byte[] currentPack = Arrays.copyOfRange(cacheHeader, fileNameLength + 5, fileNameLength + 7);
                    byte[] dataLengthBytes = Arrays.copyOfRange(cacheHeader, fileNameLength + 7, fileNameLength + 9);
                    readHeader(header, fileNameLength, fileNameBytes, allPacks, currentPack, dataLengthBytes);
                }
            } else {
                readIndex = 0;
            }
            return;
        }


        if (!isReadEnd()) {
            try {
                readData(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            sumCheck = sumCheck & 0xff;
//            Log.d(TAG, currentDataLength + "__" + mDataLength);
//            Log.d("sumCheck", (((int) data) & 0xff) + "__" + sumCheck);
            if (sumCheck == ((int) data & 0xff)) {
                postData();
            }
        }

    }

    /**
     * 发送消息给用户
     */
    private void postData() {
        //校验成功
        if (mCurrentPack < mAllPack) {

            mHandler.obtainMessage(Constants.MESSAGE_READING, mCurrentPack, mAllPack)
                    .sendToTarget();
            sumCheck = 0;
            currentDataLength = 0;
            readIndex = 0;
        } else {
            if (mDataProcess instanceof FileDataReadProcess) {
                try {
                    ((FileDataReadProcess) mDataProcess).closeStream();
                    BitmapUtil.rotateImageAndSave(cacheFilePath.getPath(), 180);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            mHandler.obtainMessage(Constants.MESSAGE_READ, mDataProcess.getResponseResult())
                    .sendToTarget();
            reset();
        }
    }

    private void readData(byte data) throws IOException {
        mDataProcess.writeByte(data);
        sumCheck += ((int) data) & 0xff;
        currentDataLength += 1;
    }

    private void reset() {
        if (mDataProcess != null && mDataProcess instanceof FileDataReadProcess) {
            try {
                ((FileDataReadProcess) mDataProcess).closeStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mDataProcess = null;
        readIndex = 0;
        currentDataLength = 0;
        cacheHeader = new byte[1024];
    }

    public boolean isHeaderReadUp() {
        return readIndex > cacheHeader.length;
    }

    public boolean isReadEnd() {
        return currentDataLength >= mDataLength;
    }

}
