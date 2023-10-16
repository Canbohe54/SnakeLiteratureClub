package com.snach.literatureclub.dao;

import com.snach.literatureclub.bean.Comment;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
@Mapper
public interface CommentDao {
    /**
     * 根据文本id载入该文本附属评论列表
     *
     * @param textId 文本id
     * @return 该文本下的评论列表
     */
    @Select("SELECT * FROM comments WHERE text_id = #{textId}")
    List<Comment> loadComment(@Param("textId") String textId);

    /**
     * 添加评论
     *
     * @param id 本评论id
     */
    @Insert("INSERT INTO comments()")
    void insertComment(String id, String text, Date date, String textBy, String textOn, String reply);

    /**
     * 删除评论
     *
     * @param commentId 评论id
     */
    void deleteComment(String commentId);
}
