package com.rentit.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromMail;

    public void sendVerificationEmail(String toMail, String otp) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name()
            );

            Context context = new Context();
            context.setVariable("email", toMail);
            context.setVariable("otp", otp);

            String htmlContent = templateEngine.process("email-verification", context);

            helper.setTo(toMail);
            helper.setSubject("RentIt - Verify Your Account");
            helper.setFrom(fromMail);
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);

        } catch(MessagingException e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }

    }

}
