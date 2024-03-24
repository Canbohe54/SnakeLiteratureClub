package com.snach.literatureclub.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.snach.literatureclub.bean.User;
import com.snach.literatureclub.common.exception.InvalidTokenException;
import com.snach.literatureclub.common.exception.NonexistentException;
import com.snach.literatureclub.common.exception.WrongIdOrPasswordException;
import com.snach.literatureclub.dao.UserDao;
import com.snach.literatureclub.utils.IdManager;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.snach.literatureclub.utils.TokenTools.tokenGen;
import static com.snach.literatureclub.utils.TokenTools.tokenVerify;


@Service
public interface UserService {
    // Account
    String login(String id, String password);
    boolean register(User user);

    // User Info
    User getUserBasicInfo(String id);
    PageInfo<User> getUserBasicInfoByName(String name, String identity, int pageNum, int pageSize);
    boolean updateUserBasicInfo(String token, User user);
}
@Transactional(rollbackFor = Exception.class)
@Mapper
@Service
class UserServiceImpl implements UserService {
    private final UserDao userDao;

    private final IdManager idManager;

    @Autowired
    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
        idManager = IdManager.getManager();
    }

    // Account
    @Override
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

    @Override
    public boolean register(User user) {
        user.setId(idManager.generateUserId());
        userDao.insertUser(user);
        return true;
    }

    // User Info
    @Override
    public User getUserBasicInfo(String id) {
        User user = userDao.getUserById(id);
        if (user == null) {
            throw new NonexistentException(User.class);
        }
        user.setPassword("<SECRET>");
        return user;
    }

    @Override
    public PageInfo<User> getUserBasicInfoByName(String name, String identity, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        if(identity != null){
            return new PageInfo<>(userDao.getUserByNameAndIdentity(name,identity));
        }
        return new PageInfo<>(userDao.getUserByName(name));
    }

    @Override
    public boolean updateUserBasicInfo(String token, User user) {
        if (!tokenVerify(token, user)) {
            throw new InvalidTokenException();
        }
        userDao.updateUserInfo(user);
        return true;
    }
}
