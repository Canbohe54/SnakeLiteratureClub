package com.snach.literatureclub.controller;

import com.github.pagehelper.PageInfo;
import com.snach.literatureclub.bean.User;
import com.snach.literatureclub.service.UserService;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("usr")
public class UserController {
    @Autowired
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Account (Login, Register)
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public Map<String, Object> login(@RequestParam("id") String id, @RequestParam("password") String password) {
        String token = userService.login(id, password);
//        System.out.println("token: " + token);
        User user = userService.getUserBasicInfo(id);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userInfo", user);
//        System.out.println("测试");
        return response;
    }

    @RequestMapping(value = "reg", method = RequestMethod.POST)
    public Map<String, Object> register(User user) {
        userService.register(user);

        Map<String, Object> response = new HashMap<>();
        return response;
    }

    // User Info
    @RequestMapping(value = "getUserBasicInfo", method = RequestMethod.GET)
    public Map<String, Object> getUserBasicInfo(@RequestParam("user_id") @Nonnull String id) {
        User user = userService.getUserBasicInfo(id);

        Map<String, Object> response = new HashMap<>();
        response.put("user_info", user);
        return response;
    }

    @RequestMapping(value = "getUserBasicInfoByName", method = RequestMethod.GET)
    public PageInfo getUserBasicInfoByName(String name, @Nullable String identity, @RequestParam("page_num") int pageNum, @RequestParam("page_size") int pageSize){
        return userService.getUserBasicInfoByName(name, identity, pageNum, pageSize);
    }
    @RequestMapping(value = "updateUserInfo", method = RequestMethod.POST)
    public Map<String, Object> updateUserInfo(String token, User user) {
        userService.updateUserBasicInfo(token, user);

        Map<String, Object> response = new HashMap<>();
        return response;
    }
}
