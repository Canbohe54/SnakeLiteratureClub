package com.snach.literatureclub.bean;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

/**
 * title        稿件标题
 * description  稿件描述
 * status       稿件状态 1：保存成功 2：待审核 3.已发布 4.未通过 0：保存失败
 * attr         稿件其他多值属性，如标签
 *
 * @see BaseText
 */
@Data
@EqualsAndHashCode(callSuper = true)
// 不加无参构造函数会出现数据库查询数据为null导致出现java.lang.IllegalArgumentException: null 错误
@NoArgsConstructor
@ToString
public class Article extends BaseText implements Serializable {
    String title;
    String description;
    int status;
    String attr;  // 标签

    public Article(String id, String text, Date time, String textBy, String title, String description, int status, String attr) {
        super(id, text, time, textBy);
        this.status = status;
        this.title = title;
        this.description = description;
        this.attr = attr;
    }
}
