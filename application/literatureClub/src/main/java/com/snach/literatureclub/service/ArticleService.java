package com.snach.literatureclub.service;

import com.snach.literatureclub.bean.Article;
import com.snach.literatureclub.dao.ArticleDao;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.snach.literatureclub.utils.Tools.generateId;

@Service
public interface ArticleService {
    /**
     * 用户新增稿件，id由时间戳生成，将稿件根据action进行保存
     * 若action为草稿保存则无需审核，为发布则需要审核
     * 返回各事件执行状态
     *
     * @param contributor_id
     * @param title          标题
     * @param description    描述
     * @param text           稿件内容
     * @param action         稿件处理事件（1：草稿保存 2：发布 ）
     * @return 保存状态（1：保存成功 2：待审核 0：保存失败）,执行状态 返回格式{ fileStatue: #{INTEGER}, statusMsg: #{STRING} }
     */
    Map<String, Object> addArticle(String contributor_id, String title, String description, String text, int action);

    /**
     * 用户更改稿件基础信息，包括标题和描述，根据id进行更新，同时更新修改时间
     *
     * @param id          稿件id
     * @param title       标题
     * @param description 描述
     * @return 保存状态（2：待审核 0：保存失败）,执行状态 返回格式{ fileStatue: #{INTEGER}, statusMsg: #{STRING} }
     */
    Map<String, Object> updateArticle(String id, String title, String description);

    /**
     * 用户更改稿件详细信息，包括标题、描述和内容，根据id进行更新，同时更新修改时间
     * 若action为草稿保存则无需审核，为发布则需要审核
     *
     * @param id          稿件id
     * @param title       标题
     * @param description 描述
     * @param text        内容
     * @param action      稿件处理事件（1：草稿保存 2：发布 ）
     * @return 保存状态（1：保存成功 2：待审核 0：保存失败）,执行状态 返回格式{ fileStatue: #{INTEGER}, statusMsg: #{STRING} }
     */
    Map<String, Object> updateArticle(String id, String title, String description, String text, int action);

    /**
     * 根据id删除稿件
     *
     * @param id 稿件id
     * @return 执行状态 返回格式{ statusMsg: #{STRING} }
     */
    Map<String, Object> deleteArticle(String id);

    /**
     * 根据作者id查找其稿件基础信息，包括标题、修改时间和描述，用于创作者界面显示
     *
     * @param contributor_id 作者id
     * @return 作者稿件列表, 执行状态 返回格式{ articles: [#{ARTICLE},...], statusMsg: #{STRING}}
     */
    Map<String, Object> getContributorArticles(String contributor_id);

    /**
     * 根据稿件id查找稿件详细信息，包括标题、描述、修改时间和内容，用于编辑界面显示
     *
     * @param id 稿件id
     * @return 稿件详细信息, 执行状态 返回格式{ article: #{ARTICLE}, statusMsg: #{STRING}}
     */
    Map<String, Object> getArticleById(String id);
}

@Service
@Mapper
class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleDao articleDao;

    @Override
    public Map<String, Object> addArticle(String contributor_id, String title, String description, String text, int action) {
        Map<String, Object> res = new HashMap<String, Object>();
        //时间戳生成id
        String id = generateId("article");

        //修改时间
        //SimpleDateFormat formatter= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());

        //插入article表
        Article article = new Article(id, text, date, "", title, description);
        articleDao.insertArticle(article);
        // 稿件状态更新
        articleDao.insertRelationAndState(contributor_id, id, action);

        res.put("fileStatue", action);
        res.put("statusMsg", "ok");
        return res;
    }

    @Override
    public Map<String, Object> updateArticle(String id, String title, String description) {
        Map<String, Object> res = new HashMap<String, Object>();
        // 修改时间
        Date date = new Date(System.currentTimeMillis());

        // 更新基本信息
        Article article = new Article(id, "", date, "", title, description);
        articleDao.updateArticleInfo(article);

        // 更新稿件状态
        articleDao.updateStatue(id,2);
        res.put("fileStatue", 2);
        res.put("statusMsg", "ok");
        return res;
    }

    @Override
    public Map<String, Object> updateArticle(String id, String title, String description, String text, int action) {
        Map<String, Object> res = new HashMap<String, Object>();

        // 修改时间
        Date date = new Date(System.currentTimeMillis());
        // 更新详细信息
        Article article = new Article(id, text, date, "", title, description);
        articleDao.updateArticleDetail(article);
        // 状态更新
        articleDao.updateStatue(id,action);

        res.put("fileStatue", action);
        res.put("statusMsg", "ok");
        return res;
    }

    @Override
    public Map<String, Object> deleteArticle(String id) {
        Map<String, Object> res = new HashMap<String, Object>();
        articleDao.deleteArticleById(id);
        res.put("statusMsg", "ok");
        return res;
    }

    @Override
    public Map<String, Object> getContributorArticles(String contributor_id) {
        Map<String, Object> res = new HashMap<String, Object>();
        res.put("articles",articleDao.getArticleByContributorId(contributor_id));
        res.put("statusMsg", "ok");
        return res;
    }

    @Override
    public Map<String, Object> getArticleById(String id) {
        Map<String, Object> res = new HashMap<String, Object>();
        res.put("article", articleDao.getArticleById(id).toString());
        res.put("statusMsg", "ok");
        return res;
    }
}