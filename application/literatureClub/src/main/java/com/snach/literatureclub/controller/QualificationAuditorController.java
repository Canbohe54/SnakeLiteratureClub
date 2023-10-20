package com.snach.literatureclub.controller;

import com.snach.literatureclub.service.QualificationAuditorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("quali")
@Deprecated
public class QualificationAuditorController {
    @Autowired
    private QualificationAuditorService qualificationAuditorService;

    /**
     * 审核员审核后对稿件状态进行更新
     * 返回格式 {statusMsg: #{STRING}}
     * @param token      用于验证是否过期以及获取作者id
     * @param article_id 稿件id
     * @param status     稿件状态（1：保存成功 2：待审核 3.已发布 4.未通过 0：保存失败）
     * @return 执行状态
     */
    @RequestMapping(value = "update_art_status",method = RequestMethod.POST)
    public Map<String,Object> update_art_status(@RequestParam("token") String token,
                                                @RequestParam("article_id") String article_id,
                                                @RequestParam("status") int status){
        return qualificationAuditorService.updateArticleStatus(token,article_id,status);
    }

}
