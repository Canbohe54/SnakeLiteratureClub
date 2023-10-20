package com.snach.literatureclub.bean;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class BaseText implements Serializable {
    @Id
    String id;
    String text;
    Date time;
    String textBy;
}
