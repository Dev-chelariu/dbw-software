package com.example.software.data.entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "emails")
public class Email extends AbstractEntity implements Serializable {

    @javax.validation.constraints.Email
    @Column(name = "recipient")
    private String to;
    @javax.validation.constraints.Email
    @Column(name = "recipients")
    private String cc;
    @NotEmpty
    @Column(name = "subject")
    private String subject;

    @NotEmpty
    @Column(name = "description")
    private String message;

    @DateTimeFormat
    @Column(name = "sendTime")
    private LocalDateTime dateSent = LocalDateTime.now();

    public Email() {

    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Email(String to, String cc, String subject, String message) {
        this.to = to;
        this.cc = cc;
        this.subject = subject;
        this.message = message;
    }

    public Email(String to, String subject, String message) {
        this.to = to;
        this.subject = subject;
        this.message = message;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public LocalDateTime getDateSent() {
        return dateSent;
    }

    public void setDateSent(LocalDateTime dateSent) {
        this.dateSent = dateSent;
    }

    public Email(String to, String cc, String subject, String message, LocalDateTime dateSent) {
        this.to = to;
        this.cc = cc;
        this.subject = subject;
        this.message = message;
        this.dateSent = dateSent;
    }
}
