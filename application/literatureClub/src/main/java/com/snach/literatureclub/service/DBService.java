package com.snach.literatureclub.service;

import com.snach.literatureclub.bean.like.ArticleLike;
import com.snach.literatureclub.bean.like.LikesAndViewCount;
import com.snach.literatureclub.common.CONSTANT;
import com.snach.literatureclub.dao.ArticleLikeDao;
import com.snach.literatureclub.dao.ArticleLikeAndViewCountDao;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;

public interface DBService {
    /**
     * 保存点赞记录
     * @param articleLike
     * @return
     */
    Boolean saveLikeRecode(ArticleLike articleLike);

    Boolean saveLikeAndViewCountRecode(LikesAndViewCount likesAndViewCount);
    /**
     * 更新点赞记录
     * @param articleLike
     * @return
     */
    Boolean updateLikeRecode(ArticleLike articleLike);
    /**
     * 根据内容的id查询点赞列表（即查询都谁给这个内容点赞过）
     * @param articleId 内容的id
     * @return
     */
    List<ArticleLike> getLikedListByArticleId(String articleId);

    /**
     * 根据点赞人的id查询点赞列表（即查询这个人都给哪些内容点赞过）
     * @param articleId
     * @return
     */
    List<ArticleLike> getLikedListByLikeUserId(String articleId);

    /**
     * 通过被点赞内容和点赞人id查询是否存在点赞记录
     * @param infoId
     * @param likeUserId
     * @return
     */
    ArticleLike getByArticleIdAndLikeUserId(String infoId, String likeUserId);

    /**
     * 将Redis里的点赞数据存入数据库中,True 表示还需要进一步持久化， False表示数据库中已存在该数据，无需进一步持久化
     */
    void transLikedFromRedis2DB();

    /**
     * 将Redis中的点赞数量数据存入数据库
     */
    void transLikedCountFromRedis2DB();
    void transViewCountFromRedis2DB();

    int getLikeCountByArticleIdFromDB(String articleId);

    LikesAndViewCount getLikeAndViewCountByArticleIdFromDB(String articleId);
}

@Transactional(rollbackFor = Exception.class)
@Service
@Slf4j
class DBServiceImpl implements DBService {
    @Autowired
    private RedisService redisService;
    @Autowired
    private ArticleLikeDao articleLikeDao;
    @Autowired
    private ArticleLikeAndViewCountDao articleLikeAndViewCountDao;
    @Autowired
    private Sid sid;// Id生成器，利用idWorker产生唯一（不重复）自增式的id，可以根据需求选用其他方式

//    @PreDestroy
//    public void destroy() {
//        transLikedFromRedis2DB();
//        transLikedCountFromRedis2DB();
//    }

    @Override
    public Boolean saveLikeRecode(ArticleLike articleLike) {
        int rows = articleLikeDao.insert(articleLike);
        return rows > 0;
    }

    @Override
    public Boolean saveLikeAndViewCountRecode(LikesAndViewCount likesAndViewCount) {
        int rows = articleLikeAndViewCountDao.insertLikeAndViewCount(likesAndViewCount);
        return rows > 0;
    }

    @Override
    public Boolean updateLikeRecode(ArticleLike articleLike) {
        String articleId = articleLike.getArticle_id();
        String userId = articleLike.getUser_id();
        int rows = articleLikeDao.update(articleId, userId, articleLike.getStatus());
        return rows > 0;
    }

    @Override
    public List<ArticleLike> getLikedListByArticleId(String articleId) {
        // 获取内容的id查询点赞列表
        List<ArticleLike> result = new LinkedList<>();
        result = articleLikeDao.getLikedListByArticleId(articleId);
        return result;
    }

    @Override
    public List<ArticleLike> getLikedListByLikeUserId (String likeUserId) {
        List<ArticleLike> result = new LinkedList<>();
        // 获取用户的id查询点赞列表
        result = articleLikeDao.getLikedListByLikeUserId(likeUserId);
        return result;
    }

    @Override
    public ArticleLike getByArticleIdAndLikeUserId (String articleId, String userId) {
        try{
            ArticleLike articleLike = articleLikeDao.getByArticleIdAndLikeUserId(articleId, userId);
            return articleLike;
        }catch (Exception e){
            return null;
        }
    }

