package com.snach.literatureclub.bean;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.annotation.Id;
import java.io.Serializable;
import java.util.Date;

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
public class BaseText implements Serializable {
    @Id
    String id;
    String text;
    Date time;
    @JsonProperty("text_by")
    String textBy;
}
