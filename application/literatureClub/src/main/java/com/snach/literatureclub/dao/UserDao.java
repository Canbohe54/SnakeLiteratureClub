package com.snach.literatureclub.dao;

import com.snach.literatureclub.bean.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface UserDao {
    // Account
    @Select("SELECT id FROM user WHERE id = #{id} AND password = #{password}")
    String loginById(String id, String password);

    @Select("SELECT id FROM user WHERE phone = #{phone} AND password = #{password}")
    String loginByPhone(String phone, String password);

    @Insert("INSERT INTO user(id, name, phone, password, `group`, introduction, organization, pictureUrl, attrs) " +
            "VALUES (#{user.id}, #{user.name}, #{user.phone}, #{user.password}, #{user.group}, #{user.introduction}, #{user.organization}, #{user.pictureUrl}, #{user.attrs})")
    void insertUser(@Param("user") User user);

    // User Info
    @Select("SELECT * FROM user WHERE id = #{id}")
    User getUserById(String id);

    @Select("SELECT * FROM user WHERE name LIKE '%${name}%'")
    List<User> getUserByName(String name);

    @Select("SELECT * FROM user WHERE identity = #{identity} AND name LIKE '%${name}%'")
    List<User> getUserByNameAndIdentity(String name, String identity);

    @Insert("UPDATE user SET name = #{user.name}, phone = #{user.phone}, introduction = #{user.introduction}, organization = #{user.organization}, attrs = #{user.attrs} WHERE id = #{user.id}")
    void updateUserInfo(@Param("user") User user);
}
