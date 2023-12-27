package com.snach.literatureclub.service;

import com.snach.literatureclub.bean.Comment;
import com.snach.literatureclub.dao.CommentDao;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.snach.literatureclub.utils.IdTools.*;
import static com.snach.literatureclub.utils.TokenTools.*;

@Service
public interface CommentService {
    /**
     * 根据稿件id载入稿件下评论
     *
     * @param articleId 稿件id
     * @param recursive 是否使用递归载入评论（即包含子评论）
     * <p>
     * 关于`根评论`及`子评论`:
     * @see Comment
     */
    Map<String, Object> loadComment(String articleId, Boolean recursive, int startAt, int limit);

    /**
     * 添加评论
     *
     * @param token 用户token
     * @param text 评论文本
     * <p>--------------</p>
     * param `textOn` and `reply`:
     * @see Comment
     */
    Map<String, Object> addComment(String token, String text, String textOn, String reply);

    /**
     * 删除评论
     *
     * @param commentId 评论id
     */
    Map<String, Object> deleteComment(String token, String commentId);
}

@Service
@Mapper
class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentDao commentDao;

    private static final Calendar calendar = Calendar.getInstance();

    @Override
    public Map<String, Object> loadComment(String articleId, Boolean recursive, int startAt, int limit) {
        Map<String, Object> response = new HashMap<>();
        if (idType(articleId) != Type.ARTICLE) {
            response.put("statusMsg", "Invalid id.");
            response.put("res", "{}");
            return response;
        }
        List<Object> resultList = new ArrayList<>();
        List<Comment> onArticle = commentDao.loadRootCommentLimit(articleId, startAt, limit);
        int rootCommentCount = commentDao.getRootCommentCount(articleId);
        resultList.add(onArticle);
        if (recursive) {
            List<List<Comment>> onComment = new ArrayList<>();
            for (Comment rootComment : onArticle) {
                onComment.add(commentDao.loadComment(rootComment.getId()));
            }
            resultList.add(onComment);
        }
        response.put("remainder", rootCommentCount - startAt - limit + 1);
        response.put("statusMsg", "Success.");
        response.put("res", resultList);
        return response;
    }

    @Override
    public Map<String, Object> addComment(String token, String text, String textOn, String reply) {
        Map<String, Object> response = new HashMap<>();
        if (!tokenVerify(token)) {
            response.put("statusMsg", "Invalid token.");
            return response;
        }
        if (reply == null) {
            reply = "";
        }
        commentDao.insertComment(generateId(Type.COMMENT), text, calendar.getTime(), getPayload(token, "id"), textOn, reply);
        response.put("statusMsg", "Success.");
        return response;
    }

    @Override
    public Map<String, Object> deleteComment(String token, String commentId) {
        Map<String, Object> response = new HashMap<>();
        if (!tokenVerify(token)) {
            response.put("statusMsg", "Invalid token.");
            return response;
        }
        commentDao.deleteComment(commentId);
        response.put("statusMsg", "Success.");
        return response;
    }
}
