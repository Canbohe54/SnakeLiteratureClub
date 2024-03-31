package com.snach.literatureclub.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.snach.literatureclub.common.ArticleAuditStatus;
import com.snach.literatureclub.common.ArticlePublishStatus;
import jakarta.annotation.Nullable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * title        稿件标题
 * description  稿件描述
 * status       稿件状态 1：保存成功 2：待审核 3.已发布 4.未通过 0：保存失败
 * attr         稿件其他多值属性，如标签
 * mentor       指导老师id
 */
@Data
@NoArgsConstructor
@ToString
public class Article {
    // Article basic info
    String id;
    Date time;
    @JsonProperty("text_by")
    String textBy;

    String title;
    @Deprecated
    String text; //正文
    String description;

    // Author info
    String authorName;
    String authorOrganization;
    String authorGrade;

    // Article status info
    ArticleAuditStatus auditStatus;
    ArticlePublishStatus publishStatus;

    // Article raw file info
    @JsonIgnore
    byte[] raw;
    @JsonProperty("file_type")
    String fileType;
    @JsonProperty("latest_approval_article_url")
    String latestApprovalArticleUrl;

    // Article attribute
    @JsonIgnore
    String tags;
    @JsonProperty("tags")
    Map<String, Set<String>> articleTags;

    // Article extra info
    @Nullable
    String mentor;
    @Nullable
    @JsonProperty("received_by")
    String receivedBy;
    @JsonProperty("audited_by")
    String auditedBy;

    String reason;
    public String getTags() {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (this.articleTags == null) {
                this.articleTags = objectMapper.readValue(this.tags, new TypeReference<>() {
                });
            }
            return objectMapper.writeValueAsString(this.articleTags);
        } catch (Exception ignore) {
            return this.tags;
        }
    }

    public Map<String, Set<String>> getArticleTags() {
        if (this.articleTags == null) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                this.articleTags = objectMapper.readValue(this.tags, new TypeReference<>() {
                });
                this.tags = objectMapper.writeValueAsString(this.articleTags);
            } catch (Exception ignore) {
                return null;
            }
        }
        return this.articleTags;
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
