package com.lauzon.stackOverflow.service.impl;

import com.lauzon.stackOverflow.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    private final String FROM_EMAIL = "no-reply@stackoverflowclone.com";

    @Override
    public void sendEmail(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(FROM_EMAIL);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            // COMMENT THIS OUT FOR NOW so you can register without crashing
            // javaMailSender.send(message);

            // Print to console instead so you can see the activation link
            System.out.println("================ EMAIL MOCK ================");
            System.out.println("To: " + to);
            System.out.println("Body: " + body);
            System.out.println("============================================");
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
