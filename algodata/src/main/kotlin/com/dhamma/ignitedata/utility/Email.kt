package com.dhamma.ignitedata.utility

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Component

@Component
class Email {
    @Autowired
    lateinit var emailSender: JavaMailSender

    fun sendSimpleMessage(text: String) {
        var message = SimpleMailMessage()
        message.setTo("rowanfoo@gmail.com");
        message.setSubject("!!!Error from APP !!!");
        message.setText(text);
        println("-------send message--")
        emailSender.send(message);
        println("-------send message----done-")

    }
}
