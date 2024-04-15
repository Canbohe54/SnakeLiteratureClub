package com.snach.literatureclub.controller;

import com.snach.literatureclub.bean.Article;
import com.snach.literatureclub.bean.User;
import com.snach.literatureclub.service.AuditorService;
import com.snach.literatureclub.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin
@RestController
@RequestMapping(value = "auditor", method = RequestMethod.POST)
public class AuditorController {
    private final AuditorService auditorService;
    private final TagService tagService;

    @Autowired
    public AuditorController(AuditorService auditorService, TagService tagService) {
        this.auditorService = auditorService;
        this.tagService = tagService;
    }

    @RequestMapping("saveApprovalArticle")
    public void saveApprovalArticle(String articleId, MultipartFile approvalArticle) {
        auditorService.saveApprovalArticle(articleId, approvalArticle);
    }

    @RequestMapping(value = "getUnauditedArticle", method = RequestMethod.GET)
    public Article getUnauditedArticle(User auditor) {
        Article article = auditorService.getUnauditedArticle(auditor);
        article.setTags(tagService.getPackedTags(article.getId()));
        return article;
    }

    @RequestMapping("audit")
    public boolean audit(User auditor,@RequestParam("article_id") String articleId,@RequestParam("audit_result") boolean auditResult, String reason) {
        return auditorService.audit(auditor, articleId, auditResult, reason);
    }

    @RequestMapping("cancelAudit")
    public boolean cancelAudit(User auditor, String articleId) {
        return auditorService.cancelAudit(auditor, articleId);
    }
}
