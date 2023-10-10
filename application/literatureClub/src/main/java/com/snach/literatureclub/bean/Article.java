package com.snach.literatureclub.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;

import java.io.Serializable;
import java.math.BigInteger;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Article extends BaseText implements Serializable {
    String title;
    String description;
}
