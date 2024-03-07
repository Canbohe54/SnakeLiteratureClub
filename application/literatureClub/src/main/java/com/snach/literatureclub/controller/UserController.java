package com.snach.literatureclub.controller;

import com.snach.literatureclub.bean.User;
import com.snach.literatureclub.common.annotation.ResponseNotIntercept;
import com.snach.literatureclub.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 向邮箱发送随机验证码
     *
     * @param email 收件人邮箱
     */
    @RequestMapping(value = "sendvcode", method = RequestMethod.POST)
    @ResponseNotIntercept
    public void sendVerifyingCode(String email) {
        userService.sendVerifyingCode(email);
    }

    /**
     * 用户注册
     *
     * @param user  用户信息
     * @param vCode 用户输入的验证码
     * @return 执行状态
     * <p>返回格式 {statusMsg: #{String}}
     */
    @RequestMapping(value = "reg", method = RequestMethod.POST)
    @ResponseNotIntercept
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
    @ResponseNotIntercept
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
    @ResponseNotIntercept
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
    @ResponseNotIntercept
    public Map<String, Object> getAllFavorites(String token,
                                               @RequestParam("user_id") String user_id,
                                               @RequestParam(name = "page_num") int pageNum,
                                               @RequestParam(name = "page_size") int pageSize) {
        return userService.getAllFavorites(token, user_id, pageNum, pageSize);
    }

    @RequestMapping(value = "isArticleFavor", method = RequestMethod.GET)
    @ResponseNotIntercept
    public Map<String, Object> getIsArticleFavorited(@RequestParam("token") String token, @RequestParam("article_id") String article_id) {
        return userService.isArticleFavorited(token, article_id);
    }

    @RequestMapping(value = "login", method = RequestMethod.POST)
    @ResponseNotIntercept
    public Map<String, Object> login(@RequestParam("email") String email, @RequestParam("password") String password) {
        return userService.login(email, password);
    }

    @RequestMapping(value = "verifyPasswd", method = RequestMethod.POST)
    @ResponseNotIntercept
    public Map<String, Object> verifyPasswd(@RequestParam("token") String token, @RequestParam("password") String password) {
        return userService.verifyPasswd(token, password);
    }

    @RequestMapping(value="confirmEmail", method = RequestMethod.POST)
    @ResponseNotIntercept
    public Map<String, Object> confirmEmail(@RequestParam("email") String email, @RequestParam("v_code") String vCode){
        return userService.confirmEmail(email, vCode);
    }

    @RequestMapping(value = "search", method = RequestMethod.GET)
    @ResponseNotIntercept
    public Map<String, Object> search(@RequestParam("keyword") String keyword) {
        return userService.userSearch(keyword);
    }


    @RequestMapping(value = "getUserBasicInfo", method = RequestMethod.GET)
    @ResponseNotIntercept
    public Map<String, Object> getUserBasicInfo(@RequestParam("user_id") String userId) {
        return userService.getUserBasicInfo(userId);
    }

    @RequestMapping(value = "updateUserInfo", method = RequestMethod.POST)
    @ResponseNotIntercept
    public Map<String, Object> updateUserInfo(@RequestParam("token") String token, User user) {
        return userService.updateUserInfo(token, user);
    }

    /**
     * 获取关注的userId列表
     * 返回格式：{ all_followed: [#{String},...], total: #{NUMBER}, statusMsg: #{String}}
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "getAllFollowed", method = RequestMethod.GET)
    @ResponseNotIntercept
    public Map<String, Object> getAllFollowed(@RequestParam("user_id") String userId,
                                              @RequestParam("target_identity") List<String> targetIdentity,
                                              @RequestParam(name = "page_num")int pageNum,
                                              @RequestParam(name = "page_size")int pageSize) {
        return userService.getAllFollowed(userId, targetIdentity, pageNum, pageSize);
    }

    @RequestMapping(value = "getAllFans", method = RequestMethod.GET)
    @ResponseNotIntercept
    public Map<String, Object> getAllFans(@RequestParam("user_id") String userId,
                                          @RequestParam("target_identity") List<String> targetIdentity) {
        return userService.getAllFans(userId, targetIdentity);
    }

    @RequestMapping(value = "followByUID", method = RequestMethod.POST)
    @ResponseNotIntercept
    public Map<String, Object> followByUID(String token, @RequestParam("user_id") String userId) {
        return userService.followByUID(token, userId);
    }

    @RequestMapping(value = "unfollowByUID", method = RequestMethod.POST)
    @ResponseNotIntercept
    public Map<String, Object> unfollowByUID(String token, @RequestParam("user_id") String userId) {
        return userService.unfollowByUID(token, userId);
    }

    @RequestMapping(value = "getIsFollowedByUID", method = RequestMethod.GET)
    @ResponseNotIntercept
    public Map<String, Object> getIsFollowedByUID(String token, @RequestParam("user_id") String userId) {
        return userService.getIsFollowedByUID(token, userId);
    }
    /**
     * 获取用户文章数
     * */
    @RequestMapping(value = "getAllArticleNum", method = RequestMethod.GET)
    @ResponseNotIntercept
    public Map<String, Object> getAllArticleNum(@RequestParam("user_id")String userId){
        return userService.getAllArticleNum(userId);
    }
    /**
     * 获取用户粉丝数
     * */
    @RequestMapping(value = "getFansNum", method = RequestMethod.GET)
    @ResponseNotIntercept
    public Map<String, Object> getFansNum(@RequestParam("user_id")String userId){
        return userService.getFansNum(userId);
    }
    /**
     * 获取用户关注数
     * */
    @RequestMapping(value = "getFollowNum", method = RequestMethod.GET)
    @ResponseNotIntercept
    public Map<String, Object> getFollowNum(@RequestParam("user_id")String userId){
        return userService.getFollowNum(userId);
    }

    @RequestMapping(value = "eraseUser",method = RequestMethod.POST)
    @ResponseNotIntercept
    public Map<String, Object> eraseUser(String token, @RequestParam("hard_token")String hardToken){
        return userService.eraseUser(token, hardToken);
    }

    @RequestMapping(value = "changePasswd", method = RequestMethod.POST)
    @ResponseNotIntercept
    public Map<String, Object> changePasswd(@RequestParam("user_id") String userId, @RequestParam("new_password")String newPassword, @RequestParam("hard_token")String hardToken){
        return userService.changePasswd(userId, newPassword, hardToken);
    }

    @RequestMapping(value = "changeEmail", method = RequestMethod.POST)
    @ResponseNotIntercept
    public Map<String, Object> changeEmail(@RequestParam("user_id") String userId, @RequestParam("new_email")String newEmail, @RequestParam("hard_token")String hardToken, @RequestParam("v_code")String vCode){
        return userService.changeEmail(userId, newEmail, hardToken, vCode);
    }

    @RequestMapping(value = "isUserExist", method = RequestMethod.GET)
    @ResponseNotIntercept
    public Map<String, Object> isUserExist(@RequestParam("user_id")String userId){
        return userService.isUserExist(userId);
    }
}