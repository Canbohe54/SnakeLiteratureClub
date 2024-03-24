package com.snach.literatureclub.controller;

import com.snach.literatureclub.common.annotation.ResponseNotIntercept;
import com.snach.literatureclub.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping(value = "comm", method = RequestMethod.POST)
public class CommentController {
    private final CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @RequestMapping(value = "load", method = RequestMethod.POST)
    public List<?> loadComment(@RequestParam("id") String id,
                               @RequestParam(value = "recursive", defaultValue = "false") Boolean recursive,
                               @RequestParam(value = "startAt", defaultValue = "0") int startAt,
                               @RequestParam(value = "limit", defaultValue = "10") int limit) {
        return commentService.loadComment(id, recursive, startAt, limit);
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    public boolean addComment(@RequestParam("token") String token,
                              @RequestParam("text") String text,
                              @RequestParam("textOn") String textOn,
                              @RequestParam(value = "reply", required = false) String reply) {
        return commentService.addComment(token, text, textOn, reply);
    }

    @RequestMapping(value = "del", method = RequestMethod.POST)
    public boolean deleteComment(@RequestParam("token") String token,
                                 @RequestParam("id") String id) {
        return commentService.deleteComment(token, id);
    }
}
