package com.snach.literatureclub.utils;

import java.io.*;

public class FileUtil {
    public static byte[] getBytesByFile(File f) {
        try {
            FileInputStream fis = new FileInputStream(f);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            byte[] data = bos.toByteArray();
            fis.close();
            bos.close();
            return data;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String generateFileNameByTimesStamp(String prefix, String suffix){
        if (prefix == null) {
            prefix = "";
        }
        if (suffix == null) {
            suffix = "";
        }
        return prefix + System.currentTimeMillis() + suffix;
    }
}
