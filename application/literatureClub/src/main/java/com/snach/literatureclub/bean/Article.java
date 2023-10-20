package com.snach.literatureclub.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
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
