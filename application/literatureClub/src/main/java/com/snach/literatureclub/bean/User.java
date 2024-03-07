package com.snach.literatureclub.bean;

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
    String password;
    Identity group;

    // Personal Info
    String name;
    String introduction;
    String organization;
    String pictureUrl;

    // Extra Info
    UserAttribute attrs;

    public User(String id, String phone, String password, String group, String name, String introduction, String organization, String pictureUrl, String attrs) {
        this.id = id;
        this.phone = phone;
        this.password = password;
        this.group = Identity.conv(group);
        this.name = name;
        this.introduction = introduction;
        this.organization = organization;
        this.pictureUrl = pictureUrl;
        this.attrs = UserAttributeFactory.load(this.group, attrs);
    }

    public void setGroup(String group) {
        this.group = Identity.conv(group);
    }

    public boolean checkIdentity(Identity identity) {
        return this.group == identity;
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
