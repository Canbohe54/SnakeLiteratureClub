package com.snach.literatureclub.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.snach.literatureclub.bean.Article;
import com.snach.literatureclub.common.ArticleStatus;
import com.snach.literatureclub.common.exception.*;
import com.snach.literatureclub.dao.ArticleDao;
import com.snach.literatureclub.utils.QiniuKodoUtil;
import com.snach.literatureclub.utils.SensitiveWordsTools;
import com.snach.literatureclub.utils.TextExtractionTools;
import com.snach.literatureclub.utils.*;
import com.snach.literatureclub.utils.redis.ArticleLocker;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.snach.literatureclub.utils.TokenTools.getPayload;
import static com.snach.literatureclub.utils.TokenTools.tokenVerify;

@Service
public interface ArticleService {
    Article getArticleById(String articleId);

    /**
     * 根据作者id查找其稿件基础信息，包括标题、修改时间和描述，用于创作者界面显示
     *
     * @param contributorId 作者id
     * @param pageNum
     * @param pageSize
     * @param statusList
     * @return 作者稿件列表, 执行状态
     */
    // TODO: return type (need: List<Article>); parameter statusList type (need: List<ArticleStatus>);
    Map<String, Object> getContributorArticles(String contributorId, int pageNum, int pageSize, List<Integer> statusList);

    Article getArticleFileById(String articleId);

    String getLatestApprovalArticleById(String articleId);

    PageInfo getReceivedArticleById(String auditorId, int pageNum, int pageSize);

    /**
     * 返回所有稿件的基础信息
     *
     * @return 所有的稿件信息的Article对象List
     */
    Map<String, Object> getAllArticles(int pageNum, int pageSize);

    /**
     * 根据关键词搜索稿件
     *
     * @param keyword    关键词
     * @param pageNum
     * @param pageSize
     * @param statusList
     * @return 搜索到的所有稿件信息
     */
    // TODO: return type (need: List<Article>);
    Map<String, Object> searchArticle(String keyword, String tag, int pageNum, int pageSize, List<ArticleStatus> statusList);

    /**
     * 根据id删除稿件
     *
     * @param token     用于验证是否过期以及获取作者id
     * @param articleId 稿件id
     * @return 执行状态
     */
    Map<String, Object> deleteArticleById(String token, String articleId);

    /**
     * 返回文章的敏感词审核结果
     *
     * @param token
     * @param articleId
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
    ArticleServiceImpl(ArticleDao articleDao, ArticleLocker articleLocker) {
        this.articleDao = articleDao;
        this.articleLocker = articleLocker;
    }

    @Override
    public Article getArticleById(String articleId) {
        return articleDao.getArticleById(articleId);
    }

    @Override
    public Map<String, Object> getContributorArticles(String contributorId, int pageNum, int pageSize, List<Integer> statusList) {
        Map<String, Object> res = new HashMap<>();
        PageHelper.startPage(pageNum, pageSize);
        res.put("articles", new PageInfo<>(articleDao.getArticleByContributorId(contributorId, statusList)));
        res.put("statusMsg", "Success.");
        return res;
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
    public PageInfo getReceivedArticleById(String auditorId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        return new PageInfo<>(articleDao.getArticleByReceivedBy(auditorId));
    }

    @Override
    public Map<String, Object> getAllArticles(int pageNum, int pageSize) {
        Map<String, Object> res = new HashMap<>();

        PageHelper.startPage(pageNum, pageSize);
        PageInfo<Article> pageInfo = new PageInfo<>(articleDao.getAllArticles());

        res.put("articles", pageInfo);
        res.put("statusMsg", "Success.");
        return res;
    }

    @Override
    public Map<String, Object> searchArticle(String keyword, String tag, int pageNum, int pageSize, List<ArticleStatus> statusList) {
        Map<String, Object> res = new HashMap<>();
        PageHelper.startPage(pageNum, pageSize);
        if (tag == null) {
            res.put("articles", new PageInfo<>(articleDao.getArticlesByKeyword(keyword, statusList)));
        } else {
            res.put("articles", new PageInfo<>(articleDao.getArticlesByKeywordAndTag(keyword, tag)));
        }
        res.put("statusMsg", "Success.");
        return res;
    }

    @Override
    public Map<String, Object> deleteArticleById(String token, String articleId) {
        Map<String, Object> res = new HashMap<>();
        // 检测token是否合法
        if (!tokenVerify(token)) {
            res.put("statusMsg", "Invalid token.");
            return res;
        }
        // 获取作者id
        String contributor_id = getPayload(token, "id");
        if (articleDao.belong(contributor_id, articleId) == 0) {
            res.put("statusMsg", "Access denied");
            return res;
        }
        articleDao.deleteArticleById(articleId);
        res.put("statusMsg", "Success.");
        return res;
    }

    /**
     * 对文章内容进行敏感词审核。
     *
     * @param token 用户的令牌，用于验证用户身份和权限。
     * @param id    文章的唯一标识符，用于从数据库中获取文章内容。
     * @return 返回一个包含审核结果的Map，其中键为审核相关的指标或错误信息，值为对应的结果或异常对象。返回的结果包括状态消息、敏感词数量和敏感词列表。
     * @throws InvalidTokenException        如果提供的token验证失败，则抛出此异常。
     * @throws UnsupportedFileTypeException throws if the article raw file format is not supported.
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
        Article article = articleDao.getArticleById(id);
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
            articleLocker.lock(articleId, expire, authorId, TokenTools.getPayload(lockedBy, "id"));
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