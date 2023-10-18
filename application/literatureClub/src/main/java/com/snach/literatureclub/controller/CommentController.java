package com.snach.literatureclub.controller;

import com.snach.literatureclub.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping(value = "comm", method = RequestMethod.POST)
public class CommentController {
    @Autowired
    private CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @RequestMapping(value = "load", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> loadComment(@RequestParam("id") String id,
                                           @RequestParam(value = "recursive", defaultValue = "false") Boolean recursive) {
        return commentService.loadComment(id, recursive);
    }

    @RequestMapping(value = "add", method = RequestMethod.POST)
    @ResponseBody
    public void addComment(@RequestParam("token") String token,
                           @RequestParam("text") String text,
                           @RequestParam("textOn") String textOn,
                           @RequestParam(value = "reply", required = false) String reply) {
        commentService.addComment(token, text, textOn, reply);
    }

    @RequestMapping(value = "del", method = RequestMethod.POST)
    @ResponseBody
    public void deleteComment(@RequestParam("token") String token,
                              @RequestParam("id") String id) {
        commentService.deleteComment(token, id);
    }
}
