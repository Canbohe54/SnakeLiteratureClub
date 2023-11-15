package com.snach.literatureclub.controller;

import com.snach.literatureclub.bean.User;
import com.snach.literatureclub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 向邮箱发送随机验证码
     *
     * @param email 收件人邮箱
     */
    @RequestMapping(value = "sendvcode", method = RequestMethod.POST)
    public void sendVerifyingCode(String email) {
        userService.sendVerifyingCode(email);
    }

    /**
     * 用户注册
     *
     * @param user 用户信息
     * @param vCode 用户输入的验证码
     * @return 执行状态
     * <p>返回格式 {statusMsg: #{String}}
     */
    @RequestMapping(value = "reg", method = RequestMethod.POST)
    public Map<String, Object> register(User user, String vCode) {
        return userService.register(user, vCode);
    }

    /**
     * 用户收藏
     * 返回格式 {statusMsg: #{String}}
     *
     * @param token      用于验证是否过期以及获取作者id
     * @param article_id
     * @return 执行状态
     */
    @RequestMapping(value = "addFavorite", method = RequestMethod.POST)
    public Map<String, Object> addFavorite(@RequestParam("token") String token, @RequestParam("article_id") String article_id) {
        return userService.addFavorite(token, article_id);
    }

    /**
     * 用户取消收藏
     * 返回格式 {statusMsg: #{String}}
     *
     * @param token      用于验证是否过期以及获取作者id
     * @param article_id
     * @return 执行状态
     */
    @RequestMapping(value = "cancelFavorite", method = RequestMethod.POST)
    public Map<String, Object> cancelFavorite(@RequestParam("token") String token, @RequestParam("article_id") String article_id) {
        return userService.cancelFavorite(token, article_id);
    }

    /**
     * 用户获取收藏列表基础信息
     * 返回格式 {articles: [{#ARTICLE}, ...], statusMsg: #{String}}
     *
     * @param token 用于验证是否过期以及获取作者id
     * @return 收藏列表，执行状态
     */
    @RequestMapping(value = "getAllFavorites", method = RequestMethod.GET)
    public Map<String, Object> getAllFavorites(String token) {
        return userService.getAllFavorites(token);
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    public Map<String, Object> login(@RequestParam("email") String email, @RequestParam("password") String password) {
        return userService.login(email, password);
    }

    @RequestMapping(value = "search", method = RequestMethod.GET)
    public Map<String, Object> search(@RequestParam("keyword") String keyword) {
        return userService.userSearch(keyword);
    }
    @RequestMapping(value = "getUserBasicInfo",method = RequestMethod.GET)
    public Map<String,Object> getUserBasicInfo(@RequestParam("user_id")String userId){
        return userService.getUserBasicInfo(userId);
    }
}