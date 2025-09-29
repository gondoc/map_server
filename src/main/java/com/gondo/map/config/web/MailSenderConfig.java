package com.gondo.map.config.web;

import com.gondo.map.component.props.MailSenderProps;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@RequiredArgsConstructor
@Configuration
public class MailSenderConfig {

    private final MailSenderProps mailSenderProps;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(mailSenderProps.getHost());
        javaMailSender.setPort(mailSenderProps.getPort());
        javaMailSender.setUsername(mailSenderProps.getUsername());
        javaMailSender.setPassword(mailSenderProps.getPassword());
        javaMailSender.setJavaMailProperties(mailSenderProps.getJavaMailProperties());
        return javaMailSender;
    }
}
