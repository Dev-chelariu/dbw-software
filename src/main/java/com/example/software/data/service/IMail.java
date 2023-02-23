package com.example.software.data.service;

import com.example.software.data.entity.Email;

import javax.mail.MessagingException;
import java.io.File;
import java.io.IOException;
import java.util.List;

public interface IMail {

    void send(String from, String cc, String subject, String message)
            throws MessagingException, IOException;

    Email saveEmail(String from, String cc, String subject, String message);

    List<Email> findAll(String stringFilter);

    void sendEmailWithAttachment(String toEmail,
                                 String body,
                                 String subject,
                                 String attachment) throws MessagingException;

    void sendEmailWithAttachment(String from, String cc, String subject, String message, File attachment)
            throws MessagingException, IOException;
}
