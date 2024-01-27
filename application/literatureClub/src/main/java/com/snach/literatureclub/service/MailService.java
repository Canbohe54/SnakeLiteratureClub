package com.snach.literatureclub.service;

import jakarta.mail.internet.MimeMessage;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.*;

@Service
public interface MailService {
    /**
     * 发送富文本邮件
     *
     * @param to 收件方邮箱
     * @param subject 邮件主题
     * @param content 邮件正文
     */
    void sendMimeMail(String to, String subject, String content) throws Exception;

    /**
     * 发送验证码邮件，正文将通过String.format(mailTemplate, #{code})生成
     *
     * @param to 收件方邮箱
     * @param code 验证码
     */
    void sendVerifyingCode(String to, String code) throws Exception;
}

@Service
@Mapper
class MailServiceImpl implements MailService {
    @Autowired
    JavaMailSender javaMailSender;

    // 邮件默认模板（格式化字符串）
    private static final String mailTemplate;

    // 读取邮件默认模板
    static {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            ClassPathResource resource = new ClassPathResource("statics/mailTemplate/mailTemplate_zh.html");
            InputStream inputStream = resource.getInputStream();
//            File file = ResourceUtils.getFile("classpath:statics/mailTemplate/mailTemplate_zh.html");
//            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(System.lineSeparator());
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        mailTemplate = stringBuilder.toString();
    }

    @Override
    public void sendMimeMail(String to, String subject, String content) throws Exception {
        MimeMessage mimeMsg = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMsg);
        try {
            helper.setFrom("3223677503@qq.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
        } catch (Exception e) {
            throw new Exception("Send E-mail Failed.");
        }
        javaMailSender.send(mimeMsg);
    }

    @Override
    public void sendVerifyingCode(String to, String code) throws Exception {
        String content = String.format(mailTemplate, code);
        sendMimeMail(to, "蛇拾文学社验证码", content);
    }
}
