package com.snach.literatureclub.service;

import com.snach.literatureclub.dao.UserDao;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public interface UserService {
    Map<String, Object> getUser(String userId);

    /**
     * 用户收藏
     * 返回格式 {statusMsg: #{String}}
     * @param token 用于验证是否过期以及获取作者id
     * @param article_id
     * @return 执行状态
     */
    Map<String,Object> addFavorite(String token,String article_id);

    /**
     * 用户取消收藏
     * 返回格式 {statusMsg: #{String}}
     * @param token 用于验证是否过期以及获取作者id
     * @param article_id
     * @return 执行状态
     */
    Map<String,Object> cancelFavorite(String token,String article_id);

}

@Mapper
@Service
class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;
    public Map<String, Object> getUser(String userId) {
        Map<String, Object> res = new HashMap<>();
        return res;
    }

    @Override
    public Map<String, Object> addFavorite(String token, String article_id) {
        return null;
    }

    @Override
    public Map<String, Object> cancelFavorite(String token, String article_id) {
        return null;
    }
    //TODO getAllFavorites
}
