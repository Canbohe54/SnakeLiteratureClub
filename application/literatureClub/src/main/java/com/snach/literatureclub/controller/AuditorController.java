package com.snach.literatureclub.controller;

import com.snach.literatureclub.bean.Article;
import com.snach.literatureclub.bean.User;
import com.snach.literatureclub.service.AuditorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin
@RestController
@RequestMapping(value = "auditor", method = RequestMethod.POST)
public class AuditorController {
    AuditorService auditorService;

    @Autowired
    public AuditorController(AuditorService auditorService) {
        this.auditorService = auditorService;
    }

    @RequestMapping("saveApprovalArticle")
    public void saveApprovalArticle(String articleId, MultipartFile approvalArticle) {
        auditorService.saveApprovalArticle(articleId, approvalArticle);
    }

    @RequestMapping(value = "getUnauditedArticle", method = RequestMethod.GET)
    public Article getUnauditedArticle(User auditor) {
        return auditorService.getUnauditedArticle(auditor);
    }

    @RequestMapping("audit")
    public boolean audit(User auditor, String articleId, boolean auditResult, String reason) {
        return auditorService.audit(auditor, articleId, auditResult, reason);
    }

    @RequestMapping("cancelAudit")
    public boolean cancelAudit(User auditor, String articleId) {
        return auditorService.cancelAudit(auditor, articleId);
    }
}
