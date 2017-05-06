package com.itechart.warehouse.mail;

import com.itechart.warehouse.entity.User;
import com.itechart.warehouse.entity.WarehouseCompany;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import java.sql.Date;

/**
 * Service for creating email messages filled with data.
 */
@Service
public class TemplateService {
    private Logger logger = LoggerFactory.getLogger(TemplateService.class);
    private SpringTemplateEngine templateEngine;

    @Autowired
    public void setTemplateEngine(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String getMessageFromTemplate(Template template, User sender, User receiver, String imageName) {
        if (template == null || sender == null || receiver == null) throw new IllegalArgumentException();
        return getBirthdayEmailText(template, sender, receiver, imageName);
    }


    private String getBirthdayEmailText(Template template, User sender, User receiver, String imageName) {
        if (sender == null || receiver == null) throw new IllegalArgumentException("Sender or receiver is null");
        logger.info("Forming birthday email html from sender: {} to receiver: {}, using template: {}", sender, receiver, template);
        final Context ctx = new Context();

        StringBuilder reference = new StringBuilder();
        if (receiver.getFirstName() != null) {
            reference.append(receiver.getFirstName());
            if (receiver.getPatronymic() != null)
                reference = reference.append(" ").append(receiver.getPatronymic());
            ctx.setVariable("name", reference.toString());
        }
        Date dateOfBirth = receiver.getDateOfBirth();
        if (dateOfBirth != null) {
            LocalDate birthdate = new LocalDate(receiver.getDateOfBirth());
            LocalDate now = new LocalDate();
            Years age = Years.yearsBetween(birthdate, now);
            ctx.setVariable("age", age.getYears());
        }

        ctx.setVariable("backgroundColor", template.getBackgroundColor());
        if (imageName != null)
            ctx.setVariable("image", imageName);

        String companyName = null;
        WarehouseCompany company = sender.getWarehouseCompany();
        if (company != null) {
            companyName = company.getName();
        }
        ctx.setVariable("company", companyName);
        return templateEngine.process("templates/birthdayTemplate", ctx);


    }

}
