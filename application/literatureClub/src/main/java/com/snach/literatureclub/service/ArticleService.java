package com.snach.literatureclub.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.snach.literatureclub.bean.Article;
import com.snach.literatureclub.common.ArticleAuditStatus;
import com.snach.literatureclub.common.ArticlePublishStatus;
import com.snach.literatureclub.common.exception.InsufficientPermissionException;
import com.snach.literatureclub.common.exception.InvalidTokenException;
import com.snach.literatureclub.dao.NewArticleDao;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.snach.literatureclub.utils.TokenTools.getPayload;
import static com.snach.literatureclub.utils.TokenTools.tokenVerify;
import static com.snach.literatureclub.utils.SqlQueryStatement.toSelectString;

@Service
public interface ArticleService {
    boolean changeArticleStatus(String articleId, ArticleAuditStatus status, String token);
    boolean changeArticlePublishStatus(String articleId, ArticlePublishStatus status, String token);

    boolean changeArticleReceivedBy(String articleId, String receivedBy, String token);

    Article getArticleById(String articleId);

    PageInfo<Article> getArticles(List<String> idList,
                                  List<String> authorList,
                                  List<String> receiverList,
                                  List<String> auditorList,
                                  String keyword,
                                  String tags,
                                  List<ArticleAuditStatus> auditStatusList,
                                  List<ArticlePublishStatus> publishStatusList,
                                  int pageNum,
                                  int pageSize);

    /**
     * Get article list by author id
     *
     * @param contributorId author id
     * @param pageNum       page number for query
     * @param pageSize      page size for query
     * @param statusList    the status of the article
     * @return PageInfo object containing article list
     */
    PageInfo<Article> getContributorArticles(String contributorId, int pageNum, int pageSize, List<ArticleAuditStatus> statusList, List<ArticlePublishStatus> publishStatusList);

    Article getArticleFileById(String articleId);

    String getLatestApprovalArticleById(String articleId);

    PageInfo<Article> getReceivedArticleById(String auditorId, int pageNum, int pageSize);

    /**
     * Get article list by page
     *
     * @param pageNum  page number for query
     * @param pageSize page size for query
     * @return PageInfo object containing article list
     */
    PageInfo<Article> getAllArticles(int pageNum, int pageSize);

    /**
     * Search article list by keyword
     *
     * @param keyword    keyword
     * @param tag        tag filter
     * @param pageNum    page number for query
     * @param pageSize   page size for query
     * @param statusList the status of the article
     * @return PageInfo object containing article list
     */
    PageInfo<Article> searchArticle(String keyword, String tag, int pageNum, int pageSize, List<ArticlePublishStatus> statusList);

    /**
     * Delete article by article id
     *
     * @param token     verify and check is the author of the article
     * @param articleId the article id
     * @return true if successfully deleted
     */
    boolean deleteArticleById(String token, String articleId);

    /**
     * Audit an article by sensitive words (sensitive words check)
     *
     * @param token     user token
     * @param articleId the article id
     * @param useStrict true if you use the strict mode
     * @return 返回文章的敏感词审核结果
     */
    List<?> sensitiveWordsJudge(String token, String articleId, boolean useStrict);

    byte[] File2Pdf(String id) throws IOException;

    boolean lockArticleById(String articleId, long expire, String lockedBy);

    boolean unLockArticleById(String articleId, String unlockedBy);

    boolean getPermissions(String articleId, String requester);

    boolean checkLock(String articleId);

    long getArticleLockExpire(String articleId);

    Map<String, Object> getRecievedAndPublishedCount(String contributorId);

    Map<String, Object> getContributorCenterInformation(String contributorId);
}

@Transactional(rollbackFor = Exception.class)
@Service
@Mapper
class ArticleServiceImpl implements ArticleService {
    private final NewArticleDao newArticleDao;

    private final ArticleLocker articleLocker;

