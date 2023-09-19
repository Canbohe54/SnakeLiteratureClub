package com.snach.literatureclub.controller;

import com.snach.literatureclub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("usr")
public class UserController {
    @Autowired
    private UserService userService;
}
