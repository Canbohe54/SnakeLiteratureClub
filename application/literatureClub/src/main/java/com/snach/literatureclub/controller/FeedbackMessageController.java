package com.snach.literatureclub.controller;

import com.snach.literatureclub.service.FeedbackMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping(value = "message", method = RequestMethod.GET)
public class FeedbackMessageController {
    private final FeedbackMessageService feedbackMessageService;

    @Autowired
    public FeedbackMessageController(FeedbackMessageService feedbackMessageService) {
        this.feedbackMessageService = feedbackMessageService;
    }


}
