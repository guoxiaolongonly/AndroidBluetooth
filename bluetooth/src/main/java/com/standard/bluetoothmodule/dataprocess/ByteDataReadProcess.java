package com.standard.bluetoothmodule.dataprocess;

import com.standard.bluetoothmodule.util.ByteUtil;

import java.io.IOException;

/**
 * @author xiaolong
 * @version v1.0
 * @function <描述功能>
 * @date: 2017/10/19 11:03
 */

public class ByteDataReadProcess extends BaseProcess<byte[]> {

    public ByteDataReadProcess() {
        super();
    }


    @Override
    public void writeBytes(byte[] bytes) {
        if (mResponseData.data == null) {
            mResponseData.data = bytes;
        } else {
            mResponseData.data = ByteUtil.concat(mResponseData.data, bytes);
        }

    }

    @Override
    public void writeByte(byte mByte) throws IOException {
        if (mResponseData.data == null) {
            mResponseData.data = new byte[]{mByte};
        } else {
            mResponseData.data = ByteUtil.concat(mResponseData.data, new byte[]{mByte});
        }
    }


}
