package com.snach.literatureclub.service;

import com.snach.literatureclub.bean.User;
import com.snach.literatureclub.bean.Article;
import com.snach.literatureclub.common.exception.InsufficientPermissionException;
import com.snach.literatureclub.common.exception.NoUnauditedArticleException;
import com.snach.literatureclub.common.ArticleStatus;
import com.snach.literatureclub.common.Identity;
import com.snach.literatureclub.dao.ArticleDao;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public interface AuditorService {
    /**
     * 获取待审核的文章
     *
     * @param auditor 审核码
     * @return
     */
    Article getUnauditedArticle(User auditor) throws InsufficientPermissionException, NoUnauditedArticleException;

    /**
     * 审核文章
     *
     * @param auditor
     * @param articleId   审核的文章id
     * @param auditResult 审核结果
     * @param reason      审核不通过原因，若审核通过则忽略
     * @return
     */
    boolean audit(User auditor, String articleId, boolean auditResult, String reason) throws InsufficientPermissionException;
}

@Service
@Mapper
class AuditorServiceImpl implements AuditorService {
    @Autowired
    ArticleDao articleDao;

    @Override
    public Article getUnauditedArticle(User auditor) throws InsufficientPermissionException, NoUnauditedArticleException {
        if (!auditor.checkIdentity(Identity.审核员)) {
            throw new InsufficientPermissionException();
        }
        Article article = articleDao.getArticleByStatus(ArticleStatus.SUBMITTED);
        if (article == null) {
            throw new NoUnauditedArticleException();
        }
        return article;
    }

    @Override
    public boolean audit(User auditor, String articleId, boolean auditResult, String reason) throws InsufficientPermissionException {
        if (!auditor.checkIdentity(Identity.审核员)) {
            throw new InsufficientPermissionException();
        }
        return true;
    }
}
