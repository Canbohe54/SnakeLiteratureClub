package com.snach.literatureclub.service;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Map;

@Service
public interface ArticleService {
    /**
     * 用户新增稿件，id由时间戳生成，将稿件根据action进行保存
     * 若action为草稿保存则无需审核，为发布则需要审核
     * 返回各事件执行状态
     *
     * @param title       标题
     * @param description 描述
     * @param text        稿件内容
     * @param action      稿件处理事件（1：草稿保存 2：发布 ）
     * @return 保存状态（1：保存成功 2：待审核 0：保存失败）,执行状态 返回格式{ fileStatue: #{INTEGER}, statusMsg: #{STRING} }
     */
    Map<String, Object> addArticle(String title, String description, String text, int action);

    /**
     * 用户更改稿件基础信息，包括标题和描述，根据id进行更新，同时更新修改时间
     *
     * @param id        稿件id
     * @param title     标题
     * @param description 描述
     * @return 保存状态（2：待审核 0：保存失败）,执行状态 返回格式{ fileStatue: #{INTEGER}, statusMsg: #{STRING} }
     */
    Map<String, Object> updateArticle(BigInteger id, String title, String description);

    /**
     * 用户更改稿件详细信息，包括标题、描述和内容，根据id进行更新，同时更新修改时间
     * @param id 稿件id
     * @param title 标题
     * @param description 描述
     * @param text 内容
     * @return 保存状态（1：保存成功 2：待审核 0：保存失败）,执行状态 返回格式{ fileStatue: #{INTEGER}, statusMsg: #{STRING} }
     */
    Map<String, Object> updateArticle(BigInteger id, String title, String description, String text);

    /**
     * 根据id删除稿件
     *
     * @param id 稿件id
     * @return 执行状态 返回格式{ fileStatue: #{INTEGER}, statusMsg: #{STRING} }
     */
    Map<String, Object> deleteArticle(BigInteger id);

    /**
     * 根据作者id查找其稿件，用于创作者界面显示
     *
     * @param contributor_id 作者id
     * @return 作者稿件列表, 执行状态 返回格式{ articles: [#{ARTICLE},...], statusMsg: #{STRING}}
     */
    Map<String, Object> getContributorArticles(BigInteger contributor_id);
}

@Service
@Mapper
class ArticleServiceImpl implements ArticleService {

    @Override
    public Map<String, Object> addArticle(String title, String description, String text, int action) {
        return null;
    }

    @Override
    public Map<String, Object> updateArticle(BigInteger id, String title, String description) {
        return null;
    }

    @Override
    public Map<String, Object> updateArticle(BigInteger id, String title, String description, String text) {
        return null;
    }

    @Override
    public Map<String, Object> deleteArticle(BigInteger id) {
        return null;
    }

    @Override
    public Map<String, Object> getContributorArticles(BigInteger contributor_id) {
        return null;
    }
}