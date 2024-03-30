package com.snach.literatureclub.dao;

import com.snach.literatureclub.bean.Message;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface FeedbackMessageDao {
    // SELECT
    @Select("SELECT id, `from`, message, `to` FROM message WHERE `from`=#{from} AND `to`=#{to}")
    Message getMessage(String from, String to);

    @Select("SELECT id, `from`, message, `to` FROM message WHERE `to`=#{to}")
    List<Message> getMessageByUserId(String to);

    @Select("SELECT id, `from`, message, `to` FROM message WHERE `from`=#{from}")
    List<Message> getMessageByArticleId(String from);

    @Select("SELECT id, `from`, message, `to` FROM message WHERE id=#{id}")
    Message getMessageById(String id);

    // INSERT
    @Insert("INSERT INTO message(id, `from`, message, `to`) VALUES (#{m.id}, #{m.from}, #{m.message}, #{m.to})")
    void insertMessage(@Param("m") Message message);

    // DELETE
    @Delete("DELETE FROM message WHERE id=#{id}")
    void deleteMessage(String id);
}
