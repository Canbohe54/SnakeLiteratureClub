package com.snach.literatureclub.service;

import com.github.pagehelper.PageHelper;
import com.snach.literatureclub.bean.Article;
import com.snach.literatureclub.bean.User;
import com.snach.literatureclub.dao.ArticleDao;
import com.snach.literatureclub.dao.FavoritesDao;
import com.snach.literatureclub.dao.UserDao;
import com.snach.literatureclub.utils.VerifyingCodeTools;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.snach.literatureclub.utils.TokenTools.*;
import static com.snach.literatureclub.utils.IdTools.*;
import static com.snach.literatureclub.utils.VerifyingCodeTools.*;

@Service
public interface UserService {
    /**
     * 向邮箱发送随机验证码
     *
     * @param email 收件人邮箱
     */
    void sendVerifyingCode(String email);

    /**
     * 用户注册
     *
     * @param user  用户信息
     * @param vCode 用户输入的验证码
     * @return 执行状态
     * <p>返回格式 {statusMsg: #{String}}
     */
    Map<String, Object> register(User user, String vCode);

    /**
     * 用户收藏
     * 返回格式 {statusMsg: #{String}}
     *
     * @param token      用于验证是否过期以及获取作者id
     * @param article_id
     * @return 执行状态
     */
    Map<String, Object> addFavorite(String token, String article_id);

    /**
     * 用户取消收藏
     * 返回格式 {statusMsg: #{String}}
     *
     * @param token      用于验证是否过期以及获取作者id
     * @param article_id
     * @return 执行状态
     */
    Map<String, Object> cancelFavorite(String token, String article_id);

    /**
     * 获取用户收藏列表基础信息
     * 返回格式 {articles: [#{Article}], statusMsg: #{String}}
     *
     * @param token    用于验证是否过期以及获取作者id
     * @param pageNum
     * @param pageSize
     * @return 收藏列表基础信息和执行状态
     */
    Map<String, Object> getAllFavorites(String token, String user_id, int pageNum, int pageSize);

    /**
     * @param token      用户id
     * @param article_id
     * @return 是否收藏该文章
     */
    Map<String, Object> isArticleFavorited(String token, String article_id);

    /**
     * 用户登录
     *
     * @param email    邮箱
     * @param password 密码
     * @return 返回格式{statusMsg: #{String},token: #{token}}
     */
    Map<String, Object> login(String email, String password);

    Map<String, Object> userSearch(String keyword);

    Map<String, Object> getUserBasicInfo(String userId);

    Map<String, Object> updateUserInfo(String token, User user);
}

@Mapper
@Service
class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    @Autowired
    private FavoritesDao favoritesDao;
    @Autowired
    private ArticleDao articleDao;

    @Override
    public void sendVerifyingCode(String email) {
        VerifyingCodeTools.sendVerifyingCode(email);
    }

    @Override
    public Map<String, Object> register(User user, String vCode) {
        Map<String, Object> response = new HashMap<>();
        if (!verifyCode(user.getEmail(), vCode)) {
            response.put("statusMsg", "Wrong Verifying Code.");
            return response;
        }
        if (userDao.existEmail(user.getEmail()) != 0) {
            response.put("statusMsg", "Email already exists.");
            return response;
        }
        if (user.getPassword() == null) {
            response.put("statusMsg", "The password cannot be empty.");
            return response;
        }
        if (user.getId() == null) {
            user.setId(generateId(Type.USER));
        }
        userDao.insertUser(user);
        response.put("statusMsg", "Success.");
        return response;
    }

    @Override
    public Map<String, Object> addFavorite(String token, String article_id) {
        Map<String, Object> response = new HashMap<String, Object>();
        // 检测token是否合法
        if (!tokenVerify(token)) {
            response.put("statusMsg", "Invalid token.");
            return response;
        }
        // 获取用户id
        String user_id = getPayload(token, "id");
        favoritesDao.addFavorite(user_id, article_id);
        response.put("statusMsg", "Success.");
        return response;
    }

    @Override
    public Map<String, Object> cancelFavorite(String token, String article_id) {
        Map<String, Object> response = new HashMap<String, Object>();
        // 检测token是否合法
        if (!tokenVerify(token)) {
            response.put("statusMsg", "Invalid token.");
            return response;
        }
        // 获取用户id
        String user_id = getPayload(token, "id");
        favoritesDao.cancelFavorite(user_id, article_id);
        response.put("statusMsg", "Success.");
        return response;
    }

    @Override
    public Map<String, Object> getAllFavorites(String token, String user_id, int pageNum, int pageSize) {
        Map<String, Object> response = new HashMap<String, Object>();
        // 检测token是否合法
        if (!tokenVerify(token)) {
            response.put("statusMsg", "Invalid token.");
            return response;
        }

        // 获取用户收藏稿件id
        PageHelper.startPage(pageNum, pageSize);
        List<String> aIds = favoritesDao.getAIdByUId(user_id);
        int total = aIds.size();
        // 通过稿件id获取稿件基本信息并返回
        List<Article> articles = new ArrayList<>();
        for (String aId : aIds) {
            articles.add(articleDao.getArticleBasicById(aId));
        }
        Map<String, Object> artInfo = new HashMap<>();
        artInfo.put("list", articles);
        artInfo.put("total", total);

        response.put("articles", artInfo);
        response.put("statusMsg", "Success.");
        return response;
    }

    @Override
    public Map<String, Object> isArticleFavorited(String token, String article_id) {
        Map<String, Object> response = new HashMap<String, Object>();
        // 检测token是否合法
        if (!tokenVerify(token)) {
            response.put("statusMsg", "Invalid token.");
            return response;
        }
        // 获取用户id
        String user_id = getPayload(token, "id");
        int isFavor = favoritesDao.isArticleFavorited(user_id, article_id);
        response.put("isFavor", isFavor == 0 ? "False" : "True");
        response.put("statusMsg", "Success.");
        return response;
    }

    @Override
    public Map<String, Object> login(String email, String password) {
        Map<String, Object> response = new HashMap<String, Object>();
        if (userDao.existEmail(email) == 0) {
            response.put("statusMsg", "Nonexistent");
        } else {
            User user = userDao.login(email, password);
            if (user == null) {
                response.put("statusMsg", "Password error.");
            } else {
                response.put("statusMsg", "Success.");
                response.put("token", tokenGen(user));
                response.put("userInfo", user.safeGetUserInfo());
            }
        }
        return response;
    }

    @Override
    public Map<String, Object> userSearch(String keyword) {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, Object>> users = new ArrayList<>();
        for (User u : userDao.getUsersByKeyword(keyword)) {
            users.add(u.safeGetUserInfo());
        }
        response.put("user_info", users);
        response.put("statusMsg", "Success.");
        return response;
    }

    @Override
    public Map<String, Object> getUserBasicInfo(String userId) {
        Map<String, Object> response = new HashMap<>();
        User user_info = userDao.getUserById(userId);
        if (user_info == null) {
            response.put("statusMsg", "Nonexistent");
            return response;
        }
        response.put("user_info", user_info.safeGetUserInfo());
        response.put("statusMsg", "Success.");
        return response;
    }

    @Override
    public Map<String, Object> updateUserInfo(String token, User user) {
        Map<String, Object> response = new HashMap<>();
        // 检测token是否合法
        if (!tokenVerify(token)) {
            response.put("statusMsg", "Invalid token.");
            return response;
        }

        userDao.updateUserInfo(user);
//        response.put("user_info", user_info.safeGetUserInfo());
        response.put("statusMsg", "Success.");
        return response;
    }
}
