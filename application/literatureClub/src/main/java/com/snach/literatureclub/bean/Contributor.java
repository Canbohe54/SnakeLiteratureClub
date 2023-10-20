package com.snach.literatureclub.bean;

import lombok.*;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@Deprecated
public class Contributor extends User implements Serializable {
    String grade;

    public Contributor(String name, String id, String phone, String email, String introduction, String organization, String pictureUrl, String attr, String grade) {
        super(name, id, phone, email, introduction, organization, pictureUrl, attr);
        this.grade = grade;
    }
}
