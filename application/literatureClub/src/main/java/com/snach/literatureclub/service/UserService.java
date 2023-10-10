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
}
