package com.fourstory.fourstory_api.service;

public interface EmailSender {
    void send(String to, String subject, String body);
}
