package com.snach.literatureclub.bean;

import lombok.*;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@Deprecated
public class Expert extends User implements Serializable {
    public Expert(String name, String id, String phone, String email, String introduction, String organization, String pictureUrl) {
        super(name, id, phone, email, introduction, organization, pictureUrl);
    }
}
