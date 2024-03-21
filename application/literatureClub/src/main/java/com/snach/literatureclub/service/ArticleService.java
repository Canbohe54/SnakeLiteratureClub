package com.snach.literatureclub.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.snach.literatureclub.bean.Article;
import com.snach.literatureclub.common.ArticleStatus;
import com.snach.literatureclub.common.exception.InvalidTokenException;
import com.snach.literatureclub.common.exception.NullFileException;
import com.snach.literatureclub.dao.ArticleDao;
import com.snach.literatureclub.utils.IdTools;
import com.snach.literatureclub.utils.QiniuKodoUtil;
import com.snach.literatureclub.utils.SensitiveWordsTools;
import com.snach.literatureclub.utils.TextExtractionTools;
import org.apache.ibatis.annotations.Mapper;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
     * @param token     用于验证是否过期以及获取作者id
     * @param article   稿件信息 id初次创建为null
     * @param imageList 文献对应的参考插图列表
     * @return 稿件基本信息（标题、描述、时间和id）,保存状态（1：保存成功 2：待审核 0：保存失败）,执行状态
     * @see Article
     */
    Map<String, Object> addArticle(String token, List<MultipartFile> imageList, Article article);

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
    Map<String, Object> updateArticle(String token, List<MultipartFile> imageList, Article article);

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
     * @param pageNum
     * @param pageSize
     * @param statusList
     * @return 作者稿件列表, 执行状态 返回格式{ articles: [#{ARTICLE},...], statusMsg: #{STRING}}
     */
    Map<String, Object> getContributorArticles(String contributor_id, int pageNum, int pageSize, List<Integer> statusList);

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
    Map<String, Object> getAllArticles(int pageNum, int pageSize);

    /**
     * 根据关键词搜索稿件
     *
     * @param keyword    关键词
     * @param pageNum
     * @param pageSize
     * @param statusList
     * @return 搜索到的所有稿件信息 返回格式{ articles: [#{Article}, ...], statusMsg: #{String} }
     */
    Map<String, Object> searchArticle(String keyword, String tag, int pageNum, int pageSize, List<ArticleStatus> statusList);

    /**
     *
     */
    Article contribute(String token, Article article, MultipartFile mulArticle);

    Article getArticleFileById(String articleId);

    String getLatestApprovalArticleById(String articleId);

    void lockArticleById(String articleId, String lockedBy);
    void getPermissions(String articleId, String requester);

    /**
     * 返回文章的敏感词审核结果
     * @param token
     * @param id
     * @return 返回文章的敏感词审核结果
     */
    Map<String, Object> SensitiveWordReview(String token, String id) throws IOException;
    Map<String, Object> SensitiveWordReview2(String token, String id) throws IOException;
}

@Transactional(rollbackFor = Exception.class)
@Service
@Mapper
class ArticleServiceImpl implements ArticleService {
    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private QiniuKodoUtil qiniuKodoUtil;

    @Autowired
    private SensitiveWordsTools sensitiveWordsTools;

    @Autowired
    private TextExtractionTools textExtractionTools;
    @Override
    public Map<String, Object> addArticle(String token, List<MultipartFile> imageList, Article article) {
        // 检测token是否合法
        if (!tokenVerify(token)) {
            throw new InvalidTokenException();
        }
        Map<String, Object> res = new HashMap<String, Object>();

        // 获取作者id
        String contributor_id = getPayload(token, "id");

        if (article.getId() == null || article.getId().equals("null")) {
            //没有稿件id，时间戳生成id
            article.setId(generateId(IdTools.Type.ARTICLE));
        } else {
            //若已有稿件id，则进行更新
            return updateArticle(token, imageList, article);
        }
        article.setTextBy(contributor_id);
        //修改时间
        Date date = new Date(System.currentTimeMillis());
        article.setTime(date);

        if (imageList != null) {
            //上传图片
            StringBuilder allImageURL = new StringBuilder("[");
            for (int i = 0; i < imageList.size(); i++) {
                try {
                    MultipartFile file = imageList.get(i);
                    File f = new File(file.getOriginalFilename());
                    BufferedOutputStream out = new BufferedOutputStream(
                            new FileOutputStream(f));
                    out.write(file.getBytes());
                    out.flush();
                    out.close();

                    String imageName = article.getId() + "/" + f.getName();
                    qiniuKodoUtil.upload(f, "articles/" + imageName);
                    allImageURL.append("\"").append(qiniuKodoUtil.getFileUrl(article.getId())).append("\"");
                    if (i != imageList.size() - 1) {
                        allImageURL.append(",");
                    }
                    File tem = new File(f.toURI());
                    if (!f.delete())
                        System.out.println("删除失败");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            allImageURL.append("]");
        }

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
        Article article = new Article();
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
    public Map<String, Object> updateArticle(String token, List<MultipartFile> imageList, Article article) {
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
    public Map<String, Object> getContributorArticles(String contributor_id, int pageNum, int pageSize, List<Integer> statusList) {
        Map<String, Object> res = new HashMap<String, Object>();
        PageHelper.startPage(pageNum, pageSize);
        res.put("articles", new PageInfo<>(articleDao.getArticleByContributorId(contributor_id, statusList)));
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
    public Map<String, Object> getAllArticles(int pageNum, int pageSize) {
        Map<String, Object> res = new HashMap<String, Object>();

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
    public Article contribute(String token, Article article, MultipartFile mulArticle) {
        // 检测token是否合法
        if (!tokenVerify(token)) {
            throw new InvalidTokenException();
        }
        if(mulArticle == null){
            throw new NullFileException("发布操作文件不能为空。");
        }
        // 获取作者id
        article.setTextBy(getPayload(token, "id"));
        // 原始文件
        try {
            article.setRaw(mulArticle.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 稿件id
        if (article.getId() == null || article.getId().equals("null")) {
            //没有稿件id，时间戳生成id
            article.setId(generateId(IdTools.Type.ARTICLE));
        } else {
            //若已有稿件id，则进行更新
//            return updateArticle(token, imageList, article);
        }
        //修改时间
        Date date = new Date(System.currentTimeMillis());
        article.setTime(date);

        articleDao.insertArticle(article);
        return article;
    }

    @Override
    public Article getArticleFileById(String articleId) {
        return articleDao.getArticleFileById(articleId);
    }

    @Override
    public String getLatestApprovalArticleById(String articleId) {
        String latestApprovalArticleUrl = articleDao.getLatestApprovalArticleUrlById(articleId);

        return latestApprovalArticleUrl;
    }

    @Override
    public void lockArticleById(String articleId, String lockedBy) {

    }

    @Override
    public void getPermissions(String articleId, String requester) {

    }

    /**
     * 对文章内容进行敏感词审核。
     * @param token 用户的令牌，用于验证用户身份和权限。
     * @param id 文章的唯一标识符，用于从数据库中获取文章内容。
     * @return 返回一个包含审核结果的Map，其中键为审核相关的指标或错误信息，值为对应的结果或异常对象。返回的结果包括状态消息、敏感词数量和敏感词列表。
     * @throws InvalidTokenException 如果提供的token验证失败，则抛出此异常。
     */
    @Override
    public Map<String, Object> SensitiveWordReview(String token, String id) throws IOException {
        // 验证用户令牌的合法性
        if (!tokenVerify(token)) {
            throw new InvalidTokenException();
        }
        // 通过文章ID从数据库获取文章对象
        Article article = articleDao.getArticleById(id);
        // 获取文章原始内容的二进制数据
        byte[] fileContent = article.getRaw();
        // 获取文章原始文件的格式
        String format = article.getFileType();
//        System.out.println(article.getFileType());
        String txt = null;
        if(format.equals("text")){
            // 将二进制数据转为字符串
            txt = new String(fileContent, StandardCharsets.UTF_8);
        }
        else {
            // 将fileContent写入临时文件
            File tempFile = File.createTempFile("tempFile", ".docx");
            try {
                Files.write(tempFile.toPath(), fileContent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // 提取文件中的文本内容
            txt = TextExtractionTools.extractTextFromDocx(tempFile.getCanonicalPath());
            // 删除临时文件
            tempFile.delete();
        }
        // 使用敏感词检测工具判断文章中是否包含敏感词
        Boolean flag = sensitiveWordsTools.judgeSensitivityWord(txt);
        Map<String, Object> res = new HashMap<>();
        // 根据敏感词检测结果填充返回结果
        if(flag){
            // 文章包含敏感词
            res.put("statusMsg", "Success.");
            res.put("num", sensitiveWordsTools.FindAllWords(txt).size());
            res.put("sensitiveWords", sensitiveWordsTools.FindAllWords(txt));
        }
        else{
            // 文章不包含敏感词
            res.put("statusMsg", "Success.");
            res.put("num", 0);
        }
        return res;
   }

    public Map<String, Object> SensitiveWordReview2(String token, String id) throws IOException {
        // 验证用户令牌的合法性
        if (!tokenVerify(token)) {
            throw new InvalidTokenException();
        }
        // 通过文章ID从数据库获取文章对象
        Article article = articleDao.getArticleById(id);
        // 获取文章原始内容的二进制数据
        byte[] fileContent = article.getRaw();
        // 获取文章原始文件的格式
        String format = article.getFileType();
//        System.out.println(article.getFileType());
        String txt = null;
        if(format.equals("text")){
            // 将二进制数据转为字符串
            txt = new String(fileContent, StandardCharsets.UTF_8);
        }
        else {
            // 将fileContent写入临时文件
            File tempFile = File.createTempFile("tempFile", ".docx");
            try {
                Files.write(tempFile.toPath(), fileContent);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            // 提取文件中的文本内容
            txt = TextExtractionTools.extractTextFromDocx(tempFile.getCanonicalPath());
            // 删除临时文件
            tempFile.delete();
        }
        // 使用敏感词检测工具判断文章中是否包含敏感词
        Boolean flag = sensitiveWordsTools.judgeSensitivityWord(txt);
        Map<String, Object> res = new HashMap<>();
        // 根据敏感词检测结果填充返回结果
        if(flag){
            // 文章包含敏感词
            res.put("statusMsg", "Success.");
            res.put("num", sensitiveWordsTools.FindAllWords2(txt).size());
            res.put("sensitiveWords", sensitiveWordsTools.FindAllWords2(txt));
        }
        else{
            // 文章不包含敏感词
            res.put("statusMsg", "Success.");
            res.put("num", 0);
        }
        return res;
    }
}