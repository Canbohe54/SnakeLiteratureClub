package com.snach.literatureclub.bean;

import com.snach.literatureclub.common.ArticleStatus;
import lombok.*;
import org.apache.ibatis.javassist.tools.reflect.Reflection;

import java.util.Date;

/**
 * title        稿件标题
 * description  稿件描述
 * status       稿件状态 1：保存成功 2：待审核 3.已发布 4.未通过 0：保存失败
 * attr         稿件其他多值属性，如标签
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
    String attr;
    String imageURL;

    public Article(String id, String text, Date time, String textBy, String title, String description, int status, String attr) {
        super(id, time, text, textBy);
        this.status = ArticleStatus.conv(status);
        this.title = title;
        this.description = description;
        this.attr = attr;
    }

    public Article(String id, String text, Date time, String textBy, String title, String description, ArticleStatus status, String attr) {
        super(id, time, text, textBy);
        this.status = status;
        this.title = title;
        this.description = description;
        this.attr = attr;
    }

    public void setStatus(int status) {
        this.status = ArticleStatus.conv(status);
    }

    public void setStatus(ArticleStatus status) {
        this.status = status;
    }

    public void setStatus(String status) {
        this.status = ArticleStatus.conv(status);
    }

    public boolean checkStatusExpired() {
        return this.status.checkExpire(this.time);
    }
}
