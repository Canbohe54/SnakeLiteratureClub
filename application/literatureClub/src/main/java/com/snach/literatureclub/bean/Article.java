package com.snach.literatureclub.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.snach.literatureclub.common.ArticleStatus;
import jakarta.annotation.Nullable;
import lombok.*;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * title        稿件标题
 * description  稿件描述
 * status       稿件状态 1：保存成功 2：待审核 3.已发布 4.未通过 0：保存失败
 * attr         稿件其他多值属性，如标签
 * mentor       指导老师id
 *
 * @see Text
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@ToString
public class Article extends Text {
    String title;
    String description;
    ArticleStatus status;
    String attr; // 标签
    byte[] raw;
    @Nullable
    String mentor;
    @JsonProperty("file_type")
    String fileType;
    @JsonProperty("latest_approval_article_url")
    String latestApprovalArticleUrl;
    @JsonProperty("received_by")
    @Nullable
    String receivedBy;

    public Article(String id, String text, Date time, String textBy, String title, String description, ArticleStatus status, String attr, byte[] rawFile, String mentor, String fileType, String latestApprovalArticleUrl, String receivedBy) {
        super(id, time, text, textBy);
        this.status = status;
        this.title = title;
        this.description = description;
        this.attr = attr;
        this.raw = rawFile;
        this.mentor = mentor;
        this.fileType = fileType;
        this.latestApprovalArticleUrl = latestApprovalArticleUrl;
        this.receivedBy = receivedBy;
    }

    public Article(String id, String text, Date time, String textBy, String title, String description, ArticleStatus status, String attr) {
        super(id, time, text, textBy);
        this.status = status;
        this.title = title;
        this.description = description;
        this.attr = attr;
    }

    public Map<String, Object> packBasicInfo() {
        Map<String, Object> articleBasicInfo = new HashMap<>();
        for (Field field : Article.class.getDeclaredFields()) {
            if (!field.getName().equalsIgnoreCase("raw")) {
                try {
                    field.setAccessible(true);
                    articleBasicInfo.put(field.getName(), field.get(this));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        articleBasicInfo.put("id", this.getId());
        return articleBasicInfo;
    }
}
