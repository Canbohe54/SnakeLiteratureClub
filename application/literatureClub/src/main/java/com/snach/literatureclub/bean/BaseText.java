package com.snach.literatureclub.bean;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseText implements Serializable {
    String text;
    String textId;
    Date time;
    String textBy;
}
