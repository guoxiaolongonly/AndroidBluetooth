package com.standard.bluetoothmodule.util;

import java.util.Arrays;

/**
 * @author xiaolong
 * @version v1.0
 * @function <描述功能>
 * @date: 2017/10/13 14:47
 */

public class ByteUtil {


    /**
     * 校验和
     *
     * @param msg    需要计算校验和的byte数组
     * @param length 校验和位数
     * @return 计算出的校验和数组
     */
    public static byte[] sumCheck(byte[] msg, int length) {
        long sum = 0;
        byte[] sumByte = new byte[length];

        /** 逐Byte添加位数和 */
        for (byte byteMsg : msg) {
            long count = ((long) byteMsg >= 0) ? (long) byteMsg : ((long) byteMsg + 256);
            sum += count;
        } /** end of for (byte byteMsg : msg) */

        /** 位数和转化为Byte数组 */
        for (int liv_Count = 0; liv_Count < length; liv_Count++) {
            sumByte[length - liv_Count - 1] = (byte) (sum >> (liv_Count * 8) & 0xff);
        } /** end of for (int liv_Count = 0; liv_Count < length; liv_Count++) */

        return sumByte;
    }

    public static byte[] concat(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        for (byte[] array : rest) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array : rest) {
            System.arraycopy(array, 0, result, offset, array.length);
            offset += array.length;
        }
        return result;
    }

    /**
     * @return 四位的字节数组 (低位在后，高位在前)
     * @方法功能 整型与字节数组的转换
     */
    public static byte[] intToBytes(int value) {
        byte[] src = new byte[4];
        src[0] = (byte) ((value >> 24) & 0xFF);
        src[1] = (byte) ((value >> 16) & 0xFF);
        src[2] = (byte) ((value >> 8) & 0xFF);
        src[3] = (byte) (value & 0xFF);
        return src;
    }

    /**
     * @param data    数据
     * @param digital 几字节一组
     * @return
     */
    public static int[] transferToIntArray(byte[] data, int digital) {
        if (digital > 4) {
            throw new UnsupportedOperationException("字节超长");
        }
        int[] datas = new int[data.length / digital];
        for (int i = 0; i < datas.length; i++) {
            byte[] intData = new byte[]{0, 0, 0, 0};
            int startPosition=4-digital;
            for (int j = 0; j < 4 - startPosition; j++) {
                intData[startPosition + j] = data[i * digital + j];
            }
            datas[i] = bytesToInt(intData, 0);
        }
        return datas;
    }




    /**
     * byte数组中取int数值，本方法适用于(低位在后，高位在前)的顺序，和和intToBytes（）配套使用
     *
     * @param src    byte数组
     * @param offset 从数组的第offset位开始
     * @return int数值
     */
    public static int bytesToInt(byte[] src, int offset) {
        int value;
        value = (int) (((src[offset] & 0xFF) << 24)
                | ((src[offset + 1] & 0xFF) << 16)
                | ((src[offset + 2] & 0xFF) << 8)
                | (src[offset + 3] & 0xFF));
        return value;
    }

    /**
     * @return 2位的字节数组 (低位在后，高位在前)
     * @方法功能 整型与字节数组的转换
     */
    public static byte[] intToBytes2(int value) {
        byte[] src = new byte[2];
        src[0] = (byte) ((value >> 8) & 0xFF);
        src[1] = (byte) (value & 0xFF);
        return src;
    }

    /**
     * byte数组中取int数值，本方法适用于(低位在后，高位在前)的顺序，和和intToBytes（）配套使用
     *
     * @param src    byte数组
     * @param offset 从数组的第offset位开始
     * @return int数值
     */
    public static int bytesToInt2(byte[] src, int offset) {
        int value;
        value = ((src[offset + 0] & 0xFF) << 8)
                | (src[offset + 1] & 0xFF);
        return value;
    }


    public static String hexAppend(byte data) {
        int value = ((int) data) & 0xff;
        return hexAppend(value);
    }

    public static String hexAppend(int data) {
        StringBuffer sb = new StringBuffer();
        if (data < 16) {
            sb.append("0");
        }
        sb.append(Integer.toHexString(data)).append(" ");
        return sb.toString();
    }
}

