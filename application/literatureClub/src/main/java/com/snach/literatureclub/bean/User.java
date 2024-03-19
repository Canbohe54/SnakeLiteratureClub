package com.snach.literatureclub.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.snach.literatureclub.bean.attribute.UserAttribute;
import com.snach.literatureclub.bean.attribute.UserAttributeFactory;
import com.snach.literatureclub.common.Identity;
import lombok.*;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * name 用户名
 * id 主键
 * phone 电话
 * email 邮箱
 * password 密码
 * group 身份
 * introduction 个人简介
 * organization 所属机构
 * pictureUrl 头像链接
 * attr 年级等额外信息
 */
@Data
@NoArgsConstructor
public class User implements Serializable {
    // Login Info
    String id;
    String phone;
    @JsonIgnore String password;
    Identity identity;

    // Personal Info
    String name;
    String introduction;
    String organization;
    String pictureUrl;

    // Extra Info
    @JsonIgnore String attrs;
    UserAttribute transformedAttrs;

    public User(String id, String phone, String password, String identity, String name, String introduction, String organization, String pictureUrl, String attrs) {
        this.id = id;
        this.phone = phone;
        this.password = password;
        this.name = name;
        this.introduction = introduction;
        this.organization = organization;
        this.pictureUrl = pictureUrl;
        this.attrs = attrs;
    }

    public UserAttribute getTransformedAttrs() {
        if (this.transformedAttrs == null) {
            this.transformedAttrs = UserAttributeFactory.load(this.identity, this.attrs);
        }
        return this.transformedAttrs;
    }

    public boolean checkIdentity(Identity identity) {
        return this.identity == identity;
    }

    public Map<String, Object> safeGetUserInfo() {
        Map<String, Object> userInfo = new HashMap<>();
        for (Field field : User.class.getDeclaredFields()) {
            if (!field.getName().equalsIgnoreCase("password")) {
                try {
                    field.setAccessible(true);
                    userInfo.put(field.getName(), field.get(this));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return userInfo;
    }
}
