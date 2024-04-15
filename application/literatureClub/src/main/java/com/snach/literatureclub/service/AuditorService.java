package com.snach.literatureclub.service;

import com.snach.literatureclub.bean.Article;
import com.snach.literatureclub.bean.User;
import com.snach.literatureclub.common.ArticleAuditStatus;
import com.snach.literatureclub.common.ArticlePublishStatus;
import com.snach.literatureclub.common.Identity;
import com.snach.literatureclub.common.exception.InsufficientPermissionException;
import com.snach.literatureclub.common.exception.NoUnauditedArticleException;
import com.snach.literatureclub.dao.NewArticleDao;
import com.snach.literatureclub.utils.QiniuKodoUtil;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.snach.literatureclub.utils.FileUtil.generateFileNameByTimesStamp;


public interface AuditorService {
    /**
     * 获取待审核的文章
     *
     * @param auditor 审核码
     * @return one article not been audited
     */
    Article getUnauditedArticle(User auditor) throws InsufficientPermissionException, NoUnauditedArticleException;

    /**
     * 审核文章
     *
     * @param auditor     User object of an auditor
     * @param articleId   审核的文章id
     * @param auditResult 审核结果
     * @param reason      审核不通过原因，若审核通过则忽略
     * @return true if audit succeed
     */
    boolean audit(User auditor, String articleId, boolean auditResult, String reason);

    /**
     * 退出审核
     *
     * @param auditor   User object of an auditor
     * @param articleId 审核的文章id
     * @return true if cancel audit succeed
     */
    boolean cancelAudit(User auditor, String articleId);

    /**
     * 保存文章的批改建议文件
     *
     * @param articleId       文章id
     * @param approvalArticle 文章的批改建议文件
     */
    void saveApprovalArticle(String articleId, MultipartFile approvalArticle);
}

@Transactional(rollbackFor = Exception.class)
@Service
@Mapper
class AuditorServiceImpl implements AuditorService {
    private final NewArticleDao newArticleDao;

    private final QiniuKodoUtil qiniuKodoUtil;

    @Autowired
    AuditorServiceImpl(QiniuKodoUtil qiniuKodoUtil, NewArticleDao newArticleDao) {
        this.qiniuKodoUtil = qiniuKodoUtil;
        this.newArticleDao = newArticleDao;
    }

    @Override
    public Article getUnauditedArticle(User auditor) {
        if (!auditor.checkIdentity(Identity.AUDITOR) && !auditor.checkIdentity(Identity.ADMINISTRATOR)) {
            throw new InsufficientPermissionException();
        }
        Article article;
        // 先找指定审核人的文章
        article = newArticleDao.getArticleByStatusAndAuditedBy(ArticleAuditStatus.SUBMITTED, auditor.getId());
        // 如果找不到，找未指定审核人的文章
        if (article == null) {
            article = newArticleDao.getArticleByStatusAndAuditedBy(ArticleAuditStatus.SUBMITTED, "");
            if (article == null) {
                // 再找不到则设文章id为null
                article = new Article();
                article.setId("null");
            }
        }
        if (!article.getId().equals("null")) {
            newArticleDao.updateAuditStatus(article.getId(), ArticleAuditStatus.BEING_AUDITED);
        }
        return article;
    }

    @Override
    public boolean audit(User auditor, String articleId, @RequestParam("audit_result") boolean auditResult, String reason) {
        if (!auditor.checkIdentity(Identity.AUDITOR) && !auditor.checkIdentity(Identity.ADMINISTRATOR)) {
            throw new InsufficientPermissionException();
        }
        // 记录审核人id
        newArticleDao.updateAuditedBy(articleId, auditor.getId());
        if (auditResult) {
            // 审核通过
            newArticleDao.audit(articleId, ArticleAuditStatus.AUDITED, reason);
            // 判断该文章是否有意向收稿者
            User user = newArticleDao.getReceivedBy(articleId);
            if (user != null && user.getIdentity() != null) {
                if (user.getIdentity() == Identity.HUNTER) {
                    // 收稿者为报社，设置待收录
                    newArticleDao.updatePublishStatus(articleId, ArticlePublishStatus.UNDER_RECORD);
                } else {
                    // 收稿者为专家，设置待推荐
                    newArticleDao.updatePublishStatus(articleId, ArticlePublishStatus.UNDER_REVIEW);
                }
            } else {
                // 没有意向收稿者，直接发布
                newArticleDao.updatePublishStatus(articleId, ArticlePublishStatus.PUBLIC);
            }
        } else {
            // 审核不通过
            newArticleDao.audit(articleId, ArticleAuditStatus.FAIL_AUDITED, reason);
        }
        return true;
    }

    @Override
    public boolean cancelAudit(User auditor, String articleId) {
        if (!auditor.checkIdentity(Identity.AUDITOR) && !auditor.checkIdentity(Identity.ADMINISTRATOR)) {
            throw new InsufficientPermissionException();
        }
        newArticleDao.updateAuditStatus(articleId, ArticleAuditStatus.SUBMITTED);
        return true;
    }

    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public void saveApprovalArticle(String articleId, MultipartFile approvalArticle) {
        try {
            String filename = approvalArticle.getOriginalFilename();
            if (filename == null) {
                filename = String.valueOf(System.currentTimeMillis());
            }
            File f = new File(filename);
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(f));
            out.write(approvalArticle.getBytes());
            out.flush();
            out.close();

            String prefix = "articles/" + articleId + "/";
            String suffix = ".pdf";
            String fileName = generateFileNameByTimesStamp(prefix, suffix);
            // 保存审批文件
            qiniuKodoUtil.upload(f, fileName);
            // 更新最新的审批文件的url
            String fileUrl = qiniuKodoUtil.getFileUrl(fileName);
            newArticleDao.updateLatestApprovalArticleUrl(articleId, fileUrl);
            if (!f.delete()) {
                System.out.println("删除失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
