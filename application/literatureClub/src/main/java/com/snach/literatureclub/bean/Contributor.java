package com.snach.literatureclub.bean;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Contributor extends BaseUser implements Serializable {
    String grade;
}
