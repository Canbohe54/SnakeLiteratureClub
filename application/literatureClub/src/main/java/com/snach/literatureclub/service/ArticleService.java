package com.snach.literatureclub.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.snach.literatureclub.bean.Article;
import com.snach.literatureclub.dao.ArticleDao;
import com.snach.literatureclub.utils.IdTools;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.snach.literatureclub.utils.TokenTools.getPayload;
import static com.snach.literatureclub.utils.TokenTools.tokenVerify;
import static com.snach.literatureclub.utils.IdTools.generateId;

@Service
public interface ArticleService {
    /**
     * 用户新增稿件，id由时间戳生成，将稿件根据action进行保存
     * 若action为草稿保存则无需审核，为发布则需要审核
     * 返回稿件基本信息（标题、描述、时间和id）和各事件执行状态
     * 返回格式{article_id: #{String}, title: #{String}, description: #{String}, time:#{Date}, fileStatue: #{INTEGER}, statusMsg: #{STRING} }
     *
     * @param token   用于验证是否过期以及获取作者id
     * @param article 稿件信息 id初次创建为null
     * @return 稿件基本信息（标题、描述、时间和id）,保存状态（1：保存成功 2：待审核 0：保存失败）,执行状态
     * @see Article
     */
    Map<String, Object> addArticle(String token, Article article);

    /**
     * 用户更改稿件基础信息，包括标题和描述，根据id进行更新，同时更新修改时间
     * 返回格式{article_id: #{String}, title: #{String}, description: #{String}, time:#{Date}, fileStatue: #{INTEGER}, statusMsg: #{STRING} }
     *
     * @param token       用于验证是否过期以及获取作者id
     * @param id          稿件id
     * @param title       标题
     * @param description 描述
     * @param status      稿件状态
     * @param attr        稿件多值属性，如标签
     * @return 保存状态（2：待审核 0：保存失败）,执行状态 返回格式{ fileStatue: #{INTEGER}, statusMsg: #{STRING} }
     */
    Map<String, Object> updateArticle(String token, String id, String title, String description, int status, String attr);

    /**
     * 用户更改稿件详细信息，包括标题、描述和内容，根据id进行更新，同时更新修改时间
     * 若action为草稿保存则无需审核，为发布则需要审核
     * 返回格式{article_id: #{String}, title: #{String}, description: #{String}, time:#{Date}, fileStatue: #{INTEGER}, statusMsg: #{STRING} }
     *
     * @param token   用于验证是否过期以及获取作者id
     * @param article 稿件详细信息
     * @return 保存状态（1：保存成功 2：待审核 0：保存失败）,执行状态
     * @see Article
     */
    Map<String, Object> updateArticle(String token, Article article);

    /**
     * 根据id删除稿件
     *
     * @param token 用于验证是否过期以及获取作者id
     * @param id    稿件id
     * @return 执行状态 返回格式{ statusMsg: #{STRING} }
     */
    Map<String, Object> deleteArticle(String token, String id);

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

    /**
     * 返回所有稿件的基础信息
     * 返回格式 { articles: [#{ARTICLE},...], statusMsg: #{STRING}}
     *
     * @return 所有的稿件信息的Article对象List
     */
    Map<String, Object> getAllArticles(int pageNum,int pageSize);

    /**
     * 根据关键词搜索稿件
     *
     * @param keyword    关键词
     * @param pageNum
     * @param pageSize
     * @param statusList
     * @return 搜索到的所有稿件信息 返回格式{ articles: [#{Article}, ...], statusMsg: #{String} }
     */
    Map<String, Object> searchArticle(String keyword, String tag, int pageNum, int pageSize, List<Integer> statusList);
}

