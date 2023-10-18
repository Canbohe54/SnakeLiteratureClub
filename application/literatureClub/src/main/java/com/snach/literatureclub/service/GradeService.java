package com.snach.literatureclub.service;

import com.snach.literatureclub.bean.Grade;
import com.snach.literatureclub.dao.GradeDao;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public interface GradeService {
    /**
     * 专家对稿件的评分和评价保存
     * @param article_id
     * @param expert_id
     * @param grade_expr
     * @param grade_struct
     * @param grade_theme
     * @return
     */
    Map<String,Object> addGrade(String article_id,String expert_id,int grade_expr,int grade_struct,int grade_theme,String advice);

    /**
     * 根据专家id和稿件id删除评分
     * @param expert_id
     * @param article_id
     * @return
     */
    Map<String,Object> deleteGrade(String expert_id,String article_id);

    /**
     *更新评分
     * @param article_id
     * @param expert_id
     * @param grade_expr
     * @param grade_struct
     * @param grade_theme
     * @param advice
     * @return
     */
    Map<String,Object> updateGrade(String article_id,String expert_id,int grade_expr,int grade_struct,int grade_theme,String advice);

    /**
     * 根据稿件id获取全部评分信息
     * @param article_id
     * @return
     */
    Map<String,Object> getGradeByArticle_id(String article_id);

    /**
     * 根据专家id获取该专家的全部评分信息
     * @param expert_id
     * @return
     */
    Map<String,Object> getGradeByExpert_id(String expert_id);

    /**
     * 根据专家id和稿件id获取对应的评分信息
     * @param expert_id
     * @param article_id
     * @return
     */
    Map<String ,Object> getGradeByExpertIdAndArticleId(String expert_id,String article_id);
}

@Service
@Mapper
class GradeServiceImpl implements GradeService {
    @Autowired
    private GradeDao gradeDao;

    @Override
    public Map<String, Object> addGrade(String article_id, String expert_id, int grade_expr, int grade_struct, int grade_theme, String advice) {
        Map<String, Object> res = new HashMap<String, Object>();
        int grade_all = grade_expr+grade_struct+grade_theme;
        Grade grade = new Grade(article_id,expert_id,grade_expr,grade_struct,grade_theme,grade_all,advice);
        gradeDao.insertGrade(grade);
        res.put("article_id", article_id);
        res.put("expert_id", expert_id);
        res.put("grade_expr", grade_expr);
        res.put("grade_struct", grade_struct);
        res.put("grade_theme", grade_theme);
        res.put("grade_all", grade_all);
        res.put("advice",advice);
        res.put("statusMsg", "ok");
        return res;
    }

    @Override
    public Map<String, Object> deleteGrade(String expert_id, String article_id) {
        Map<String, Object> res = new HashMap<String, Object>();
        gradeDao.deleteGradeByExpertIdAndArticleId(expert_id,article_id);
        res.put("statusMsg", "success");
        return res;
    }

    @Override
    public Map<String, Object> updateGrade(String article_id, String expert_id, int grade_expr, int grade_struct, int grade_theme, String advice) {
        Map<String, Object> res = new HashMap<String, Object>();
        int grade_all = grade_expr+grade_struct+grade_theme;
        Grade grade = new Grade(article_id,expert_id,grade_expr,grade_struct,grade_theme,grade_all,advice);
        gradeDao.updateGrade(grade);
        res.put("article_id", article_id);
        res.put("expert_id", expert_id);
        res.put("grade_expr", grade_expr);
        res.put("grade_struct", grade_struct);
        res.put("grade_theme", grade_theme);
        res.put("grade_all", grade_all);
        res.put("advice",advice);
        res.put("statusMsg", "ok");
        return res;
    }

    @Override
    public Map<String, Object> getGradeByArticle_id(String article_id) {
        Map<String, Object> res = new HashMap<String, Object>();
        res.put("grades", gradeDao.getGradeByArticle_id(article_id));
        res.put("statusMsg", "success");
        return res;
    }

    @Override
    public Map<String, Object> getGradeByExpert_id(String expert_id) {
        Map<String, Object> res = new HashMap<String, Object>();
        res.put("grades", gradeDao.getGradeByExpert_id(expert_id));
        res.put("statusMsg", "success");
        return res;
    }

    @Override
    public Map<String, Object> getGradeByExpertIdAndArticleId(String expert_id, String article_id) {
        Map<String, Object> res = new HashMap<String, Object>();
        res.put("grade", gradeDao.getGradeByExpertIdAndArticleId(expert_id,article_id));
        res.put("statusMsg", "success");
        return res;
    }
}


