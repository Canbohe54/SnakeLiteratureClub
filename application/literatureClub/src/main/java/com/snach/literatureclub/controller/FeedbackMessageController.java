package com.snach.literatureclub.controller;

import com.snach.literatureclub.bean.Message;
import com.snach.literatureclub.service.FeedbackMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping(value = "message", method = RequestMethod.GET)
public class FeedbackMessageController {
    private final FeedbackMessageService feedbackMessageService;

    @Autowired
    public FeedbackMessageController(FeedbackMessageService feedbackMessageService) {
        this.feedbackMessageService = feedbackMessageService;
    }

    @RequestMapping(value = "getMessage", method = RequestMethod.GET)
    public Message getMessage(String userId, String articleId) {
        return feedbackMessageService.getMessage(userId, articleId);
    }
    @RequestMapping(value = "getMessageListByArticleId", method = RequestMethod.GET)
    public List<Message> getMessageListByArticleId(String articleId) {
        return feedbackMessageService.getMessageListByArticleId(articleId);
    }
    @RequestMapping(value = "getMessageListByUserId", method = RequestMethod.GET)
    public List<Message> getMessageListByUserId(String userId) {
        return feedbackMessageService.getMessageListByUserId(userId);
    }
    @RequestMapping(value = "addMessage", method = RequestMethod.POST)
    public boolean addMessage(Message message) {
        return feedbackMessageService.addMessage(message);
    }
    @RequestMapping(value = "deleteMessage", method = RequestMethod.POST)
    public boolean deleteMessage(String token, String messageId) {
        return feedbackMessageService.deleteMessage(token, messageId);
    }
}
