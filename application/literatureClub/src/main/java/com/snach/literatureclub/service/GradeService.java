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

import static com.snach.literatureclub.utils.TokenTools.tokenVerify;

@Service
public interface GradeService {
    /**
     * 专家对稿件的评分和评价保存
     *
     * @param token        token
     * @param article_id   稿件id
     * @param expert_id    专家id
     * @param grade_expr   文字表达成绩
     * @param grade_struct 文章结构成绩
     * @param grade_theme  文章主题成绩
     * @param advice       专家评价（建议）
     * @return 返回格式{article_id: #{String}, expert_id: #{String},
     * grade_expr: #{INTEGER}, grade_struct:#{INTEGER}, grade_theme: #{INTEGER},grade_all:#{Integer},
     * advice:#{String},statusMsg: #{STRING} }
     */
    Map<String, Object> addGrade(String token, String article_id, String expert_id, int grade_expr, int grade_struct, int grade_theme, String advice);

    /**
     * 根据专家id和稿件id删除评分
     *
     * @param expert_id  专家id
     * @param article_id 稿件id
     * @return 返回格式{statusMsg: #{STRING}}
     */
    Map<String, Object> deleteGrade(String token, String expert_id, String article_id);

    /**
     * 更新评分
     *
     * @param article_id   稿件id
     * @param expert_id    专家id
     * @param grade_expr   文字表达成绩
     * @param grade_struct 文章结构成绩
     * @param grade_theme  文章主题成绩
     * @param advice       专家评价（建议）
     * @return 返回格式{article_id: #{String}, expert_id: #{String},
     * grade_expr: #{INTEGER}, grade_struct:#{INTEGER}, grade_theme: #{INTEGER},grade_all:#{Integer},
     * advice:#{String},statusMsg: #{STRING} }
     */
    Map<String, Object> updateGrade(String token, String article_id, String expert_id, int grade_expr, int grade_struct, int grade_theme, String advice);

    /**
     * 检查评分是否存在
     *
     * @param expert_id
     * @param article_id
     * @return 评分数
     */
    Map<String, Object> existGrade(String expert_id, String article_id);

    /**
     * 根据稿件id获取全部评分信息
     *
     * @param article_id 稿件id
     * @return 返回格式{grades:#{List<Grade>,statusMsg: #{STRING}}
     */
    Map<String, Object> getGradeByArticle_id(String article_id);

    /**
     * 根据专家id获取该专家的全部评分信息
     *
     * @param expert_id 专家id
     * @return 返回格式{grades:#{List<Grade>,statusMsg: #{STRING}}
     */
    Map<String, Object> getGradeByExpert_id(String expert_id);

    /**
     * 根据专家id和稿件id获取对应的评分信息
     *
     * @param expert_id  专家id
     * @param article_id 稿件id
     * @return 返回格式{grade:#{Grade},statusMsg: #{STRING}}
     */
    Map<String, Object> getGradeByExpertIdAndArticleId(String expert_id, String article_id);

    /**
     * 根据稿件id获取该稿件的平均分
     *
     * @param article_id 稿件id
     * @return 返回格式{avg_garde:{Integer},statusMsg: #{STRING}}
     */
    Map<String, Object> getAvgGradeByArticleId(String article_id);

    /**
     * 根据稿件id获取稿件的评分专家人数
     *
     * @param article_id 稿件id
     * @return 返回格式{count:{Integer},statusMsg: #{STRING}}
     */
    Map<String, Object> getCountByArticleId(String article_id);

    /**
     * 根据专家id获取该专家的评分列表，根据总分由高到低排序
     *
     * @param expert_id 专家id
     * @return 返回格式{grades:#{List<Grade>,statusMsg: #{STRING}}
     */
    Map<String, Object> getSortByGradeAll(String expert_id);
}

@Service
@Mapper
class GradeServiceImpl implements GradeService {
    @Autowired
    private GradeDao gradeDao;

