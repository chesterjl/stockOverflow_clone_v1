package com.lauzon.stackOverflow.service;

public interface EmailService {

    void sendEmail(String to, String subject, String body);
}
