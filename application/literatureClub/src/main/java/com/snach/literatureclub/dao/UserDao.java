package com.snach.literatureclub.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
@Mapper
public interface UserDao {

    @Select("SELECT COUNT(*) FROM group WHERE id = #{id} AND user_id = #{user_id}")
    int exist(@Param("id") BigInteger id, @Param("user_id") String user_id);
}
