package com.snach.literatureclub.service;

import com.snach.literatureclub.bean.User;
import com.snach.literatureclub.common.ArticlePublishStatus;
import com.snach.literatureclub.common.Identity;
import com.snach.literatureclub.common.exception.InsufficientPermissionException;
import com.snach.literatureclub.dao.ArticleDao;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

public interface ExpertService {
    /**
     * 推荐文章
     *
     * @param expert        User object of an auditor
     * @param articleId     文章id
     * @param recommendTo   杂志社猎头id
     * @return true if success
     */
    boolean recommendArticle(User expert, String articleId, String recommendTo);
}

@Transactional(rollbackFor = Exception.class)
@Service
@Mapper
class ExpertServiceImpl implements ExpertService {

    private final ArticleDao articleDao;

    @Autowired
    ExpertServiceImpl(ArticleDao articleDao) {
        this.articleDao = articleDao;
    }
    public boolean recommendArticle(User expert, String articleId, String recommendTo) {
        if (!expert.checkIdentity(Identity.EXPERT) && !expert.checkIdentity(Identity.ADMINISTRATOR)) {
            throw new InsufficientPermissionException();
        }
        articleDao.updatePublishStatus(articleId, ArticlePublishStatus.UNDER_RECORD);
        articleDao.updateReceivedBy(articleId, recommendTo);
        return true;
    }
}