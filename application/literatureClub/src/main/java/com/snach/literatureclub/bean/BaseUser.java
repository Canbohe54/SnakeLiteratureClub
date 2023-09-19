package com.snach.literatureclub.bean;

import lombok.*;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseUser implements Serializable {
    String name;
    String id;
    String phone;
    String email;
    String introduction;
    String organization;
    String pictureUrl;
}
