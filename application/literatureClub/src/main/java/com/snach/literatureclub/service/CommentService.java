package com.snach.literatureclub.service;

import com.snach.literatureclub.bean.Comment;
import com.snach.literatureclub.dao.CommentDao;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.snach.literatureclub.utils.Tools.*;

@Service
public interface CommentService {
    /**
     * 根据稿件id载入稿件下评论
     *
     * @param articleId 稿件id
     * @param recursive 是否使用递归载入评论（即包含子评论）
     * @return
     * <p>
     * 关于`根评论`及`子评论`:
     * @see Comment
     */
    Map<String, Object> loadComment(String articleId, Boolean recursive);

    /**
     * 添加评论
     *
     * @param commentTo 稿件id或评论id
     */
    Map<String, Object> addComment(String commentTo);

    /**
     * 删除评论
     *
     * @param commentId 评论id
     * @param reclusive 是否递归删除所有评论，默认false
     */
    void deleteComment(String commentId, Boolean reclusive);
}

@Service
@Mapper
class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentDao commentDao;

    @Override
    public Map<String, Object> loadComment(String articleId, Boolean recursive) {
        Map<String, Object> response = new HashMap<>();
        if (idType(articleId) == Type.ARTICLE) {
            response.put("statusMsg", "Invalid id.");
            response.put("res", "{}");
            return response;
        }
        List<Object> resultList = new ArrayList<>();
        List<Comment> onArticle = commentDao.loadComment(articleId);
        resultList.add(onArticle);
        if (recursive) {
            List<List<Comment>> onComment = new ArrayList<>();
            for (Comment rootComment : onArticle) {
                onComment.add(commentDao.loadComment(rootComment.getId()));
            }
            resultList.add(onComment);
        }
        response.put("statusMsg", "Success.");
        response.put("res", resultList);
        return response;
    }

    @Override
    public Map<String, Object> addComment(String commentTo) {
        Map<String, Object> response = new HashMap<>();
//        commentDao.insertComment(commentTo);
        return response;
    }

    @Override
    public void deleteComment(String commentId, Boolean reclusive) {
        commentDao.deleteComment(commentId);
    }
}
