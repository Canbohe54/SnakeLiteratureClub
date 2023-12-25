package com.snach.literatureclub.dao;

import com.snach.literatureclub.bean.User;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
@Mapper
public interface UserDao {
    @Select("SELECT COUNT(*) FROM user WHERE `group` = #{group} AND user_id = #{user_id}")
    int exist(@Param("group") BigInteger group, @Param("user_id") String user_id);

    @Select("SELECT * FROM user WHERE id = #{id}")
    User getUserById(String id);

    @Select("SELECT * FROM user WHERE name LIKE '%${keyword}%'")
    List<User> getUsersByKeyword(String keyword);

    @Insert("INSERT INTO user(id, name, phone, email, password, `group`, organization, attr) " +
            "VALUES(#{user.id}, #{user.name}, #{user.phone}, #{user.email}, #{user.password}, #{user.group}, #{user.organization}, #{user.attr})")
    void insertUser(@Param("user") User user);

    @Select("select count(email) from user where email = #{email}")
    int existEmail(@Param("email") String email);

    @Select("select * from user where email = #{email} and password = #{password}")
    User login(@Param("email") String email, @Param("password") String password);

    @Update("UPDATE user SET name=#{user.name}, phone=#{user.phone}, email=#{user.email}, password=#{user.password}, `group`=#{user.group}, organization=#{user.organization},attr=#{user.attr} WHERE id=#{user.id};")
    void updateUserInfo(@Param("user") User user);
}
