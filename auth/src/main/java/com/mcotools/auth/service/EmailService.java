package com.mcotools.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final String frontendUrl;

    public EmailService(JavaMailSender mailSender, @Value("${app.frontend-url}") String frontendUrl) {
        this.mailSender = mailSender;
        this.frontendUrl = frontendUrl;
    }

    public void envoyerEmailActivation(String destinataire, String nom, String tokenActivation) {
        String lien = frontendUrl + "/confirmation?token=" + tokenActivation;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(destinataire);
        message.setSubject("MCOtools - Activation de votre compte");
        message.setText(
                "Bonjour " + nom + ",\n\n"
                        + "Merci de votre inscription sur MCOtools.\n"
                        + "Pour activer votre compte, cliquez sur le lien ci-dessous :\n\n"
                        + lien + "\n\n"
                        + "Si vous n'etes pas a l'origine de cette inscription, ignorez cet email."
        );

        try {
            mailSender.send(message);
            log.info("Email d'activation envoye a {}", destinataire);
        } catch (Exception e) {
            log.error("Echec de l'envoi de l'email d'activation a {}: {}", destinataire, e.getMessage(), e);
            throw e;
        }
    }
}
