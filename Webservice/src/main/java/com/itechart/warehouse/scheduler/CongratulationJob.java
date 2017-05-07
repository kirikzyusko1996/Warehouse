package com.itechart.warehouse.scheduler;

import com.itechart.warehouse.entity.User;
import com.itechart.warehouse.mail.EmailSenderService;
import com.itechart.warehouse.mail.EmailSendingResult;
import com.itechart.warehouse.mail.Template;
import com.itechart.warehouse.mail.TemplateEnum;
import com.itechart.warehouse.service.exception.DataAccessException;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import com.itechart.warehouse.service.services.UserService;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Job for finding users having birthday and sending them congratulation emails.
 */
@Component
public class CongratulationJob {
    private Logger logger = LoggerFactory.getLogger(CongratulationJob.class);
    private UserService userService;
    private EmailSenderService emailSenderService;
    private Scheduler retryScheduler;

    @Autowired
    public void setRetryScheduler(Scheduler retryScheduler) {
        this.retryScheduler = retryScheduler;
    }

    @Autowired
    public void setEmailSenderService(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Scheduled(cron = "40 59 23 * * *")
    public void sendEmails() {
        logger.info("Executing task");
        try {
            List<User> users = userService.findUserByBirthday(new DateTime().now());
            if (users != null) {
                Template template = new Template();
                template.setType(TemplateEnum.BIRTHDAY);
                ArrayList<Long> userIdList = new ArrayList<>();
                template.setReceiverIds(userIdList);
                template.setSubject("Поздравление с днем рождения");
                template.setDate(new Date(LocalDate.now().toDate().getTime()));
                template.setBackgroundColor("#c4c0c0");
                for (User user : users) {
                    if (user != null)
                        userIdList.add(user.getId());
                }
                template.setReceiverIds(userIdList);
                EmailSendingResult result = emailSenderService.sendEmail(template);
                if (result.hasErrors()) {
                    try {
                        JobDataMap data = new JobDataMap();
                        data.put("result", result);
                        data.put("service", emailSenderService);
                        data.put("count", 1);
                        JobDetail job = newJob(RetrySendingJob.class).setJobData(data)
                                .withIdentity("retryJob", "group1")
                                .build();
                        Trigger trigger = newTrigger()
                                .withIdentity("retryTrigger", "group1")
                                .startAt(new DateTime().plusSeconds(1).toDate())//todo plus hours
                                .withSchedule(simpleSchedule()

                                        .withRepeatCount(5).withIntervalInSeconds(1))
//                                        .withIntervalInHours(1))
                                .build();
                        retryScheduler.scheduleJob(job, trigger);
                    } catch (SchedulerException e) {
                        logger.error("Exception while trying to schedule job for retrying sending emails: {}", e.getMessage());
                    }

                }
            }
        } catch (IllegalParametersException | DataAccessException e) {
            logger.error("Exception during search for users: {}", e.getMessage());
        }
    }


}

