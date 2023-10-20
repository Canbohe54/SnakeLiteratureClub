package com.snach.literatureclub.service;

import com.snach.literatureclub.dao.ArticleDao;
import com.snach.literatureclub.dao.QualificationAuditorDao;
import com.snach.literatureclub.dao.UserDao;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import static com.snach.literatureclub.utils.TokenTools.getPayload;
import static com.snach.literatureclub.utils.TokenTools.tokenVerify;

@Service
@Deprecated
public interface QualificationAuditorService {
    /**
     * 审核员审核后对稿件状态进行更新
     * 返回格式 {statusMsg: #{STRING}}
     * @param token      用于验证是否过期以及获取作者id
     * @param article_id 稿件id
     * @param status     稿件状态（1：保存成功 2：待审核 3.已发布 4.未通过 0：保存失败）
     * @return  执行状态
     */
    Map<String, Object> updateArticleStatus(String token, String article_id, int status);
}

@Mapper
@Service
@Deprecated
class QualificationAuditorServiceImpl implements QualificationAuditorService {
    @Autowired
    private QualificationAuditorDao qualificationAuditorDao;
    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private UserDao userDao;
    @Override
    public Map<String, Object> updateArticleStatus(String token, String article_id, int status) {
        Map<String, Object> res = new HashMap<String, Object>();
        // 检测token是否合法
        if (!tokenVerify(token)) {
            res.put("statusMsg", "Invalid token.");
            return res;
        }
        // 获取审核员id，查询其是否具有权限，无权限则返回"Access denied."
        String q_id = getPayload(token, "id");
        if(userDao.exist(new BigInteger(String.valueOf(4)),q_id)==0){
            res.put("statusMsg", "Access denied.");
            return res;
        }
        articleDao.updateStatus(status,article_id);
        res.put("statusMsg","Success.");
        return res;
    }
}