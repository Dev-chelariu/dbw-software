package com.example.software.data.service.implementation;

import com.example.software.data.entity.Email;
import com.example.software.data.repository.EmailRepository;
import com.example.software.data.service.IMail;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class EmailSenderService implements IMail {

    private final JavaMailSender mailSender;
    private final EmailRepository emailRepository;

    public EmailSenderService(JavaMailSender mailSender, EmailRepository emailRepository) {
        this.mailSender = mailSender;
        this.emailRepository = emailRepository;
    }
    /**
     * Sends an email message with no attachments.
     *
     * @param from       email address from which the message will be sent.
     * @param cc the recipients of the message.
     * @param subject    subject header field.
     * @param message       content of the message.
     * @throws MessagingException
     * @throws IOException
     */
    @Override
    public void send(String from, String cc, String subject, String message)
            throws MessagingException, IOException {
        try {
        SimpleMailMessage messages = new SimpleMailMessage();
        messages.setFrom("chelariu.webusiness@gmail.com");
        messages.setTo(from);
        messages.setCc(cc);
        messages.setSubject(subject);
        messages.setText(message);

        mailSender.send(messages);
        System.out.println("Mail send..");
        } catch (Exception e) {
            System.out.println("Error 500" + e.getMessage());
        }
    }

    @Override
    public Email saveEmail(String from, String cc, String subject, String message) {
        Email email = new Email();
        email.setTo(from);
        email.setCc(cc);
        email.setSubject(subject);
        email.setMessage(message);
        emailRepository.save(email);
        return email;
    }

    @Override
    @Transactional
    public List<Email> findAll(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return emailRepository.findAll();
        } else {
            return emailRepository.search(stringFilter);
        }
    }

    @Override
    public void sendEmailWithAttachment(String toEmail,
                                        String body,
                                        String subject,
                                        String attachment) throws MessagingException {

        MimeMessage mimeMessage = mailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper
                = new MimeMessageHelper(mimeMessage, true);

        mimeMessageHelper.setFrom("spring.email.from@gmail.com");
        mimeMessageHelper.setTo(toEmail);
        mimeMessageHelper.setText(body);
        mimeMessageHelper.setSubject(subject);

        FileSystemResource fileSystem
                = new FileSystemResource(new File(attachment));

        mimeMessageHelper.addAttachment(fileSystem.getFilename(),
                fileSystem);

        mailSender.send(mimeMessage);
        System.out.println("Mail Send...");

    }

    /**
     * Sends an email message to one recipient with one attachment.
     *
     * @param from       email address from which the message will be sent.
     * @param cc  the recipients of the message.
     * @param subject    subject header field.
     * @param message       content of the message.
     * @param attachment attachment to be included with the message.
     * @throws MessagingException
     * @throws IOException
     */
    @Override
    public void sendEmailWithAttachment(String from, String cc, String subject, String message, File attachment)
            throws MessagingException, IOException {

        MimeMessage messageAttach = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(messageAttach, true);

        helper.setTo(from);
        helper.setCc(cc);
        helper.setSubject(subject);
        helper.setText(message);

        // Add the attachment
        FileSystemResource fileResource = new FileSystemResource(attachment);
        helper.addAttachment(attachment.getName(), fileResource);
        mailSender.send(messageAttach);
        System.out.println("Mail Send...");
    }
}
