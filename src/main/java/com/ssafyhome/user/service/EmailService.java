package com.ssafyhome.user.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordResetEmail(String to, String tempPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("SSAFY Home - 임시 비밀번호 발급");
        message.setText("안녕하세요, SSAFY Home 서비스입니다.\n\n" +
                "요청하신 임시 비밀번호는 다음과 같습니다: " + tempPassword + "\n\n" +
                "로그인 후 반드시 비밀번호를 변경해주세요. \n\n" +
                "감사합니다.");

        mailSender.send(message);
    }
}
