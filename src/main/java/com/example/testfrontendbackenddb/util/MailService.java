//package com.example.testfrontendbackenddb.util;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.ByteArrayResource;
//import org.springframework.mail.javamail.JavaMailSender;
//import org.springframework.mail.javamail.MimeMessageHelper;
//import org.springframework.stereotype.Service;
//
//import jakarta.mail.internet.MimeMessage;
//
//@Service
//public class MailService {
//
//    private final JavaMailSender mailSender;
//
//    @Value("${attendance.email.to}")
//    private String emailTo;
//
//    public MailService(JavaMailSender mailSender) {
//        this.mailSender = mailSender;
//    }
//
//    public void sendEmailWithAttachment(String subject, String body, String filename, byte[] bytes) {
//        try {
//            MimeMessage message = mailSender.createMimeMessage();
//            MimeMessageHelper helper = new MimeMessageHelper(message, true);
//            helper.setTo(emailTo);
//            helper.setSubject(subject);
//            helper.setText(body);
//            helper.addAttachment(filename, new ByteArrayResource(bytes));
//            mailSender.send(message);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}