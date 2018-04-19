package com.standard.bluetoothmodule.util;

import android.util.Log;

import java.util.Arrays;

/**
 * @author xiaolong
 * @version v1.0
 * @function <描述功能>
 * @date: 2017/11/20 11:25
 */

public class PackUtil {


    public static byte[] pack(int nameLength,byte[] nameByte, int packLength, int currentPack, int allPackLength, byte[] data) {
        return pack(new byte[]{0x7e, 0x7e}, nameLength, nameByte, packLength, currentPack, allPackLength, data);
    }

    /**
     * 打包类，将数据打包
     *
     * @param buffer
     * @param packLength
     * @return
     */
    public static byte[] pack(byte[] header, int nameLength, byte[] nameByte, int packLength, int currentPack, int allPackLength, byte[] buffer) {
        buffer = Arrays.copyOf(buffer, packLength);
        int sumCheck = 0;
        int dataLength = buffer.length;
        byte[] dataLengthBytes = ByteUtil.intToBytes2(dataLength);
        byte[] allPackLengthBytes = ByteUtil.intToBytes2(allPackLength);
        byte[] currentPackLengthBytes = ByteUtil.intToBytes2(currentPack);
        byte[] resultData = ByteUtil.concat(header, new byte[]{(byte) (nameLength & 0xff)}, nameByte,
                allPackLengthBytes, currentPackLengthBytes, dataLengthBytes, buffer);
        StringBuffer databuffer = new StringBuffer();
        for (byte dataLengthResult : resultData) {
            databuffer.append(Integer.toHexString(dataLengthResult));
            databuffer.append(" ");
            sumCheck += dataLengthResult & 0xff;
        }
        sumCheck = sumCheck & 0xff;
        resultData = ByteUtil.concat(resultData, new byte[]{(byte) sumCheck});
        return resultData;
    }

}
