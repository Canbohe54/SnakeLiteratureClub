package com.snach.literatureclub.service;

import com.snach.literatureclub.bean.Article;
import com.snach.literatureclub.dao.ArticleDao;
import com.snach.literatureclub.dao.FavoritesDao;
import com.snach.literatureclub.dao.UserDao;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.snach.literatureclub.utils.TokenTools.getPayload;
import static com.snach.literatureclub.utils.TokenTools.tokenVerify;

@Service
public interface UserService {
    Map<String, Object> getUser(String userId);

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
     * @param token 用于验证是否过期以及获取作者id
     * @return 收藏列表基础信息和执行状态
     */
    Map<String, Object> getAllFavorites(String token);

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

    public Map<String, Object> getUser(String userId) {
        Map<String, Object> response = new HashMap<>();
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
    public Map<String, Object> getAllFavorites(String token) {
        Map<String, Object> response = new HashMap<String, Object>();
        // 检测token是否合法
        if (!tokenVerify(token)) {
            response.put("statusMsg", "Invalid token.");
            return response;
        }
        // 获取用户id
        String user_id = getPayload(token, "id");
        // 获取用户收藏稿件id
        List<String> aIds = favoritesDao.getAIdByUId(user_id);
        // 通过稿件id获取稿件基本信息并返回
        List<Article> articles = new ArrayList<>();
        for (String aId : aIds) {
            articles.add(articleDao.getArticleBasicById(aId));
        }
        response.put("articles", articles);
        response.put("statusMsg", "Success.");
        return response;
    }

}
