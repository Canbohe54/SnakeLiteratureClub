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

    @Select("SELECT user.id FROM user WHERE user.email = #{email}")
    String getIdByEmail(String email);

    @Select("SELECT * FROM user WHERE name LIKE '%${keyword}%'")
    List<User> getUsersByKeyword(String keyword);

    @Insert("INSERT INTO user(id, name, phone, email, password, `group`, organization, attr) " +
            "VALUES(#{user.id}, #{user.name}, #{user.phone}, #{user.email}, #{user.password}, #{user.group}, #{user.organization}, #{user.attr})")
    void insertUser(@Param("user") User user);

    @Select("select count(email) from user where email = #{email}")
    int existEmail(@Param("email") String email);

    @Select("select * from user where email = #{email} and password = #{password}")
    User login(@Param("email") String email, @Param("password") String password);

    @Select("SELECT COUNT(*) FROM user WHERE id = #{user_id} AND password = #{password}")
    int verifyPasswd(@Param("user_id") String userId, @Param("password") String password);

    @Update("UPDATE user SET name=#{user.name}, phone=#{user.phone},introdution=#{user.introduction}, organization=#{user.organization},attr=#{user.attr},pictureUrl=#{user.pictureUrl} WHERE id=#{user.id};")
    void updateUserInfo(@Param("user") User user);

    @Select("SELECT COUNT(*) FROM follow WHERE follow_user_id = #{follow_user_id} AND followed_user_id = #{followed_user_id}")
    int isFollowedByUID(@Param("follow_user_id") String followUserId, @Param("followed_user_id") String followedUserId);

    @Delete("DELETE FROM follow WHERE follow_user_id = #{follow_user_id} AND followed_user_id = #{followed_user_id}")
    void unFollow(@Param("follow_user_id") String followUserId, @Param("followed_user_id") String followedUserId);

    @Insert("INSERT INTO follow(follow_user_id, followed_user_id) VALUES (#{follow_user_id},#{followed_user_id})")
    void follow(@Param("follow_user_id") String followUserId, @Param("followed_user_id") String followedUserId);

    @Select({"<script>",
            "SELECT ",
            "fo.followed_user_id ",
            "FROM follow fo left join user u on fo.followed_user_id = u.id ",
            "WHERE fo.follow_user_id=#{userId} AND u.group in",
            "<foreach collection='items' item='item' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"
    })
    List<String> getAllFollowed(String userId, @Param("items") List<String> targetIdentity);

    @Select({"<script>",
            "SELECT ",
            "fo.follow_user_id ",
            "FROM follow fo left join user u on fo.follow_user_id = u.id ",
            "WHERE fo.followed_user_id=#{userId} AND u.group in",
            "<foreach collection='items' item='item' open='(' separator=',' close=')'>",
            "#{item}",
            "</foreach>",
            "</script>"
    })
    List<String> getAllFans(String userId, @Param("items") List<String> targetIdentity);

    @Select({"<script>",
            "SELECT ",
            "COUNT(*) ",
            "FROM follow fo left join user u on fo.follow_user_id = u.id ",
            "WHERE fo.followed_user_id=#{contributor_id}",
            "</script>"
    })
    int getFansNumById(@Param("contributor_id") String contributor_id);

    @Select({"<script>",
            "SELECT ",
            "COUNT(*) ",
            "FROM follow fo left join user u on fo.followed_user_id = u.id ",
            "WHERE fo.follow_user_id=#{contributor_id}",
            "</script>"
    })
    int getFollowNumById(@Param("contributor_id") String contributor_id);

    @Delete("DELETE FROM user WHERE id=#{id}")
    void eraseUser(@Param("id") String userId);

    @Update("UPDATE user SET user.password = #{newPassword} WHERE user.id = #{userId}")
    void changePassword(String userId, String newPassword);

    @Update("UPDATE user SET user.email = #{newEmail} WHERE user.id = #{userId}")
    void changeEmail(String userId, String newEmail);
}
