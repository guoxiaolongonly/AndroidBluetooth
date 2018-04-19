package com.standard.bluetoothmodule.dataprocess;

import java.io.IOException;

/**
 * @author xiaolong
 * @version v1.0
 * @function <描述功能>
 * @date: 2017/10/19 14:17
 */

public abstract class BaseProcess<T> {
    protected ResponseData<T> mResponseData;

    public BaseProcess() {
        mResponseData = new ResponseData<>();
    }


    public abstract void writeBytes(byte[] bytes) throws IOException;

    /**
     * 写数据到本地
     * @param mByte
     * @throws IOException
     */
    public abstract void writeByte(byte mByte) throws IOException;

    public ResponseData<T> getResponseResult() {
        return mResponseData;
    }

}
