package com.itechart.warehouse.controller;

import com.itechart.warehouse.mail.EmailSenderService;
import com.itechart.warehouse.mail.EmailSendingResult;
import com.itechart.warehouse.mail.Template;
import com.itechart.warehouse.service.exception.IllegalParametersException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller for handling requests to send email.
 */
@RestController
@RequestMapping(value = "/email")
public class EmailController {
    private EmailSenderService emailSenderService;
    private Logger logger = LoggerFactory.getLogger(EmailController.class);

    @Autowired
    public void setEmailSenderService(EmailSenderService emailSenderService) {
        this.emailSenderService = emailSenderService;
    }

    @RequestMapping(value = "", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EmailSendingResult> getActs(@RequestBody Template template,
                                                      @RequestParam(required = false) final MultipartFile image) throws IllegalParametersException {
        logger.info("Handling request for sending email according to template: {}", template);
        EmailSendingResult result = emailSenderService.sendEmail(template, image);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