    @Override
    public void transLikedFromRedis2DB() {
        // 批量获取缓存中的点赞数据
        List<ArticleLike> list = redisService.getLikedDataFromRedis();
        if (CollectionUtils.isEmpty(list))// 为空，不写入
        {
            return;
        }

        for (ArticleLike item: list){
            ArticleLike articleLike_DB = getByArticleIdAndLikeUserId(item.getArticle_id(), item.getUser_id());// 在数据库中查询
            if (articleLike_DB == null) {// 无记录，新增
                item.setLike_id(sid.nextShort());
                if(!saveLikeRecode(item)){
                    log.info("新增点赞数据失败！");
                    return;
                    // System.out.println("缓存记录写入数据库失败！请重试");
                }
            }else {// 有记录，更新
                // 判断数据库中点赞状态与缓存中点赞状态一致性
                if (articleLike_DB.getStatus()==item.getStatus()){// 一致，无需持久化

                }else{// 不一致
                    if (articleLike_DB.getStatus()== CONSTANT.LikedStatusEum.LIKE.getCode()){// 在数据库中已经是点赞，则取消点赞
                        // 之前是点赞，现在改为取消点赞 1.设置更改status
                        articleLike_DB.setStatus(CONSTANT.LikedStatusEum.UNLIKE.getCode());
                    } else if (articleLike_DB.getStatus()== CONSTANT.LikedStatusEum.UNLIKE.getCode()) {// 未点赞，则点赞，修改点赞状态
                        articleLike_DB.setStatus(CONSTANT.LikedStatusEum.LIKE.getCode());
                    }
                    if(!updateLikeRecode(articleLike_DB)){// 更新点赞数据
                        return;
                        // System.out.println("缓存记录更新数据库失败！请重试");
                    }
                }
            }
        }
    }

    @Override
    public void transLikedCountFromRedis2DB() {
        // 获取缓存中内容的点赞数列表
        List<LikesAndViewCount> list = redisService.getLikedCountListFromRedis();
        if (CollectionUtils.isEmpty(list))// 为空
        {
            return;
        }

        for (LikesAndViewCount item: list){
            // 获取数据库中的内容点赞数
            LikesAndViewCount likesAndViewCount_DB = articleLikeAndViewCountDao.getByArticleId(item.getArticle_id());
            if (likesAndViewCount_DB != null) {// 更新点赞数
//                Integer likeCount = likesAndViewCount_DB.getLike_count() + item.getLike_count();
                Integer likeCount = item.getLike_count();
                likesAndViewCount_DB.setLike_count(likeCount);
                int rows = articleLikeAndViewCountDao.updateLikeCount(likesAndViewCount_DB.getArticle_id(), likesAndViewCount_DB.getLike_count());
                if (rows <= 0) {
                    log.info("更新点赞数失败！");
                }
            }
            else{
                if (!saveLikeAndViewCountRecode(item)){
                    return;
                }
            }
        }
    }

    @Override
    public void transViewCountFromRedis2DB() {
        // 获取缓存中内容的浏览数列表
        List<LikesAndViewCount> list = redisService.getViewCountListFromRedis();
        if (CollectionUtils.isEmpty(list))// 为空
        {
            return;
        }

        for (LikesAndViewCount item: list){
            // 获取数据库中的内容浏览数
            LikesAndViewCount likesAndViewCount_DB = articleLikeAndViewCountDao.getByArticleId(item.getArticle_id());
            if (likesAndViewCount_DB != null) {// 更新浏览数
//                Integer likeCount = likesAndViewCount_DB.getLike_count() + item.getLike_count();
                Integer viewCount = item.getView_count();
                likesAndViewCount_DB.setView_count(viewCount);
                int rows = articleLikeAndViewCountDao.updateViewCount(likesAndViewCount_DB.getArticle_id(), likesAndViewCount_DB.getView_count());
                if (rows <= 0) {
                    log.info("更新浏览数失败！");
                }
            }
            else{
                if (!saveLikeAndViewCountRecode(item)){
                    log.info("新增文章浏览数失败！");
                    return;
                }
            }
        }
    }

    @Override
    public int getLikeCountByArticleIdFromDB(String articleId) {
        LikesAndViewCount likesAndViewCount = articleLikeAndViewCountDao.getByArticleId(articleId);
        if(likesAndViewCount !=null){
            return likesAndViewCount.getLike_count();
        }
        else{
            return 0;
        }
    }

    @Override
    public LikesAndViewCount getLikeAndViewCountByArticleIdFromDB(String articleId) {
        LikesAndViewCount likesAndViewCount = articleLikeAndViewCountDao.getByArticleId(articleId);
        if(likesAndViewCount !=null){
            return likesAndViewCount;
        }
        else{
            return null;
        }
    }
}
