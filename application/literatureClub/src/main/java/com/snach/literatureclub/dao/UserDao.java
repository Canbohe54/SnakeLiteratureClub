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

//    @Insert("INSERT INTO user(id, name, phone, password, identity, introduction, organization, pictureUrl, attrs) " +
//            "VALUES (#{user.id}, #{user.name}, #{user.phone}, #{user.password}, #{user.identity},  #{user.introduction}, #{user.organization}, #{user.pictureUrl}, #{user.attrs})")
//    void insertUser(@Param("user") User user);

    @Insert("INSERT INTO user(id, name, phone, password, identity) " +
            "VALUES (#{user.id}, #{user.name}, #{user.phone}, #{user.password}, #{user.identity})")
    void insertUser(@Param("user") User user);

    // User Info
    @Select("SELECT * FROM user WHERE id = #{id}")
    User getUserById(String id);

    @Select("SELECT * FROM user WHERE name LIKE '%${name}%'")
    List<User> getUserByName(String name);

//    @Select("SELECT * FROM user WHERE identity = #{identity} AND name LIKE '%${name}%'")
    @Select({"<script>",
            "SELECT ",
            "* ",
            "FROM user u LEFT JOIN contributor ct ON u.id=ct.id ",
            "LEFT JOIN teacher t ON u.id=t.id ",
            "LEFT JOIN expert e ON u.id=e.id ",
            "LEFT JOIN hunter h ON u.id=h.id ",
            "LEFT JOIN auditor a ON u.id=a.id ",
            "WHERE user.name LIKE '%${name}%' AND user.identity in",
            "<foreach collection='items' item='item' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"
    })
    List<User> getUserByNameAndIdentity(String name,@Param("items") List<String> identity);

    @Select("SELECT identity FROM user WHERE id = #{id}")
    String getUserIdentity(String id);

    @Update("UPDATE user SET name = #{user.name}, phone = #{user.phone} WHERE id = #{user.id}")
    void updateUserInfo(@Param("user") User user);

    @Update("UPDATE user SET password = #{newPassword} WHERE id = #{id}")
    void updateUserPassword(String id, String newPassword);
}