    @Autowired
    ArticleServiceImpl(ArticleLocker locker, NewArticleDao newArticleDao) {
        this.articleLocker = locker;
        this.newArticleDao = newArticleDao;
    }

    @Override
    public boolean changeArticleStatus(String articleId, ArticleAuditStatus status, String token) {
        if (!tokenVerify(token)) {
            throw new InvalidTokenException();
        }
        newArticleDao.updateAuditStatus(articleId, status);
        return true;
    }

    @Override
    public boolean changeArticlePublishStatus(String articleId, ArticlePublishStatus status, String token) {
        if (!tokenVerify(token)) {
            throw new InvalidTokenException();
        }
        newArticleDao.updatePublishStatus(articleId, status);
        return true;
    }

    @Override
    public boolean changeArticleReceivedBy(String articleId, String receivedBy, String token) {
        if (!tokenVerify(token)) {
            throw new InvalidTokenException();
        }
        newArticleDao.updateReceivedBy(articleId, receivedBy);
        return true;
    }

    @Override
    public Article getArticleById(String articleId) {
        Article article = newArticleDao.getArticleById(articleId);
        return article;
    }

    @Override
    public PageInfo<Article> getArticles(List<String> idList,
                                         List<String> authorList,
                                         List<String> receiverList,
                                         List<String> auditorList,
                                         String keyword,
                                         String tags,
                                         List<ArticleAuditStatus> auditStatusList,
                                         List<ArticlePublishStatus> publishStatusList,
                                         int pageNum,
                                         int pageSize) {
        // todo tag 筛选
        PageHelper.startPage(pageNum, pageSize);
        StringBuilder whereStatement = new StringBuilder();

        if (idList != null && !idList.isEmpty()) {
            whereStatement.append("id IN ").append(toSelectString(idList));
        }
        if (authorList != null && !authorList.isEmpty()) {
            if (!whereStatement.isEmpty()) {
                whereStatement.append(" AND ");
            }
            whereStatement.append("text_by IN ").append(toSelectString(authorList));
        }
        if (receiverList != null && !receiverList.isEmpty()) {
            if (!whereStatement.isEmpty()) {
                whereStatement.append(" AND ");
            }
            whereStatement.append("received_by IN ").append(toSelectString(receiverList));
        }
        if (auditorList != null && !auditorList.isEmpty()) {
            if (!whereStatement.isEmpty()) {
                whereStatement.append(" AND ");
            }
            whereStatement.append("audited_by IN ").append(toSelectString(auditorList));
        }
//        if (keyword != null && !keyword.isEmpty()) {
        if (keyword != null) {
            if (!whereStatement.isEmpty()) {
                whereStatement.append(" AND ");
            }
            whereStatement.append("title LIKE '%").append(keyword).append("%'");
        }
        if (auditStatusList != null && !auditStatusList.isEmpty()) {
            if (!whereStatement.isEmpty()) {
                whereStatement.append(" AND ");
            }
            whereStatement.append("audit_status IN ").append(toSelectString(auditStatusList));
        }
        if (publishStatusList != null && !publishStatusList.isEmpty()) {
            if (!whereStatement.isEmpty()) {
                whereStatement.append(" AND ");
            }
            whereStatement.append("publish_status IN ").append(toSelectString(publishStatusList));
        }
        return new PageInfo<>(newArticleDao.getArticleByPersonalOptions(whereStatement.toString()));
    }

