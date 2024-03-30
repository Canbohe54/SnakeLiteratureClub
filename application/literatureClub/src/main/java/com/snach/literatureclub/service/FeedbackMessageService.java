package com.snach.literatureclub.service;

import com.snach.literatureclub.bean.Message;
import com.snach.literatureclub.common.exception.InvalidTokenException;
import com.snach.literatureclub.dao.FeedbackMessageDao;
import com.snach.literatureclub.utils.IdManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.snach.literatureclub.utils.TokenTools.tokenVerify;

@Service
public interface FeedbackMessageService {
    // GET
    Message getMessage(String userId, String articleId);

    List<Message> getMessageListByArticleId(String articleId);

    List<Message> getMessageListByUserId(String userId);

    // SET
    boolean addMessage(String token, Message message);

    // DEL
    boolean deleteMessage(String token, String messageId);
}

@Service
class FeedbackMessageServiceImpl implements FeedbackMessageService {
    private final FeedbackMessageDao feedbackMessageDao;

    private final IdManager idManager;

    @Autowired
    public FeedbackMessageServiceImpl(FeedbackMessageDao feedbackMessageDao, IdManager idManager) {
        this.feedbackMessageDao = feedbackMessageDao;
        this.idManager = idManager;
    }

    @Override
    public Message getMessage(String userId, String articleId) {
        return feedbackMessageDao.getMessage(userId, articleId);
    }

    @Override
    public List<Message> getMessageListByArticleId(String articleId) {
        return feedbackMessageDao.getMessageByArticleId(articleId);
    }

    @Override
    public List<Message> getMessageListByUserId(String userId) {
        return feedbackMessageDao.getMessageByUserId(userId);
    }

    @Override
    public boolean addMessage(String token, Message message) {
        if (!tokenVerify(token, message.getFrom())) {
            throw new InvalidTokenException();
        }
        message.setId(idManager.generateMessageId());
        feedbackMessageDao.insertMessage(message);
        return true;
    }

    @Override
    public boolean deleteMessage(String token, String messageId) {
        Message message = feedbackMessageDao.getMessageById(messageId);
        if (!tokenVerify(token, message.getFrom())) {
            throw new InvalidTokenException();
        }
        feedbackMessageDao.deleteMessage(messageId);
        return true;
    }
}
