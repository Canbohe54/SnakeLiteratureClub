package com.snach.literatureclub.service;

import com.snach.literatureclub.bean.Article;
import com.snach.literatureclub.common.ArticleAuditStatus;
import com.snach.literatureclub.common.exception.InvalidTokenException;
import com.snach.literatureclub.common.exception.NullFileException;
import com.snach.literatureclub.dao.ArticleDao;
import com.snach.literatureclub.utils.IdManager;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;

import static com.snach.literatureclub.utils.TokenTools.getPayload;
import static com.snach.literatureclub.utils.TokenTools.tokenVerify;

public interface ContributorService {
    /**
     * 投稿保存或发布
     * 返回稿件基本信息（标题、描述、时间和id）
     * @param token 用于验证是否过期以及获取作者id
     * @param article 投稿基本信息，必须有text_by
     * @param mulArticle 用户上传的文件
     * @return
     */
    Article contribute(String token, Article article, MultipartFile mulArticle);

}
@Transactional(rollbackFor = Exception.class)
@Service
@Mapper
class ContributorServiceImpl implements ContributorService {
    private final ArticleDao articleDao;

    private final IdManager idManager;

    @Autowired
    public ContributorServiceImpl(ArticleDao articleDao, IdManager manager) {
        this.articleDao = articleDao;
        this.idManager = manager;
    }
    @Override
    public Article contribute(String token, Article article, MultipartFile mulArticle) {
        // 检测token是否合法
        if (!tokenVerify(token)) {
            throw new InvalidTokenException();
        }
        if(article.getAuditStatus() != ArticleAuditStatus.ROUGH && mulArticle == null){
            throw new NullFileException("发布操作文件不能为空。");
        }
        // 获取作者id
        article.setTextBy(getPayload(token, "id"));
        // 原始文件
        try {
            if(mulArticle != null) {
                article.setRaw(mulArticle.getBytes());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //修改时间
        Date date = new Date(System.currentTimeMillis());
        article.setTime(date);
        // 稿件id
        if (article.getId() == null || article.getId().equals("null")) {
            //没有稿件id，时间戳生成id
            article.setId(idManager.generateArticleId());
            articleDao.insertArticle(article);
        } else {
            //若已有稿件id，则进行更新
            articleDao.updateArticleDetail(article);
        }

        return article;
    }
}