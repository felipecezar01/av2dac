package com.av2dac.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import com.av2dac.entities.User;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendRegistrationEmail(User user) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Registro Bem-sucedido");
        message.setText("Olá " + user.getName() + ",\n\nSeu registro foi concluído com sucesso!\n\nObrigado por se registrar em nosso sistema.\n\nAtenciosamente,\nEquipe AV2DAC");

        mailSender.send(message);
    }
}

