package com.snach.literatureclub.controller;

import com.github.pagehelper.PageInfo;
import com.snach.literatureclub.bean.User;
import com.snach.literatureclub.service.UserService;
import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("usr")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Account (Login, Register)
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public Map<String, Object> login(@RequestParam("id") String id, @RequestParam("password") String password) {
        String token = userService.login(id, password);
        User user = userService.getUserBasicInfo(id);

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("userInfo", user);
        return response;
    }

    @RequestMapping(value = "reg", method = RequestMethod.POST)
    public boolean register(User user) {
        return userService.register(user);
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
    public PageInfo<User> getUserBasicInfoByName(String name,
                                                 @Nullable @RequestParam(name = "identity", required = false) List<String> identity,
                                                 @RequestParam("page_num") int pageNum,
                                                 @RequestParam("page_size") int pageSize) {
        return userService.getUserBasicInfoByName(name, identity, pageNum, pageSize);
    }

    @RequestMapping(value = "getUserBasicInfoByNameNoPagination",method = RequestMethod.GET)
    public List<User> getUserBasicInfoByNameNoPagination(String name, @Nullable @RequestParam(name = "identity", required = false) List<String> identity) {
        return userService.getUserBasicInfoByNameNoPagination(name, identity);
    }

    @RequestMapping(value = "updateUserInfo", method = RequestMethod.POST)
    public boolean updateUserInfo(String token, User user) {
        return userService.updateUserBasicInfo(token, user);
    }

    @RequestMapping(value = "updateUserPassword", method = RequestMethod.POST)
    public boolean updateUserPassword(String token, String oldPassword, String newPassword) {
        return userService.updateUserPassword(token, oldPassword, newPassword);
    }
}
