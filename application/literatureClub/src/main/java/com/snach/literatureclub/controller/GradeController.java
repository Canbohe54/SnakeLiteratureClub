package com.snach.literatureclub.controller;

import com.snach.literatureclub.common.annotation.ResponseNotIntercept;
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
    @ResponseNotIntercept
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
    @ResponseNotIntercept
    public Map<String,Object> deleteGrade(@RequestParam("token") String token,
                                       @RequestParam("article_id") String article_id,
                                       @RequestParam("expert_id") String expert_id){
        return gradeService.deleteGrade(token,expert_id,article_id);
    }

    @RequestMapping("update")
    @ResponseNotIntercept
    public Map<String,Object> updateGrade(@RequestParam("token") String token,
                                       @RequestParam("article_id") String article_id,
                                       @RequestParam("expert_id") String expert_id,
                                       @RequestParam("grade_expr") int grade_expr,
                                       @RequestParam("grade_struct") int grade_struct,
                                       @RequestParam("grade_theme") int grade_theme,
                                       @RequestParam("advice") String advice){
        return gradeService.updateGrade(token,article_id,expert_id,grade_expr,grade_struct,grade_theme,advice);
    }
    @RequestMapping("exist")
    @ResponseNotIntercept
    public Map<String,Object> existGrade(String expert_id,String article_id){
        return gradeService.existGrade(expert_id,article_id);
    }

    @RequestMapping("getGradeByArticleId")
    @ResponseNotIntercept
    public Map<String,Object> getGradeByArticleId(@RequestParam("article_id") String article_id,
                                                  @RequestParam("page_num" )int pageNum,
                                                  @RequestParam("page_size")int pageSize){
        return gradeService.getGradeByArticle_id(article_id,pageNum,pageSize);
    }

    @RequestMapping("getGradeByExpertId")
    @ResponseNotIntercept
    public Map<String,Object> getGradeByExpertId(@RequestParam("expert_id") String expert_id){
        return gradeService.getGradeByExpert_id(expert_id);
    }

    @RequestMapping("getGradeByExpertIdAndArticleId")
    @ResponseNotIntercept
    public Map<String,Object> getGradeByExpertIdAndArticleId(@RequestParam("expert_id") String expert_id,
                                                             @RequestParam("article_id") String article_id){
        return gradeService.getGradeByExpertIdAndArticleId(expert_id,article_id);
    }

    @RequestMapping("getAvgGrade")
    @ResponseNotIntercept
    public Map<String,Object> getAvgGradeByArticleId(@RequestParam("article_id") String article_id){
        return gradeService.getAvgGradeByArticleId(article_id);
    }

    @RequestMapping("getCount")
    @ResponseNotIntercept
    public Map<String,Object> getCountByArticleId(@RequestParam("article_id") String article_id){
        return gradeService.getCountByArticleId(article_id);
    }

    @RequestMapping("getSortByGradeAll")
    @ResponseNotIntercept
    public Map<String,Object> getSortByGradeAll(@RequestParam("expert_id") String expert_id){
        return gradeService.getSortByGradeAll(expert_id);
    }
}
