package com.snach.literatureclub.service;

import com.snach.literatureclub.bean.Message;
import com.snach.literatureclub.common.exception.InsufficientPermissionException;
import com.snach.literatureclub.common.exception.InvalidTokenException;
import com.snach.literatureclub.dao.FeedBackMessageDao;
import com.snach.literatureclub.utils.IdManager;
import com.snach.literatureclub.utils.TokenTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.snach.literatureclub.utils.TokenTools.getPayload;

@Service
public interface FeedbackMessageService {
    // GET
    Message getMessage(String userId, String articleId);

    List<Message> getMessageListByArticleId(String articleId);

    List<Message> getMessageListByUserId(String userId);

    // SET
    boolean addMessage(Message message);

    // DEL
    boolean deleteMessage(String token, String messageId);
}

@Service
class FeedbackMessageServiceImpl implements FeedbackMessageService {
    private final FeedBackMessageDao feedBackMessageDao;

    private final IdManager idManager;

    @Autowired
    public FeedbackMessageServiceImpl(FeedBackMessageDao feedBackMessageDao, IdManager idManager) {
        this.feedBackMessageDao = feedBackMessageDao;
        this.idManager = idManager;
    }

    @Override
    public Message getMessage(String userId, String articleId) {
        return feedBackMessageDao.getMessage(userId, articleId);
    }

    @Override
    public List<Message> getMessageListByArticleId(String articleId) {
        return feedBackMessageDao.getMessageByArticleId(articleId);
    }

    @Override
    public List<Message> getMessageListByUserId(String userId) {
        return feedBackMessageDao.getMessageByUserId(userId);
    }

    @Override
    public boolean addMessage(Message message) {
        message.setId(idManager.generateMessageId());
        feedBackMessageDao.insertMessage(message);
        return true;
    }

    @Override
    public boolean deleteMessage(String token, String messageId) {
        if (!TokenTools.tokenVerify(token)) {
            throw new InvalidTokenException();
        }
        String userId = getPayload(token, "id");
        Message message = feedBackMessageDao.getMessageById(messageId);
        if (!userId.equals(message.getFrom())) {
            throw new InsufficientPermissionException();
        }
        feedBackMessageDao.deleteMessage(messageId);
        return true;
    }
}
