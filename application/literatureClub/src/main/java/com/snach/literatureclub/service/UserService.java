package com.snach.literatureclub.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.snach.literatureclub.bean.User;
import com.snach.literatureclub.common.Identity;
import com.snach.literatureclub.common.exception.InvalidTokenException;
import com.snach.literatureclub.common.exception.NonexistentException;
import com.snach.literatureclub.common.exception.WrongIdOrPasswordException;
import com.snach.literatureclub.dao.*;
import com.snach.literatureclub.utils.IdManager;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

import static com.snach.literatureclub.utils.TokenTools.tokenGen;
import static com.snach.literatureclub.utils.TokenTools.tokenVerify;
import static com.snach.literatureclub.utils.TokenTools.getPayload;


@Service
public interface UserService {
    // Account
    String login(String id, String password);

    String register(User user);

    // User Info
    User getUserBasicInfo(String id);

    PageInfo<User> getUserBasicInfoByName(String name, List<String> identity, int pageNum, int pageSize);

    String getUserIdentity(String id);

    List<User> getUserBasicInfoByNameNoPagination(String name, List<String> identity);

    boolean updateUserBasicInfo(String token, User user);

    boolean updateUserPassword(String token, String oldPassword, String newPassword);
}

@Transactional(rollbackFor = Exception.class)
@Mapper
@Service
class UserServiceImpl implements UserService {
    private final UserDao userDao;
    private final ContributorDao contributorDao; //字段多了感觉还是在字段上用autowired舒服
    private final IdManager idManager;
    private final TeacherDao teacherDao;
    private final ExpertDao expertDao;
    private final HunterDao hunterDao;
    private final AuditorDao auditorDao;

    @Autowired
    public UserServiceImpl(UserDao userDao, IdManager manager,
                           ContributorDao contributorDao, TeacherDao teacherDao,
                           ExpertDao expertDao, HunterDao hunterDao, AuditorDao auditorDao) {
        this.userDao = userDao;
        this.idManager = manager;
        this.contributorDao = contributorDao;
        this.teacherDao = teacherDao;
        this.expertDao = expertDao;
        this.hunterDao = hunterDao;
        this.auditorDao = auditorDao;
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
    public String register(User user) {
        String newUserId = idManager.generateUserId();
        user.setId(newUserId);
        userDao.insertUser(user);
        Identity identity = user.getIdentity();
        switch (identity) {
            case CONTRIBUTOR:
                contributorDao.insertUser(user);
                break;
            case TEACHER:
                teacherDao.insertUser(user);
                break;
            case EXPERT:
                expertDao.insertUser(user);
                break;
            case HUNTER:
                hunterDao.insertUser(user);
                break;
            case AUDITOR:
                auditorDao.insertUser(user);
                break;
            default:
                throw new NonexistentException(String.valueOf(user.getIdentity()));
        }
        return newUserId;
    }

    // User Info
    @Override
    public User getUserBasicInfo(String id) {
        Identity identity = userDao.getUserById(id).getIdentity();
        if (identity == null) {
            throw new NonexistentException(User.class);
        }
        User user;
        String attrs;
        switch (identity) {
            case CONTRIBUTOR:
                user = contributorDao.getUserById(id);
                attrs = contributorDao.getContributorGrade(id);
                user.setAttrs("{\"grade\":"+attrs+"}");
                break;
            case TEACHER:
                user = teacherDao.getUserById(id);
                break;
            case EXPERT:
                user = expertDao.getUserById(id);
                break;
            case HUNTER:
                user = hunterDao.getUserById(id);
                break;
            case AUDITOR:
                user = auditorDao.getUserById(id);
                break;
            default:
                throw new NonexistentException(User.class);
        }
        user.setIdentity(identity);
        user.setPassword("<SECRET>");
        return user;
    }

    @Override
    public PageInfo<User> getUserBasicInfoByName(String name, List<String> identity, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        if (identity != null && !identity.isEmpty()) {
            return new PageInfo<>(userDao.getUserByNameAndIdentity(name, identity));
        }
        return new PageInfo<>(userDao.getUserByName(name));
    }

    @Override
    public String getUserIdentity(String id) {
        return userDao.getUserIdentity(id);
    }

    @Override
    public List<User> getUserBasicInfoByNameNoPagination(String name, List<String> identity) {
        return userDao.getUserByNameAndIdentity(name, identity);
    }

    @Override
    public boolean updateUserBasicInfo(String token, User user) {
        if (!tokenVerify(token, user)) {
            throw new InvalidTokenException();
        }
        userDao.updateUserInfo(user);
        switch (user.getIdentity()) {
            case CONTRIBUTOR:
                contributorDao.updateUserInfo(user);
                break;
            case TEACHER:
                teacherDao.updateUserInfo(user);
                break;
            case EXPERT:
                expertDao.updateUserInfo(user);
                break;
            case HUNTER:
                hunterDao.updateUserInfo(user);
                break;
            case AUDITOR:
                auditorDao.updateUserInfo(user);
                break;
            default:
                throw new NonexistentException(String.valueOf(user.getIdentity()));
        }
        return true;
    }

    @Override
    public boolean updateUserPassword(String token, String oldPassword, String newPassword) {
        if (!tokenVerify(token)) {
            throw new InvalidTokenException();
        }
        String id = getPayload(token, "id");
        if (!Objects.equals(userDao.loginById(id, oldPassword), id)) {
            throw new WrongIdOrPasswordException("Wrong old password.");
        }
        userDao.updateUserPassword(id, newPassword);
        return true;
    }
}
