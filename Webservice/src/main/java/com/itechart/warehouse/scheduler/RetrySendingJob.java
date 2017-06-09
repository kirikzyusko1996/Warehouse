package com.itechart.warehouse.scheduler;

import com.itechart.warehouse.mail.EmailFailedNotificationTemplate;
import com.itechart.warehouse.mail.EmailSenderService;
import com.itechart.warehouse.mail.EmailSendingResult;
import com.itechart.warehouse.mail.TemplateEnum;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.util.Assert;

/**
 * Job for retrying sending email after fail.
 */
@PropertySource("classpath:application.properties")
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class RetrySendingJob extends QuartzJobBean {

    private static final String ERROR_EXCEPTION_DURING_SENDING = "Exception during sending email: {}";

    private Logger logger = LoggerFactory.getLogger(RetrySendingJob.class);
//    private Environment environment;

//    @Autowired
//    public void setEnvironment(Environment environment) {
//        this.environment = environment;
//    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        logger.info("Retrying sending: try {}", context.getRefireCount());

        EmailSendingResult result = (EmailSendingResult) context.getJobDetail().getJobDataMap().get("result");
        EmailSenderService emailSenderService = (EmailSenderService) context.getJobDetail().getJobDataMap().get("service");
        Assert.notNull(result, "Result is null");
        Assert.notNull(emailSenderService, "Email sender service is null");
        int count = context.getJobDetail().getJobDataMap().getInt("count");

        if (count < 5) {
            context.getJobDetail().getJobDataMap().put("count", ++count);
            if (result.hasErrors()) {
                sendEmail(emailSenderService, result, context);
            }
        } else {
            sendEmail(emailSenderService, result, context);
            if (result.hasErrors()) {
                sendNotification(context, emailSenderService);
            }
        }
    }

    private void sendNotification(JobExecutionContext context, EmailSenderService emailSenderService) {
        EmailFailedNotificationTemplate template = new EmailFailedNotificationTemplate();
        template.setType(TemplateEnum.EMAIL_SENDING_FAILED);
        template.setSubject("Email sending failed");
        template.setResult((EmailSendingResult) context.getJobDetail().getJobDataMap().get("result"));
//                String emailAddress = environment.getProperty("spring.admin.email");
        String emailAddress = "ai@tut.by";
        if (emailAddress != null) {
            try {
                // TODO: 07.05.2017 admin email
                emailSenderService.sendEmail(emailAddress, template);
            } catch (MailException e) {
                logger.error(ERROR_EXCEPTION_DURING_SENDING, e.getMessage());
            }
        }
    }

    private void deleteJob(JobExecutionContext context) {
        try {
            context.getScheduler().deleteJob(context.getJobDetail().getKey());
        } catch (SchedulerException e) {
            logger.error(ERROR_EXCEPTION_DURING_SENDING, e.getMessage());
        }
    }

    private void sendEmail(EmailSenderService emailSenderService, EmailSendingResult result, JobExecutionContext context) {
        try {
            emailSenderService.sendEmail(result);
            if (!result.hasErrors()) {
                deleteJob(context);
            }
        } catch (IllegalParametersException e) {
            logger.error(ERROR_EXCEPTION_DURING_SENDING, e.getMessage());
        }
    }
}
