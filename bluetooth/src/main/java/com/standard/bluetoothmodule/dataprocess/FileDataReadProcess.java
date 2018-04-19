package com.standard.bluetoothmodule.dataprocess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author xiaolong
 * @version v1.0
 * @function <描述功能>
 * @date: 2017/10/19 11:03
 */

public class FileDataReadProcess extends BaseProcess<File> {
    FileOutputStream fileOutputStream;

    public FileDataReadProcess(File file) throws FileNotFoundException {
        fileOutputStream = new FileOutputStream(file);
        mResponseData.data = file;
    }


    @Override
    public void writeBytes(byte[] bytes) throws IOException {
        fileOutputStream.write(bytes);
    }

    @Override
    public void writeByte(byte bytes) throws IOException {
        fileOutputStream.write(bytes);
    }

    public void closeStream() throws IOException {
        fileOutputStream.close();
    }

}
