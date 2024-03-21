package com.snach.literatureclub.bean;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.snach.literatureclub.utils.IdTools;
import lombok.*;
import org.springframework.data.annotation.Id;
import java.io.Serializable;
import java.util.Date;

import static com.snach.literatureclub.utils.IdTools.generateId;

/**
 * id       主键
 * text     文本内容
 * time     最近修改时间
 * textBy   文本作者
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Text implements Serializable {
    @Id
    String id;
    Date time;
    @JsonProperty("text_by")
    String textBy;
    String text; //正文

    public Text(String textBy, String text) {
        this.id = generateId(IdTools.Type.ARTICLE);
        this.time = new Date(System.currentTimeMillis());
        this.text = text;
        this.textBy = textBy;
    }
}
