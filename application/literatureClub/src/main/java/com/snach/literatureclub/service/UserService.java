package com.snach.literatureclub.service;

import com.snach.literatureclub.utils.IdManager;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.snach.literatureclub.bean.User;
import com.snach.literatureclub.common.exception.*;
import com.snach.literatureclub.dao.UserDao;

import static com.snach.literatureclub.utils.TokenTools.*;


@Service
public interface UserService {
    // Account
    String login(String id, String password);
    void register(User user);

    // User Info
    User getUserBasicInfo(String id);
    void updateUserBasicInfo(String token, User user);
}

@Mapper
@Service
class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    // Account
    public String login(String id, String password) {
        String verifiedId;
        if (id.length() == 11 && id.charAt(0) == '1') {
            verifiedId = userDao.loginByPhone(id, password);
        } else {
            verifiedId = userDao.loginById(id, password);
        }
        if (verifiedId == null) {
            throw new WrongIdOrPasswordException();
        }
        return tokenGen(verifiedId);
    }

    public void register(User user) {
        user.setId(IdManager.generateUserId());
        userDao.insertUser(user);
    }

    // User Info
    public User getUserBasicInfo(String id) {
        User user = userDao.getUserById(id);
        if (user == null) {
            throw new NonexistentUserException();
        }
        user.setPassword("<SECRET>");
        return user;
    }

    public void updateUserBasicInfo(String token, User user) {
        if (!tokenVerify(token, user)) {
            throw new InvalidTokenException();
        }
        userDao.updateUserInfo(user);
    }
}
