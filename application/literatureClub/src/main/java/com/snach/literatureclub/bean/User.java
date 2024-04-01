package com.snach.literatureclub.bean;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.snach.literatureclub.bean.attribute.UserAttribute;
import com.snach.literatureclub.bean.attribute.UserAttributeFactory;
import com.snach.literatureclub.common.Identity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@AllArgsConstructor
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
    @JsonProperty("attrs") UserAttribute userAttribute;

    public UserAttribute getUserAttribute() {
        if (this.userAttribute == null) {
            this.userAttribute = UserAttributeFactory.load(this.identity, this.attrs);
        }
        return this.userAttribute;
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
