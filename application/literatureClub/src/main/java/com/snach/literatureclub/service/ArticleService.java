package com.snach.literatureclub.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.snach.literatureclub.bean.Article;
import com.snach.literatureclub.common.ArticleStatus;
import com.snach.literatureclub.common.exception.InsufficientPermissionException;
import com.snach.literatureclub.common.exception.InvalidTokenException;
import com.snach.literatureclub.dao.ArticleDao;
import com.snach.literatureclub.utils.File2PdfTools;
import com.snach.literatureclub.utils.FileUtil;
import com.snach.literatureclub.utils.SensitiveWordsTools;
import com.snach.literatureclub.utils.TokenTools;
import com.snach.literatureclub.utils.redis.ArticleLocker;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.snach.literatureclub.utils.TokenTools.getPayload;
import static com.snach.literatureclub.utils.TokenTools.tokenVerify;

@Service
public interface ArticleService {
    Article getArticleById(String articleId);

    /**
     * Get article list by author id
     * @param contributorId author id
     * @param pageNum page number for query
     * @param pageSize page size for query
     * @param statusList the status of the article
     * @return PageInfo object containing article list
     */
    PageInfo<Article> getContributorArticles(String contributorId, int pageNum, int pageSize, List<ArticleStatus> statusList);

    Article getArticleFileById(String articleId);

    String getLatestApprovalArticleById(String articleId);

    PageInfo<Article> getReceivedArticleById(String auditorId, int pageNum, int pageSize);

    /**
     * Get article list by page
     * @param pageNum page number for query
     * @param pageSize page size for query
     * @return PageInfo object containing article list
     */
    PageInfo<Article> getAllArticles(int pageNum, int pageSize);

    /**
     * Search article list by keyword
     * @param keyword keyword
     * @param tag tag filter
     * @param pageNum page number for query
     * @param pageSize page size for query
     * @param statusList the status of the article
     * @return PageInfo object containing article list
     */
    PageInfo<Article> searchArticle(String keyword, String tag, int pageNum, int pageSize, List<ArticleStatus> statusList);

    /**
     * Delete article by article id
     * @param token verify and check is the author of the article
     * @param articleId the article id
     * @return true if successfully deleted
     */
    boolean deleteArticleById(String token, String articleId);

    /**
     * Audit an article by sensitive words (sensitive words check)
     * @param token user token
     * @param articleId the article id
     * @param useStrict true if you use the strict mode
     * @return 返回文章的敏感词审核结果
     */
    List<?> sensitiveWordsJudge(String token, String articleId, boolean useStrict);

    byte[] File2Pdf(String id) throws IOException;

    boolean lockArticleById(String articleId, long expire, String lockedBy);

    boolean unLockArticleById(String articleId, String unlockedBy);

    boolean getPermissions(String articleId, String requester);
}

@Transactional(rollbackFor = Exception.class)
@Service
@Mapper
class ArticleServiceImpl implements ArticleService {
    private final ArticleDao articleDao;

    private final ArticleLocker articleLocker;

    @Autowired
    ArticleServiceImpl(ArticleDao articleDao, ArticleLocker locker) {
        this.articleDao = articleDao;
        this.articleLocker = locker;
    }

    @Override
    public Article getArticleById(String articleId) {
        return articleDao.getArticleById(articleId);
    }

    @Override
    public PageInfo<Article> getContributorArticles(String contributorId, int pageNum, int pageSize, List<ArticleStatus> statusList) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<>(articleDao.getArticleByContributorId(contributorId, statusList));
    }

    @Override
    public Article getArticleFileById(String articleId) {
        return articleDao.getArticleFileById(articleId);
    }

    @Override
    public String getLatestApprovalArticleById(String articleId) {
        return articleDao.getLatestApprovalArticleUrlById(articleId);
    }

    @Override
    public PageInfo<Article> getReceivedArticleById(String auditorId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<>(articleDao.getArticleByReceivedBy(auditorId));
    }

    @Override
    public PageInfo<Article> getAllArticles(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<>(articleDao.getAllArticles());
    }

    @Override
    public PageInfo<Article> searchArticle(String keyword, String tag, int pageNum, int pageSize, List<ArticleStatus> statusList) {
        PageHelper.startPage(pageNum, pageSize);
        if (tag == null) {
            return new PageInfo<>(articleDao.getArticlesByKeyword(keyword, statusList));
        } else {
            return new PageInfo<>(articleDao.getArticlesByKeywordAndTag(keyword, tag));
        }
    }

    @Override
    public boolean deleteArticleById(String token, String articleId) {
        if (!tokenVerify(token)) {
            throw new InvalidTokenException();
        }
        // get user id from token
        String contributorId = getPayload(token, "id");
        // check if the article belongs to the user
        if (articleDao.belong(contributorId, articleId) == 0) {
            throw new InsufficientPermissionException();
        }
        articleDao.deleteArticleById(articleId);
        return true;
    }

    /**
     * 对文章内容进行敏感词审核。
     *
     * @param token 用户的令牌，用于验证用户身份和权限。
     * @param id    文章的唯一标识符，用于从数据库中获取文章内容。
     * @return 返回一个包含审核结果的Map，其中键为审核相关的指标或错误信息，值为对应的结果或异常对象。返回的结果包括状态消息、敏感词数量和敏感词列表。
     * @throws InvalidTokenException        如果提供的token验证失败，则抛出此异常。
     */
    public List<?> sensitiveWordsJudge(String token, String id, boolean useStrict) {
        if (!tokenVerify(token)) {
            throw new InvalidTokenException();
        }

        Article article = articleDao.getArticleById(id);
        byte[] fileContent = article.getRaw();
        String format = article.getFileType();

        String txt = FileUtil.getFileRawTextByBytes(fileContent, format);

        boolean containsSensitiveWord = SensitiveWordsTools.judgeSensitivityWord(txt);

        List<?> sensitiveWordsList = new ArrayList<>();
        if (containsSensitiveWord) {
            sensitiveWordsList = SensitiveWordsTools.findAllSensitiveWords(txt, useStrict);
        }
        return sensitiveWordsList;
    }

    @Override
    public byte[] File2Pdf(String id) throws IOException {
        // 通过文章ID从数据库获取文章对象
        Article article = articleDao.getArticleFileById(id);
        // 获取文章原始内容的二进制数据
        byte[] fileContent = article.getRaw();
        // 获取文章原始文件的格式
        String fileType = article.getFileType();
        if (fileType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") || fileType.equals("text/plain")) {
            return File2PdfTools.File2Pdf(new ByteArrayInputStream(fileContent));
        } else {
            return null;
        }
    }

    @Override
    public boolean lockArticleById(String articleId, long expire, String lockedBy) {
        if (articleLocker.checkLock(articleId) && !articleLocker.checkLockPermission(articleId, lockedBy)) {
            throw new InsufficientPermissionException();
        } else {
            String authorId = articleDao.getArticleById(articleId).getTextBy();
            articleLocker.lock(articleId, expire, authorId, lockedBy);
        }
        return true;
    }

    @Override
    public boolean unLockArticleById(String articleId, String unlockedBy) {
        if (!getPermissions(articleId, TokenTools.getPayload(unlockedBy, "id"))) {
            throw new InsufficientPermissionException();
        }
        articleLocker.unlock(articleId);
        return true;
    }

    @Override
    public boolean getPermissions(String articleId, String requester) {
        if (articleLocker.checkLock(articleId) && !articleLocker.checkLockPermission(articleId, requester)) {
            throw new InsufficientPermissionException();
        }
        return true;
    }
}