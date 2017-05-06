package com.itechart.warehouse.mail;

import com.itechart.warehouse.entity.User;
import com.itechart.warehouse.security.UserDetailsProvider;
import com.itechart.warehouse.service.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.internet.MimeMessage;

/**
 * Service for sending emails.
 */
@Service
@PropertySource("classpath:application.properties")
public class EmailSenderService {
    private Logger logger = LoggerFactory.getLogger(EmailSenderService.class);
    private JavaMailSender mailSender;
    private UserService userService;
    private Environment environment;
    private TemplateService templateService;

    @Autowired
    public void setTemplateService(TemplateService templateService) {
        this.templateService = templateService;
    }

    @Autowired
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    public void setMailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    public void sendEmail(Template template, MultipartFile image) {
        logger.info("Sending email using template: {} ", template);
        if (template == null) throw new IllegalArgumentException("Template is null");
        if (template.getReceiverIds() != null)
            for (Long id : template.getReceiverIds()) {
                try {
                    User receiver = userService.findUserById(id);
                    User sender = UserDetailsProvider.getUserDetails().getUser();
                    sendEmail(template, sender, receiver, image);
                } catch (Exception e) {
                    logger.error("Exception during user retrieval: {}", e.getMessage());
                }
            }
    }


    public void sendEmail(Template template, User sender, User receiver, MultipartFile image) {
        logger.info("Sending email from user: {} to user: {} ", sender, receiver);
        if (sender == null || receiver == null) throw new IllegalArgumentException("Sender or receiver is null");
        if (receiver.getEmail() == null) return;
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                message.setTo(receiver.getEmail());
                message.setFrom(environment.getProperty("spring.email.address"));
                String imageName = null;
                if (image != null && !image.isEmpty()) {
                    imageName = image.getName();
                    final InputStreamSource imageSource = new ByteArrayResource(image.getBytes());
                    message.addInline(imageName, imageSource, image.getContentType());



                }
                if (template.getSubject() != null)
                    message.setSubject(template.getSubject());
                if (template.getDate() != null)
                    message.setSentDate(template.getDate());
                final String htmlContent = templateService.getMessageFromTemplate(template, sender, receiver, imageName);
                message.setText(htmlContent, true);
            }
        };
        try {
            mailSender.send(preparator);
        }catch (MailException e){
            logger.error("Exception during email sending: {}", e.getMessage());
        }
    }

}
