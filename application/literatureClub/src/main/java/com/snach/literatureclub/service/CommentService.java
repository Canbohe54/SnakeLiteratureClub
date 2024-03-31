package com.snach.literatureclub.service;

import com.snach.literatureclub.bean.Comment;
import com.snach.literatureclub.common.exception.InvalidTokenException;
import com.snach.literatureclub.dao.CommentDao;
import com.snach.literatureclub.utils.IdManager;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.snach.literatureclub.utils.TokenTools.getPayload;
import static com.snach.literatureclub.utils.TokenTools.tokenVerify;

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
    List<?> loadComment(String articleId, Boolean recursive, int startAt, int limit);

    int getRootCommentCount(String articleId);

    /**
     * 添加评论
     *
     * @param token 用户token
     * @param text  评论文本
     * <p>--------------</p>
     * param `textOn` and `reply`:
     * @see Comment
     */
    boolean addComment(String token, String text, String textOn, String reply);

    /**
     * 删除评论
     *
     * @param commentId 评论id
     */
    boolean deleteComment(String token, String commentId);
}

@Transactional(rollbackFor = Exception.class)
@Service
@Mapper
class CommentServiceImpl implements CommentService {
    private final CommentDao commentDao;

    private final IdManager idManager;

    @Autowired
    public CommentServiceImpl(CommentDao commentDao, IdManager manager) {
        this.commentDao = commentDao;
        this.idManager = manager;
    }

    @Override
    public List<?> loadComment(String articleId, Boolean recursive, int startAt, int limit) {
        List<Object> resultList = new ArrayList<>();
        List<Comment> onArticle = commentDao.loadRootCommentLimit(articleId, startAt, limit);
        resultList.add(onArticle);
        if (recursive) {
            List<List<Comment>> onComment = new ArrayList<>();
            for (Comment rootComment : onArticle) {
                onComment.add(commentDao.loadComment(rootComment.getId()));
            }
            resultList.add(onComment);
        }
        return resultList;
    }

    @Override
    public int getRootCommentCount(String articleId) {
        return commentDao.getRootCommentCount(articleId);
    }

    @Override
    public boolean addComment(String token, String text, String textOn, String reply) {
        if (!tokenVerify(token)) {
            throw new InvalidTokenException();
        }
        if (reply == null) {
            reply = "";
        }
        Calendar calendar = Calendar.getInstance();
        commentDao.insertComment(idManager.generateCommentId(), text, calendar.getTime(), getPayload(token, "id"), textOn, reply);
        return true;
    }

    @Override
    public boolean deleteComment(String token, String commentId) {
        if (!tokenVerify(token)) {
            throw new InvalidTokenException();
        }
        commentDao.deleteComment(commentId);
        return true;
    }
}
