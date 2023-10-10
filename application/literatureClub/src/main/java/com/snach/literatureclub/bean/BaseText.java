package com.snach.literatureclub.bean;

import lombok.*;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseText implements Serializable {
    @Id
    BigInteger id;
    String text;
    Date time;
    String textBy;
}
