package com.itechart.warehouse.mail;

import com.itechart.warehouse.entity.User;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.exception.ResourceNotFoundException;
import com.itechart.warehouse.service.services.UserService;
import org.joda.time.DateTime;
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
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.internet.MimeMessage;

/**
 * Service for sending emails.
 */
@Service
@PropertySource("classpath:application.properties")
public class EmailSenderService {

    private static final String ERROR_TEMPLATE_IS_NULL = "Template is null";
    private static final String ERROR_EXCEPTION_DURING_SENDING = "Exception during email sending: {}";
    private static final String ENCODING_UTF_8 = "UTF-8";
    private static final String SPRING_EMAIL_ADDRESS = "spring.email.address";

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


    public EmailSendingResult sendEmail(Template template, MultipartFile image) throws IllegalParametersException {
        logger.info("Send email, template: {} ", template);
        if (template == null) {
            throw new IllegalParametersException(ERROR_TEMPLATE_IS_NULL);
        }
        EmailSendingResult result = new EmailSendingResult(template);
        if (template.getReceiverIds() != null) {
            for (Long id : template.getReceiverIds()) {
                try {
                    User receiver = userService.findUserById(id);
                    send(template, receiver, image, result);
                } catch (DataAccessException | IllegalParametersException | ResourceNotFoundException e) {
                    logger.error("Exception during retrieval user from the database: {}", e.getMessage());
                }
            }
        }
        return result;
    }

    public EmailSendingResult sendEmail(Template template) throws IllegalParametersException {
        logger.info("Send email, template: {} ", template);
        if (template == null) {
            throw new IllegalParametersException(ERROR_TEMPLATE_IS_NULL);
        }
        EmailSendingResult result = new EmailSendingResult(template);
        if (template.getReceiverIds() != null) {
            for (Long id : template.getReceiverIds()) {
                try {
                    User receiver = userService.findUserById(id);
                    send(template, receiver, null, result);
                } catch (DataAccessException | IllegalParametersException | ResourceNotFoundException e) {
                    logger.error("Exception during retrieval user from the database: {}", e.getMessage());
                }
            }
        }
        return result;
    }

    private void send(Template template, User receiver, MultipartFile image, EmailSendingResult result) {
        try {
            sendEmail(template, receiver, null);
            result.addSuccess(receiver);
        } catch (MailException e) {
            logger.error(ERROR_EXCEPTION_DURING_SENDING, e.getMessage());
            result.addError(new EmailSendingError(e, receiver, new DateTime()));
        }
    }

    public EmailSendingResult sendEmail(EmailSendingResult result) throws IllegalParametersException {
        logger.info("Retry sending email,  previous result: {} ", result);
        if (result == null) throw new IllegalParametersException("Result is null");
        if (result.getErrors() != null) {
            for (EmailSendingError error : result.getErrors()) {
                User receiver = error.getReceiver();
                try {
                    sendEmail(result.getTemplate(), receiver, null);
                    result.removeError(error);
                } catch (MailException e) {
                    logger.error(ERROR_EXCEPTION_DURING_SENDING, e.getMessage());
                }
            }
        }
        return result;
    }

    public boolean sendMessageAboutRegistration(User receiver) {
        logger.info("Sending email about registration, receiver: {}", receiver);
        Assert.notNull(receiver, "Receiver is null");
        Assert.notNull(receiver.getEmail(), "Receiver email address is null");
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, ENCODING_UTF_8);
                message.setTo(receiver.getEmail());
                message.setFrom(environment.getProperty(SPRING_EMAIL_ADDRESS));
                String imageName = null;
                Template template = new Template();
                template.setType(TemplateEnum.REGISTRATION);
                final String htmlContent = templateService.getMessageFromTemplate(template, receiver, imageName);
                message.setText(htmlContent, true);
            }
        };
        try {
            mailSender.send(preparator);
        } catch (MailException e) {
            logger.error("Error sending email: {}", e);
            return false;
        }
        return true;
    }

    //todo check access
//    @PreAuthorize("")
    private void sendEmail(Template template, User receiver, MultipartFile image) {
        logger.info("Send email, receiver: {}", receiver);
        Assert.notNull(receiver, "Receiver is null");
        Assert.notNull(receiver.getEmail(), "Receiver email address is null");
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, ENCODING_UTF_8);
                message.setTo(receiver.getEmail());
                message.setFrom(environment.getProperty(SPRING_EMAIL_ADDRESS));
                String imageName = null;
                if (image != null && !image.isEmpty()) {
                    imageName = image.getName();
                    final InputStreamSource imageSource = new ByteArrayResource(image.getBytes());
                    message.addInline(imageName, imageSource, image.getContentType());
//                    message.addAttachment(imageName, imageSource, image.getContentType());
                }
                if (template.getSubject() != null)
                    message.setSubject(template.getSubject());
                if (template.getDate() != null)
                    message.setSentDate(template.getDate());
                final String htmlContent = templateService.getMessageFromTemplate(template, receiver, imageName);
                message.setText(htmlContent, true);
            }
        };
        mailSender.send(preparator);

    }


    public void sendEmail(String emailAddress, Template template) {
        logger.info("Send email, address: {}, template:", emailAddress, template);
        Assert.notNull(emailAddress, "Email address is null");
        Assert.notNull(template, ERROR_TEMPLATE_IS_NULL);
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, ENCODING_UTF_8);
                message.setTo(emailAddress);
                message.setFrom(environment.getProperty(SPRING_EMAIL_ADDRESS));
                String imageName = null;
//                if (image != null && !image.isEmpty()) {
//                    imageName = image.getName();
//                    final InputStreamSource imageSource = new ByteArrayResource(image.getBytes());
//                    message.addInline(imageName, imageSource, image.getContentType());
////                    message.addAttachment(imageName, imageSource, image.getContentType());
//                }
                if (template.getSubject() != null)
                    message.setSubject(template.getSubject());
                if (template.getDate() != null)
                    message.setSentDate(template.getDate());
                final String htmlContent = templateService.getMessageFromTemplate(template, null, imageName);
                message.setText(htmlContent, true);
            }
        };
        mailSender.send(preparator);

    }

}
