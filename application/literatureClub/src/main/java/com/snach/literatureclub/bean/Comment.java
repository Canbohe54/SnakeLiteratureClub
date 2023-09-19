package com.snach.literatureclub.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Comment extends BaseText implements Serializable {
    String textTo;
}
