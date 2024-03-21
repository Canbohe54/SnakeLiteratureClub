package com.snach.literatureclub.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.snach.literatureclub.utils.IdManager;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.snach.literatureclub.bean.User;
import com.snach.literatureclub.common.exception.*;
import com.snach.literatureclub.dao.UserDao;
import org.springframework.transaction.annotation.Transactional;

import static com.snach.literatureclub.utils.TokenTools.*;


@Service
public interface UserService {
    // Account
    String login(String id, String password);
    void register(User user);

    // User Info
    User getUserBasicInfo(String id);
    PageInfo getUserBasicInfoByName(String name, String identity, int pageNum, int pageSize);
    void updateUserBasicInfo(String token, User user);
}
@Transactional(rollbackFor = Exception.class)
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

    @Override
    public PageInfo getUserBasicInfoByName(String name, String identity, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        if(identity != null){
            return new PageInfo<>(userDao.getUserByNameAndIdentity(name,identity));
        }
        return new PageInfo<>(userDao.getUserByName(name));
    }

    public void updateUserBasicInfo(String token, User user) {
        if (!tokenVerify(token, user)) {
            throw new InvalidTokenException();
        }
        userDao.updateUserInfo(user);
    }
}
