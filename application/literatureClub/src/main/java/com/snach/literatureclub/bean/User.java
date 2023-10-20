package com.snach.literatureclub.bean;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class User implements Serializable {
    String name;
    String id;
    String phone;
    String email;
    String introduction;
    String organization;
    String pictureUrl;
    String attr;

    public User(String name, String id, String phone, String email, String introduction, String organization, String pictureUrl) {
        this.name = name;
        this.id = id;
        this.phone = phone;
        this.email = email;
        this.introduction = introduction;
        this.organization = organization;
        this.pictureUrl = pictureUrl;
    }
}
