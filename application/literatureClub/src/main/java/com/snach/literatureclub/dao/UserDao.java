package com.snach.literatureclub.dao;

import com.snach.literatureclub.bean.Article;
import com.snach.literatureclub.bean.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
@Mapper
public interface UserDao {
    @Select("SELECT COUNT(*) FROM group WHERE id = #{id} AND user_id = #{user_id}")
    int exist(@Param("id") BigInteger id, @Param("user_id") String user_id);

    @Select("SELECT * FROM user WHERE id = #{id}")
    User getUserById(String id);

    @Select("SELECT * FROM user WHERE name LIKE '%${keyword}%'")
    List<User> getUsersByKeyword(String keyword);

    @Insert("INSERT INTO user(id, name, phone, email, password, `group`, organization) " +
            "VALUES(#{user.id}, #{user.name}, #{user.phone}, #{user.email}, #{user.password}, #{user.group}, #{user.organization})")
    void insertUser(@Param("user") User user);

    @Select("select count(email) from user where email = #{email}")
    int existEmail(@Param("email") String email);

    @Select("select * from user where email = #{email} and password = #{password}")
    User login(@Param("email") String email, @Param("password") String password);
}