    @Override
    public PageInfo<Article> getContributorArticles(String contributorId, int pageNum, int pageSize, List<ArticleAuditStatus> auditStatusList, List<ArticlePublishStatus> publishStatusList) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<>(newArticleDao.getArticleByContributorId(contributorId, auditStatusList, publishStatusList));
    }

    @Override
    public Article getArticleFileById(String articleId) {
        return newArticleDao.getArticleFileById(articleId);
    }

    @Override
    public String getLatestApprovalArticleById(String articleId) {
        return newArticleDao.getLatestApprovalArticleUrlById(articleId);
    }

    @Override
    public PageInfo<Article> getReceivedArticleById(String auditorId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<>(newArticleDao.getArticleByReceivedBy(auditorId));
    }

    @Override
    public PageInfo<Article> getAllArticles(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<>(newArticleDao.getAllArticles());
    }

    @Override
    public PageInfo<Article> searchArticle(String keyword, String tag, int pageNum, int pageSize, List<ArticlePublishStatus> statusList) {
        PageHelper.startPage(pageNum, pageSize);
        PageInfo<Article> pageInfo;
        if (tag == null) {
            pageInfo = new PageInfo<>(newArticleDao.getArticlesByKeyword(keyword, statusList));
        } else {
            pageInfo = new PageInfo<>(newArticleDao.getArticlesByKeywordAndTag(keyword, tag));
        }
        return pageInfo;
    }

    @Override
    public boolean deleteArticleById(String token, String articleId) {
        if (!tokenVerify(token)) {
            throw new InvalidTokenException();
        }
        // get user id from token
        String contributorId = getPayload(token, "id");
        // check if the article belongs to the user
        if (newArticleDao.belong(contributorId, articleId) == 0) {
            throw new InsufficientPermissionException();
        }
        newArticleDao.deleteArticleById(articleId);
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

        Article article = newArticleDao.getArticleFileById(id);
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
        Article article = newArticleDao.getArticleFileById(id);
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
        if (!tokenVerify(lockedBy)) {
            throw new InvalidTokenException();
        }
        if (articleLocker.checkLock(articleId) && !articleLocker.checkLockPermission(articleId, TokenTools.getPayload(lockedBy, "id"))) {
            throw new InsufficientPermissionException();
        } else {
            String authorId = newArticleDao.getArticleById(articleId).getTextBy();
            articleLocker.lock(articleId, expire, authorId, TokenTools.getPayload(lockedBy, "id"));
        }
        return true;
    }

    @Override
    public boolean unLockArticleById(String articleId, String unlockedBy) {
        if (!tokenVerify(unlockedBy)) {
            throw new InvalidTokenException();
        }
        if (articleLocker.checkLock(articleId) && !articleLocker.checkLockPermission(articleId, TokenTools.getPayload(unlockedBy, "id"))) {
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

    @Override
    public boolean checkLock(String articleId) {
        return articleLocker.checkLock(articleId);
    }

    @Override
    public long getArticleLockExpire(String articleId) {
        return articleLocker.getArticleLockExpire(articleId);
    }

    @Override
    public Map<String, Object> getRecievedAndPublishedCount(String contributorId) {
        int receivedCount = newArticleDao.getReceivedCount(contributorId);
        int publishedCount = newArticleDao.getPublishedCount(contributorId);
        Map<String, Object> res = new HashMap<>();
        res.put("receivedCount", receivedCount);
        res.put("publishedCount", publishedCount);
        return res;
    }

    @Override
    public Map<String, Object> getContributorCenterInformation(String contributorId) {
        int roughCount = newArticleDao.getRoughCount(contributorId);
        int beingAuditedCount = newArticleDao.getBeingAuditedCount(contributorId);
        int failAuditedCount = newArticleDao.getFailAuditedCount(contributorId);
        int reasonCount = newArticleDao.getReasonCount(contributorId);
        int failedReviewCount = newArticleDao.getFailedReviewCount(contributorId);
        int postRecordCount = newArticleDao.getPostRecordCount(contributorId);
        Map<String, Object> res = new HashMap<>();
        res.put("roughCount", roughCount);
        res.put("beingAuditedCount", beingAuditedCount);
        res.put("failAuditedCount", failAuditedCount);
        res.put("reasonCount", reasonCount);
        res.put("failedReviewCount", failedReviewCount);
        res.put("postRecordCount", postRecordCount);
        return res;
    }

}