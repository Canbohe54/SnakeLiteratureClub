package com.snach.literatureclub.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
public class Article extends BaseText implements Serializable {
    String title;
    String description;
    int status;
    public Article(String id, String text, Date time,String textBy, String title, String description,int status){
        super(id,text,time,textBy);
        this.status = status;
        this.title = title;
        this.description = description;
    }
}
