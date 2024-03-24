package com.snach.literatureclub.service;

import com.snach.literatureclub.bean.User;
import com.snach.literatureclub.bean.Article;
import com.snach.literatureclub.common.exception.InsufficientPermissionException;
import com.snach.literatureclub.common.exception.InvalidTokenException;
import com.snach.literatureclub.common.exception.NoUnauditedArticleException;
import com.snach.literatureclub.common.ArticleStatus;
import com.snach.literatureclub.common.Identity;
import com.snach.literatureclub.dao.ArticleDao;
import com.snach.literatureclub.utils.QiniuKodoUtil;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Objects;

import static com.snach.literatureclub.utils.FileUtil.generateFileNameByTimesStamp;


public interface AuditorService {
    /**
     * 获取待审核的文章
     *
     * @param auditor 审核码
     * @return
     */
    Article getUnauditedArticle(User auditor) throws InsufficientPermissionException, NoUnauditedArticleException;

    /**
     * 审核文章
     *
     * @param auditor
     * @param articleId   审核的文章id
     * @param auditResult 审核结果
     * @param reason      审核不通过原因，若审核通过则忽略
     * @return
     */
    boolean audit(User auditor, String articleId, boolean auditResult, String reason);

    void saveApprovalArticle(String articleId, MultipartFile approvalArticle);
}

@Transactional(rollbackFor = Exception.class)
@Service
@Mapper
class AuditorServiceImpl implements AuditorService {
    @Autowired
    ArticleDao articleDao;
    @Autowired
    QiniuKodoUtil qiniuKodoUtil;

    @Override
    public Article getUnauditedArticle(User auditor) throws InsufficientPermissionException, NoUnauditedArticleException {
        if (!auditor.checkIdentity(Identity.AUDITOR) && !auditor.checkIdentity(Identity.ADMINISTRATOR)) {
            throw new InsufficientPermissionException();
        }
        Article article = articleDao.getArticleByStatus(ArticleStatus.SUBMITTED);
        if (article == null) {
//            article.setId("null");
            throw new NoUnauditedArticleException();
        }
//        articleDao.updateStatus(ArticleStatus.BEING_AUDITED, article.getId());
        return article;
    }

    @Override
    public boolean audit(User auditor, String articleId, boolean auditResult, String reason) {
        if (!auditor.checkIdentity(Identity.AUDITOR) && !auditor.checkIdentity(Identity.ADMINISTRATOR)) {
            throw new InsufficientPermissionException();
        }
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
            articleDao.updateLatestApprovalArticleUrl(articleId, fileUrl);
            File tem = new File(f.toURI());
            if (!f.delete()) {
                System.out.println("删除失败");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