@Service
@Mapper
class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleDao articleDao;

    @Override
    public Map<String, Object> addArticle(String token, Article article) {
        Map<String, Object> res = new HashMap<String, Object>();
        // 检测token是否合法
        if (!tokenVerify(token)) {
            res.put("statusMsg", "Invalid token.");
            return res;
        }
        // 获取作者id
        String contributor_id = getPayload(token, "id");
        if (article.getId() == null) {
            //没有稿件id，时间戳生成id
            article.setId(generateId(IdTools.Type.ARTICLE));
        } else {
            //若已有稿件id，则进行更新
            return updateArticle(token, article);
        }
        article.setTextBy(contributor_id);
        //修改时间
        //SimpleDateFormat formatter= new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        Date date = new Date(System.currentTimeMillis());
        article.setTime(date);
        //插入article表
        articleDao.insertArticle(article);
        // 稿件投稿者关系更新
        articleDao.insertRelation(contributor_id, article.getId());

        res.put("article_id", article.getId());
        res.put("title", article.getTitle());
        res.put("description", article.getDescription());
        res.put("time", article.getTime());
        res.put("fileStatue", 3);
        res.put("statusMsg", "Success.");
        return res;
    }

    @Override
    public Map<String, Object> updateArticle(String token, String id, String title, String description, int status, String attr) {
        Map<String, Object> res = new HashMap<String, Object>();
        // 检测token是否合法
        if (!tokenVerify(token)) {
            res.put("statusMsg", "Invalid token.");
            return res;
        }
        // 获取作者id,查看该作者是否拥有该稿件，若不拥有则返回"Access denied."
        String contributor_id = getPayload(token, "id");
        if (articleDao.belong(contributor_id, id) == 0) {
            res.put("statusMsg", "Access denied.");
            return res;
        }
        // 修改时间
        Date date = new Date(System.currentTimeMillis());

        // 更新基本信息
        Article article = new Article(id, "", date, "", title, description, status, attr);
        articleDao.updateArticleInfo(article);


        res.put("article_id", id);
        res.put("title", title);
        res.put("description", description);
        res.put("time", date);
        // 稿件状态 1：保存成功 2：待审核 3.已发布 4.未通过 0：保存失败
        res.put("fileStatue", 2);
        res.put("statusMsg", "Success.");
        return res;
    }


    @Override
    public Map<String, Object> updateArticle(String token, Article article) {
        Map<String, Object> res = new HashMap<String, Object>();
        // 检测token是否合法
        if (!tokenVerify(token)) {
            res.put("statusMsg", "Invalid token.");
            return res;
        }
        // 获取作者id,查看该作者是否拥有该稿件，若不拥有则返回"Access denied."
        String contributor_id = getPayload(token, "id");
        if (articleDao.belong(contributor_id, article.getId()) == 0) {
            res.put("statusMsg", "Access denied.");
            return res;
        }
        // 稿件状态 1：保存成功 2：待审核 3.已发布 4.未通过 0：保存失败
        article.setStatus(article.getStatus());
        // 修改时间
        Date date = new Date(System.currentTimeMillis());
        article.setTime(date);
        // 更新详细信息
        articleDao.updateArticleDetail(article);

        res.put("article_id", article.getId());
        res.put("title", article.getTitle());
        res.put("description", article.getDescription());
        res.put("time", article.getTime());
        res.put("fileStatue", article.getStatus());
        res.put("statusMsg", "Success.");
        return res;
    }

    @Override
    public Map<String, Object> deleteArticle(String token, String id) {
        Map<String, Object> res = new HashMap<String, Object>();
        // 检测token是否合法
        if (!tokenVerify(token)) {
            res.put("statusMsg", "Invalid token.");
            return res;
        }
        // 获取作者id
        String contributor_id = getPayload(token, "id");
        if (articleDao.belong(contributor_id, id) == 0) {
            res.put("statusMsg", "Access denied");
            return res;
        }
        articleDao.deleteArticleById(id);
        res.put("statusMsg", "Success.");
        return res;
    }

    @Override
    public Map<String, Object> getContributorArticles(String contributor_id) {
        Map<String, Object> res = new HashMap<String, Object>();
        res.put("articles", articleDao.getArticleByContributorId(contributor_id));
        res.put("statusMsg", "Success.");
        return res;
    }

    @Override
    public Map<String, Object> getArticleById(String id) {
        Map<String, Object> res = new HashMap<String, Object>();
        res.put("article", articleDao.getArticleById(id));
        res.put("statusMsg", "Success.");
        return res;
    }

    @Override
    public Map<String, Object> getAllArticles(int pageNum,int pageSize) {
        Map<String, Object> res = new HashMap<String, Object>();

        PageHelper.startPage(pageNum,pageSize);
        PageInfo<Article> pageInfo = new PageInfo<>(articleDao.getAllArticles());

        res.put("articles", pageInfo);
        res.put("statusMsg", "Success.");
        return res;
    }

    @Override
    public Map<String, Object> searchArticle(String keyword, String tag, int pageNum, int pageSize, List<Integer> statusList) {
        Map<String, Object> res = new HashMap<>();
        PageHelper.startPage(pageNum,pageSize);
        if (tag == null) {
            res.put("articles", new PageInfo<>(articleDao.getArticlesByKeyword(keyword,statusList)));
        } else {
            res.put("articles", new PageInfo<>(articleDao.getArticlesByKeywordAndTag(keyword, tag)));
        }
        res.put("statusMsg", "Success.");
        return res;
    }
}