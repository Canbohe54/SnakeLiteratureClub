package com.snach.literatureclub.utils;

import com.snach.literatureclub.common.exception.NullFileException;
import com.snach.literatureclub.common.exception.UnsupportedFileTypeException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

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

    private static String getDocxRawTextByBytes(byte[] byteFile) {
        File tempFile = null;
        String rawText;
        try {
            tempFile = File.createTempFile("tempFile", ".docx");
            Files.write(tempFile.toPath(), byteFile);
            rawText = TextExtractionTools.extractTextFromDocx(tempFile.getCanonicalPath());
        } catch (IOException e) {
            throw new NullFileException();
        } finally {
            if (tempFile != null) {
                tempFile.delete();
            }
        }
        return rawText;
    }

    private static String getTxtRawTextByBytes(byte[] byteFile) {
        return new String(byteFile, StandardCharsets.UTF_8);
    }

    public static String getFileRawTextByBytes(byte[] byteFile, String fileType) {
        return switch (fileType) {
            case "docx" -> getDocxRawTextByBytes(byteFile);
            case "text" -> getTxtRawTextByBytes(byteFile);
            default -> throw new UnsupportedFileTypeException("*.docx/*.txt");
        };
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
