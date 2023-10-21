package com.snach.literatureclub.controller;

import com.snach.literatureclub.service.ArticleService;
import com.snach.literatureclub.service.GradeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping(value = "grade", method = RequestMethod.POST)
public class GradeController {
    @Autowired
    private GradeService gradeService;

    @RequestMapping("add")
    public Map<String,Object> addGrade(@RequestParam("token") String token,
                                       @RequestParam("article_id") String article_id,
                                       @RequestParam("expert_id") String expert_id,
                                       @RequestParam("grade_expr") int grade_expr,
                                       @RequestParam("grade_struct") int grade_struct,
                                       @RequestParam("grade_theme") int grade_theme,
                                       @RequestParam("advice") String advice){
        return gradeService.addGrade(token,article_id,expert_id,grade_expr,grade_struct,grade_theme,advice);
    }

    @RequestMapping("delete")
    public Map<String,Object> deleteGrade(@RequestParam("token") String token,
                                       @RequestParam("article_id") String article_id,
                                       @RequestParam("expert_id") String expert_id){
        return gradeService.deleteGrade(token,expert_id,article_id);
    }

    @RequestMapping("update")
    public Map<String,Object> updateGrade(@RequestParam("token") String token,
                                       @RequestParam("article_id") String article_id,
                                       @RequestParam("expert_id") String expert_id,
                                       @RequestParam("grade_expr") int grade_expr,
                                       @RequestParam("grade_struct") int grade_struct,
                                       @RequestParam("grade_theme") int grade_theme,
                                       @RequestParam("advice") String advice){
        return gradeService.updateGrade(token,article_id,expert_id,grade_expr,grade_struct,grade_theme,advice);
    }

    @RequestMapping("getGradeByArticleId")
    public Map<String,Object> getGradeByArticleId(@RequestParam("article_id") String article_id){
        return gradeService.getGradeByArticle_id(article_id);
    }

    @RequestMapping("getGradeByExpertId")
    public Map<String,Object> getGradeByExpertId(@RequestParam("expert_id") String expert_id){
        return gradeService.getGradeByExpert_id(expert_id);
    }

    @RequestMapping("getGradeByExpertIdAndArticleId")
    public Map<String,Object> getGradeByExpertIdAndArticleId(@RequestParam("expert_id") String expert_id,
                                                             @RequestParam("article_id") String article_id){
        return gradeService.getGradeByExpertIdAndArticleId(expert_id,article_id);
    }

    @RequestMapping("getAvgGrade")
    public Map<String,Object> getAvgGradeByArticleId(@RequestParam("article_id") String article_id){
        return gradeService.getAvgGradeByArticleId(article_id);
    }

    @RequestMapping("getCount")
    public Map<String,Object> getCountByArticleId(@RequestParam("article_id") String article_id){
        return gradeService.getCountByArticleId(article_id);
    }

    @RequestMapping("getSortByGradeAll")
    public Map<String,Object> getSortByGradeAll(@RequestParam("expert_id") String expert_id){
        return gradeService.getSortByGradeAll(expert_id);
    }
}