    @Override
    public Map<String, Object> addGrade(String token, String article_id, String expert_id, int grade_expr, int grade_struct, int grade_theme, String advice) {
        Map<String, Object> res = new HashMap<String, Object>();
        // 检测token是否合法
        if (!tokenVerify(token)) {
            res.put("statusMsg", "Invalid token.");
            return res;
        }
        if (gradeDao.existGrade(expert_id, article_id) != 0) {
            int grade_all = grade_expr + grade_struct + grade_theme;
            Grade grade = new Grade(expert_id, article_id, grade_expr, grade_struct, grade_theme, grade_all, advice);
            gradeDao.updateGrade(grade);
            res.put("statusMsg", "update.");
            return res;
        }
        int grade_all = grade_expr + grade_struct + grade_theme;
        Grade grade = new Grade(expert_id, article_id, grade_expr, grade_struct, grade_theme, grade_all, advice);
        gradeDao.insertGrade(grade);
        res.put("article_id", article_id);
        res.put("expert_id", expert_id);
        res.put("grade_expr", grade_expr);
        res.put("grade_struct", grade_struct);
        res.put("grade_theme", grade_theme);
        res.put("grade_all", grade_all);
        res.put("advice", advice);
        res.put("statusMsg", "success");
        return res;
    }

    @Override
    public Map<String, Object> deleteGrade(String token, String expert_id, String article_id) {
        Map<String, Object> res = new HashMap<String, Object>();
        // 检测token是否合法
        if (!tokenVerify(token)) {
            res.put("statusMsg", "Invalid token.");
            return res;
        }
        if (gradeDao.existGrade(expert_id, article_id) == 0) {
            res.put("statusMsg", "Grades do not exist.");
            return res;
        }
        gradeDao.deleteGradeByExpertIdAndArticleId(expert_id, article_id);
        res.put("statusMsg", "success");
        return res;
    }

    @Override
    public Map<String, Object> updateGrade(String token, String article_id, String expert_id, int grade_expr, int grade_struct, int grade_theme, String advice) {
        Map<String, Object> res = new HashMap<String, Object>();
        // 检测token是否合法
        if (!tokenVerify(token)) {
            res.put("statusMsg", "Invalid token.");
            return res;
        }
        if (gradeDao.existGrade(expert_id, article_id) != 0) {
            res.put("statusMsg", "Grade exists.");
            return res;
        }
        int grade_all = grade_expr + grade_struct + grade_theme;
        Grade grade = new Grade(expert_id, article_id, grade_expr, grade_struct, grade_theme, grade_all, advice);
        gradeDao.updateGrade(grade);
        res.put("expert_id", expert_id);
        res.put("article_id", article_id);
        res.put("grade_expr", grade_expr);
        res.put("grade_struct", grade_struct);
        res.put("grade_theme", grade_theme);
        res.put("grade_all", grade_all);
        res.put("advice", advice);
        res.put("statusMsg", "success");
        return res;
    }

    @Override
    public Map<String, Object> existGrade(String expert_id, String article_id) {
        Map<String, Object> res = new HashMap<String, Object>();
        res.put("num", gradeDao.existGrade(expert_id, article_id));
        res.put("statusMsg", "success");
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
        res.put("grade", gradeDao.getGradeByExpertIdAndArticleId(expert_id, article_id));
        res.put("statusMsg", "success");
        return res;
    }

    @Override
    public Map<String, Object> getAvgGradeByArticleId(String article_id) {
        Map<String, Object> res = new HashMap<>();
        if (gradeDao.countArticleGrade(article_id) == 0) {
            res.put("avg_grade", null);
        } else {
            res.put("avg_grade", gradeDao.getAvgGradeByArticleId(article_id));
        }
        res.put("article_id",article_id);
        res.put("statusMsg", "success");
        return res;
    }

    @Override
    public Map<String, Object> getCountByArticleId(String article_id) {
        Map<String, Object> res = new HashMap<>();
        res.put("count", gradeDao.getCountByArticleId(article_id));
        res.put("statusMsg", "success");
        return res;
    }

    @Override
    public Map<String, Object> getSortByGradeAll(String expert_id) {
        Map<String, Object> res = new HashMap<String, Object>();
        res.put("grades", gradeDao.getSortByGradeAll(expert_id));
        res.put("statusMsg", "success");
        return res;
    }
}


