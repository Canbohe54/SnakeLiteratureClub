package com.snach.literatureclub.service;

import com.google.gson.Gson;
import com.snach.literatureclub.bean.Article;
import com.snach.literatureclub.common.ArticleAuditStatus;
import com.snach.literatureclub.common.exception.NullFileException;
import com.snach.literatureclub.dao.NewArticleDao;
import com.snach.literatureclub.utils.IdManager;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static com.snach.literatureclub.utils.TokenTools.getPayload;

public interface ContributorService {
    /**
     * 投稿保存或发布
     * 返回稿件基本信息（标题、描述、时间和id）
     *
     * @param token      用于验证是否过期以及获取作者id
     * @param article    投稿基本信息，必须有text_by
     * @param mulArticle 用户上传的文件
     * @return
     */
    Article contribute(String token, Article article, MultipartFile mulArticle);

}

@Transactional(rollbackFor = Exception.class)
@Service
@Mapper
class ContributorServiceImpl implements ContributorService {
    private final NewArticleDao newArticleDao;

    private final IdManager idManager;

    @Autowired
    public ContributorServiceImpl(IdManager manager, NewArticleDao newArticleDao) {
        this.idManager = manager;
        this.newArticleDao = newArticleDao;
    }

    @Override
    public Article contribute(String token, Article article, MultipartFile mulArticle) {
        // 检测token是否合法
//         todo 添加token检测
//        if (!tokenVerify(token)) {
//            throw new InvalidTokenException();
//        }
        if (article.getAuditStatus() != ArticleAuditStatus.ROUGH && mulArticle == null) {
            throw new NullFileException("发布操作文件不能为空。");
        }
        // 获取作者id
        article.setTextBy(getPayload(token, "id"));
        // 原始文件
        try {
            if (mulArticle != null) {
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

            insertArticle(article);
        } else {
            //若已有稿件id，则进行更新
            updateArticle(article);
        }

        return article;
    }

    private void insertArticle(Article article) {
        newArticleDao.insertArticle(article);
        newArticleDao.insertArticleInfo(article);
        newArticleDao.insertArticleRaw(article);
        newArticleDao.insertArticleStatus(article);

        // 插入 tags
        Gson gson = new Gson();
        Map<String, List<String>> tagsMap = gson.fromJson(article.getTags(), HashMap.class);
        for (Map.Entry<String, List<String>> entry : tagsMap.entrySet()) {
            for (String value : entry.getValue()) {
                String tagName = entry.getKey() + ":" + value;
                addTag(article.getId(), tagName);
            }
        }
    }

    public void updateArticle(Article article) {
        newArticleDao.updateArticleRaw(article);
        newArticleDao.updateArticleInfo(article);
        newArticleDao.updateArticle(article);
        newArticleDao.updateArticleStatus(article);

        // 更新tags
        // 将article.tags 转化为["tag1:value1", "tag2:value2", ...]的格式
        List<String> oldTagList = newArticleDao.getTagsByArticleId(article.getId());
        Set<String> oldTagSet = new HashSet<>(oldTagList);
        Gson gson = new Gson();
        Map<String, List<String>> tagsMap = gson.fromJson(article.getTags(), HashMap.class);
        Set<String> newTagSet = new HashSet<>();
        for (Map.Entry<String, List<String>> entry : tagsMap.entrySet()) {
            for (String value : entry.getValue()) {
                String tagName = entry.getKey() + ":" + value;
                newTagSet.add(tagName);
            }
        }

        // 求旧tags和新tags的交集
        Set<String> intersection = new HashSet<>(oldTagSet);
        intersection.retainAll(newTagSet);

        // 找到丢弃的tag并删除
        oldTagSet.removeAll(intersection);
        for (String tag : oldTagSet){
            newArticleDao.deleteArticleTagByIdAndTagName(article.getId(), tag);
        }
        // 找到新增的tag并插入
        newTagSet.removeAll(intersection);
        for (String tag : newTagSet){
            addTag(article.getId(), tag);
        }
    }

    private int addTag(String articleId, String tagName){
        String tagId;
        if (newArticleDao.existTag(tagName) == 0) {
            // 如果不存在tag，则新建tag
            tagId = idManager.generateTagId();
            newArticleDao.insertTag(tagId, tagName);
        } else {
            tagId = newArticleDao.getTagIdByName(tagName);
        }
        return newArticleDao.insertArticleTag(articleId, tagId);
    }
}