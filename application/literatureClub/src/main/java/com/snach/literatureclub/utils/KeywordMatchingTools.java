package com.snach.literatureclub.utils;

import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KeywordMatchingTools {
    /**
     * 构建简单关键词匹配的正则表达式
     * <p>
     * Examples:
     * <blockquote><pre>
     *     formRegex({})
     *         -> returns "()"
     *     formRegex({"word"})
     *         -> returns "(word)"
     *     formRegex({"word", "sentence"})
     *         -> returns "(word)|(sentence)"
     * </pre></blockquote>
     * </p>
     *
     * @param keywords 关键字列表
     * @return 构建的正则表达式
     */
    public static String formRegex(List<String> keywords) {
        StringBuilder regex = new StringBuilder();
        for (String keyword : keywords) {
            regex.append(String.format("(%s)|", keyword));
        }
        if (!regex.isEmpty()) {
            regex.deleteCharAt(regex.length() - 1);
        }
        return regex.toString();
    }

    /**
     * 多关键词匹配
     * @param text 待匹配文本
     * @param keywords 关键字列表
     * @return 关键字所在位置
     */
    public static List<Pair<Integer, Integer>> keywordsMatch(String text, List<String> keywords) {
        Pattern pattern = Pattern.compile(formRegex(keywords));
        Matcher matcher = pattern.matcher(text);
        List<Pair<Integer, Integer>> keywordLocations = new ArrayList<>();
        while (matcher.find()) {
            keywordLocations.add(Pair.of(matcher.start(), matcher.end()));
        }
        return keywordLocations;
    }

    /**
     * 按特定模式替换字符串
     * <p>
     * Examples:
     * <blockquote><pre>
     *     stringReplace("This is an example.", {{0, 4}, {5, 7}}, "@%s@")
     *         -> returns "@This@ @is@ an example."
     *     stringReplace("This is another example.", {{0, 4}, {5, 7}}, "**")
     *         -> returns "** ** another example."
     *     stringReplace("Pattern can contain multiple '%s'.", {{0, 7}, {5, 7}}, "%s(%s)")
     *         -> returns "Pattern(Pattern) can contain multiple '%s'."
     * </pre></blockquote>
     * </p>
     *
     * @param text 待替换的字符串
     * @param locations 需要替换的子串位置
     * @param pattern 替换模式（类似于格式化字符串，使用%s代替需要替换的子串）
     * @return 替换后的字符串
     */
    public static String stringReplace(String text, List<Pair<Integer, Integer>> locations, String pattern) {
        StringBuilder replacedText = new StringBuilder(text);
        for (int i = locations.size() - 1; i >= 0; i--) {
            Pair<Integer, Integer> loc = locations.get(i);
            String key = text.substring(loc.getFirst(), loc.getSecond());
            replacedText.delete(loc.getFirst(), loc.getSecond());
            replacedText.insert(loc.getFirst(), pattern.replace("%s", key));
        }
        return replacedText.toString();
    }

    /**
     * 在字符串对应位置的子串前后插入一对指定的字符串
     * [相当于pattern只包含一个'%s'的stringReplace(String, List<Pair<Integer, Integer>>, String)]
     * <p>
     * Example:
     * <blockquote><pre>
     *     stringReplace("This is an example.", {{8, 10}}, {"[", "]"})
     *         -> returns "This is [an] example."
     * </pre></blockquote>
     * </p>
     *
     * @param text 待修改的字符串
     * @param locations 需要修改的子串位置
     * @param replPair 子串前后添加的字符串
     * @return 修改后的子串
     */
    public static String stringReplace(String text, List<Pair<Integer, Integer>> locations, Pair<String, String> replPair) {
        StringBuilder replacedText = new StringBuilder(text);
        for (int i = locations.size() - 1; i >= 0; i--) {
            Pair<Integer, Integer> loc = locations.get(i);
            replacedText.insert(loc.getSecond(), replPair.getSecond());
            replacedText.insert(loc.getFirst(), replPair.getFirst());
        }
        return replacedText.toString();
    }
}
