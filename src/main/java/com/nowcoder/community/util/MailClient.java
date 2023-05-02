package com.nowcoder.community.util;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class MailClient {

    //启用日志
    private static final Logger logger = LoggerFactory.getLogger(MailClient.class);

    //注入JavaMailSender
    @Autowired
    private JavaMailSender mailSender;

    //注入properties里的username值给from
    @Value("${spring.mail.username}")
    private String from;

    public void sendMail(String to, String subject, String content) {

        try {
            //邮件内容
            MimeMessage message = mailSender.createMimeMessage();
            //帮助构建邮件内容
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom(from);                   //发件人
            helper.setTo(to);                       //收件人
            helper.setSubject(subject);             //主题
            helper.setText(content, true);    //内容和开启html
            mailSender.send(helper.getMimeMessage());
        } catch (MessagingException e) {
            logger.error("发送邮件失败：" + e.getMessage());//报错加到日志里
        }
    }
}
