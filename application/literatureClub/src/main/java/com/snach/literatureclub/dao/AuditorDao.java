package com.snach.literatureclub.dao;

import com.snach.literatureclub.bean.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.*;

import java.util.List;
@Repository
@Mapper
public interface AuditorDao {
    @Insert("INSERT INTO auditor(id, introduction, organization, pictureUrl) " +
            "VALUES (#{user.id},  #{user.introduction}, #{user.organization}, #{user.pictureUrl})")
    void insertUser(@Param("user") User user);
    @Update("UPDATE auditor SET introduction = #{user.introduction}, organization = #{user.organization}, WHERE id = #{user.id}")
    void updateUserInfo(@Param("user") User user);
    @Select("SELECT * FROM auditor NATURAL JOIN user WHERE id = #{id}")
    User getUserById(String id);
    @Select("SELECT * FROM auditor NATURAL JOIN user WHERE name LIKE '%${name}%'")
    List<User> getUserByName(String name);
}