package com.snach.literatureclub.bean;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseAuditor implements Serializable {
    String name;
    String id;
}
