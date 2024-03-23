package com.snach.literatureclub.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import toolgood.words.IllegalWordsSearch;
import toolgood.words.IllegalWordsSearchResult;
import toolgood.words.StringSearch;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SensitiveWordsTools {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveWordsTools.class);

    private static final String sensitiveWordsFilePath;

    private static final List<String> sensitiveWordsList;

    private static final StringSearch searcher = new StringSearch();

    private static final IllegalWordsSearch harderSearcher = new IllegalWordsSearch();

    static {
        // read the sensitive words list
        sensitiveWordsFilePath = Objects.requireNonNull(Thread.currentThread().getContextClassLoader().getResource("sensi_words.txt")).getPath();
        sensitiveWordsList = readTxt(sensitiveWordsFilePath);
        // set the keywords of searchers
        searcher.SetKeywords(sensitiveWordsList);
        harderSearcher.SetKeywords(sensitiveWordsList);
    }

    /**
     * 根据文件路径获取到list集合
     * @param filePath
     * @return
     */
    public static List<String> readTxt(String filePath) {
        List<String> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))){
            String line;
            while ((line = br.readLine()) != null) {
                list.add(line);
            }
        } catch (IOException e) {
            logger.error("IOException(" + filePath + ") occurred. " + e.getMessage());
        }
        return list;
    }

    /**
     * 判断是否存在敏感词
     * @param txt
     * @return
     */
    public static Boolean judgeSensitivityWord(String txt) {
        return searcher.ContainsAny(txt);
    }

    /**
     * 查找所有违禁词
     * @param txt
     * @return 违禁词列表
     */
    private static List<String> findAllWords(String txt) {
        return searcher.FindAll(txt);
    }

    private static List<IllegalWordsSearchResult> strictFindAllWords(String txt) {
        return harderSearcher.FindAll(txt);
    }

    public static List<?> findAllSensitiveWords(String txt, boolean useStrict) {
        return useStrict ? strictFindAllWords(txt) : findAllWords(txt);
    }

    /**
     * 敏感词过滤用*替代
     * @param txt 内容
     * @param replace 敏感词替代字符 '*'
     * @return
     */
    public static String filterSensitivityWord(String txt,char replace){
        return searcher.Replace(txt,replace);
    }
}