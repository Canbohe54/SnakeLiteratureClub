package com.snach.literatureclub.utils;

import org.apache.ibatis.annotations.Param;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import toolgood.words.StringSearch;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

@Component
public class SensitiveWordsTools {


    /**
     * 根据文件路径获取到list集合
     * @param filePath
     * @return
     */
    public static List<String> readTxt(String filePath) {
        List<String> list = new ArrayList<String>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            String line = null;
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 判断是否存在敏感词
     * @param txt
     * @return
     */
    public static Boolean judgeSensitivityWord(String txt) {
        String path = Thread.currentThread().getContextClassLoader().getResource("sensi_words.txt").getPath();
        List<String> list = readTxt(path);
        StringSearch iwords = new StringSearch ();
        iwords.SetKeywords(list);
        return iwords.ContainsAny(txt);
    }

    /**
     * 查找所有违禁词
     * @param txt
     * @return 违禁词列表
     */
    public static List<String> FindAllWords(String txt) {
        String path = Thread.currentThread().getContextClassLoader().getResource("sensi_words.txt").getPath();
        List<String> list = readTxt(path);
        StringSearch iwords = new StringSearch ();
        iwords.SetKeywords(list);
        return iwords.FindAll(txt);
    }

    /**
     * 敏感词过滤用*替代
     * @param txt 内容
     * @param replace 敏感词替代字符 '*'
     * @return
     */
    public static String filterSensitivityWord(String txt,char replace){
        String path = Thread.currentThread().getContextClassLoader().getResource("sensi_words.txt").getPath();
        List<String> list = readTxt(path);
        StringSearch iwords = new StringSearch ();
        iwords.SetKeywords(list);
        return iwords.Replace(txt,replace);
    }
}