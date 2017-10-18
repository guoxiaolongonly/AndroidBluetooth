package cn.xiaolong.bluetoothconnectdemo.bluetooth;

import java.io.File;

/**
 * @author xiaolong
 * @version v1.0
 * @function <这个类作为传输的基础类>
 * @date: 2017/10/12 11:29
 */

public class BaseDataBean {
    private int dataType;
    private String textData;
    private int dataLength;

    public BaseDataBean() {
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public void setTextData(String textData) {
        this.textData = textData;
    }

    public void setDataLength(int dataLength) {
        this.dataLength = dataLength;
    }

    public int getDataType() {
        return dataType;
    }

    public byte[] getTextData() {
        return new byte[]{};
    }

    public int getDataLength() {
        return dataLength;
    }
}
