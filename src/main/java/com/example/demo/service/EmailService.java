package com.example.demo.service;

import com.example.demo.dto.request.EmailDetail;
import com.example.demo.entity.User;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Service
public class EmailService {

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private AuthenticationService authenticationService;

    public void sendResetOTPMail(String to, String subject, String otp, int expiredMinutes){
        User currentUser = authenticationService.getCurrentUser();
        try{

            Context context = new Context();
            context.setVariable("name", currentUser.getFullName());
            context.setVariable("otp", otp);
            context.setVariable("expired", expiredMinutes);
            context.setVariable("link", "https://www.youtube.com/");

            String html = templateEngine.process("emailOTPtemplate", context);

            // Creating a simple mail message
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

            // Setting up necessary details
            mimeMessageHelper.setFrom("admin@gmail.com");
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setText(html, true);
            mimeMessageHelper.setSubject(subject);
            javaMailSender.send(mimeMessage);

        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void sendAcpResetPasswordMail(String to, String subject){
        User currentUser = authenticationService.getCurrentUser();
        try{

            Context context = new Context();
            context.setVariable("name", currentUser.getFullName());
            context.setVariable("link", "https://www.youtube.com/");

            String html = templateEngine.process("resetPasswordtemplate", context);

            // Creating a simple mail message
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

            // Setting up necessary details
            mimeMessageHelper.setFrom("admin@gmail.com");
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setText(html, true);
            mimeMessageHelper.setSubject(subject);
            javaMailSender.send(mimeMessage);

        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }
}
