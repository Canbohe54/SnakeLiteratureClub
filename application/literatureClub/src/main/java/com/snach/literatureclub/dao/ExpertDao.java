package com.snach.literatureclub.dao;

import com.snach.literatureclub.bean.Expert;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
@Mapper
public interface ExpertDao {
    @Insert("INSERT INTO expert_info(id, username) VALUES(#{expert.id}, #{expert.username})")
    void insert(@Param("expert") Expert expert);

    @Select("SELECT * FROM expert_info WHERE id = #{id}")
    Expert getById(@Param("id") BigInteger id);

    @Update("UPDATE expert_info SET username = #{expert.username} WHERE id = #{expert.id}")
    void updateById(@Param("expert") Expert expert);

    @Delete("DELETE FROM expert_info WHERE id = #{id}")
    void deleteById(@Param("id") BigInteger id);
}
