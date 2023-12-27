package com.snach.literatureclub.dao;

import com.snach.literatureclub.bean.Comment;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
@Mapper
public interface CommentDao {
    /**
     * 根据评论id获取评论
     *
     * @param commentId 评论id
     * @return 该评论对象
     */
    @Select("SELECT id, text, time, text_by as textBy, text_on as textOn, reply FROM comments WHERE id = #{commentId}")
    Comment getComment(String commentId);

    /**
     * 根据文本id载入该文本附属所有评论列表
     *
     * @param textId 文本id
     * @return 该文本下的评论列表
     */
    @Select("SELECT id, text, time, text_by as textBy, text_on as textOn, reply FROM comments WHERE text_on = #{textId} ORDER BY time DESC")
    List<Comment> loadComment(String textId);

    @Select("SELECT id, text, time, text_by as textBy, text_on as textOn, reply FROM comments WHERE text_on = #{textId} AND reply = '' ORDER BY time DESC")
    List<Comment> loadRootComment(String textId);

    @Select("SELECT id, text, time, text_by as textBy, text_on as textOn, reply FROM comments WHERE text_on = #{textId} ORDER BY time DESC LIMIT #{startAt}, #{limit}")
    List<Comment> loadCommentLimit(String textId, int startAt, int limit);

    @Select("SELECT id, text, time, text_by as textBy, text_on as textOn, reply FROM comments WHERE text_on = #{textId} AND reply = '' ORDER BY time DESC LIMIT #{startAt}, #{limit}")
    List<Comment> loadRootCommentLimit(String textId, int startAt, int limit);

    @Select("SELECT count(1) FROM comments WHERE text_on = #{textId} AND reply = ''")
    int getRootCommentCount(String textId);

    @Select("SELECT count(1) FROM comments WHERE text_on = #{textId}")
    int getCommentCount(String textId);

    /**
     * 添加评论
     *
     * @param id 本评论id
     */
    @Insert("INSERT INTO comments(id, text, time, text_by, text_on, reply) values(#{id}, #{text}, #{time}, #{textBy}, #{textOn}, #{reply})")
    void insertComment(String id, String text, Date time, String textBy, String textOn, String reply);

    /**
     * 删除评论
     *
     * @param commentId 评论id
     */
    @Delete("DELETE FROM comments where id=#{commentId}")
    void deleteComment(String commentId);
}
