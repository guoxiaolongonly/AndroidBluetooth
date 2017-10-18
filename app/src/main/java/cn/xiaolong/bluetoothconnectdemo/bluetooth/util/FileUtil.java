package cn.xiaolong.bluetoothconnectdemo.bluetooth.util;

import android.content.Context;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * @author xiaolong
 * @version v1.0
 * @function <描述功能>
 * @date: 2017/10/13 14:47
 */

public class FileUtil {
    public static File checkAndCreateFile(File path, String name) {
        int filePosition = 1;
        File file = new File(path, name.replace("\\", "").trim());
        while (file.exists()) {
            file = new File(path, name.replace("\\.", "(" + filePosition + ")\\.").replace("\\", "").trim());
            filePosition += 1;
        }
        return file;
    }
}

