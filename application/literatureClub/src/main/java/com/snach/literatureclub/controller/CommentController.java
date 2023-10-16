package com.snach.literatureclub.controller;

import com.snach.literatureclub.bean.Comment;
import com.snach.literatureclub.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public Map<String, Object> loadComment(@RequestParam("id") String id, @RequestParam(value = "recursive", defaultValue = "false") Boolean recursive) {
        return commentService.loadComment(id, recursive);
    }
}
