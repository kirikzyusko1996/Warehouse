package com.itechart.warehouse.mail;

import com.itechart.warehouse.entity.User;
import com.itechart.warehouse.entity.WarehouseCompany;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.Years;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Service for creating email messages filled with data.
 */
@Service
public class TemplateService {

    private static final String ERROR_TEMPLATE_IS_NULL = "Template is null";
    private static final String ERROR_TEMPLATE_TYPE_IS_NULL = "Template type is null";

    private Logger logger = LoggerFactory.getLogger(TemplateService.class);
    private SpringTemplateEngine templateEngine;

    @Autowired
    public void setTemplateEngine(SpringTemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String getMessageFromTemplate(Template template, User receiver, String imageName) {
        logger.info("Get email text, receiver: {}, template: {}", receiver, template);
        Assert.notNull(template, ERROR_TEMPLATE_IS_NULL);
        Assert.notNull(template.getType(), ERROR_TEMPLATE_TYPE_IS_NULL);
        switch (template.getType()) {
            case BIRTHDAY:
                return getBirthdayEmailText(template, receiver, imageName);
            case EMAIL_SENDING_FAILED:
                return getEmailFailedNotificationText((EmailFailedNotificationTemplate) template);
            case REGISTRATION:
                return getEmailRegistrationText(template, receiver);
            case DEFAULT:
                return getSimpleEmailText(template, imageName);
            default:
                return null;
        }
    }

    public List<Template> getTemplates() {
        List<Template> templates = new ArrayList<>();

        Template template = new Template();
        template.setType(TemplateEnum.BIRTHDAY);
        template.setBody(getRawTemplateText(TemplateEnum.BIRTHDAY));
        templates.add(template);

        return templates;
    }

    private String getRawTemplateText(TemplateEnum template) {
        if (StringUtils.isBlank(template.getPath())) {
            return "";
        }
        final Context ctx = new Context();
        ctx.setVariable("name", "[Имя Отчество]");
        ctx.setVariable("age", "[Возраст]");
        ctx.setVariable("company", "[Компания]");

        return templateEngine.process(template.getPath(), ctx);
    }

    private String getSimpleEmailText(Template template, String imageName) {
        Assert.notNull(template, ERROR_TEMPLATE_IS_NULL);
        Assert.notNull(template.getType(), ERROR_TEMPLATE_TYPE_IS_NULL);
        final Context ctx = new Context();
        ctx.setVariable("backgroundColor", template.getBackgroundColor());
        ctx.setVariable("body", template.getBody());
        if (imageName != null) {
            ctx.setVariable("image", imageName);
        }
        return templateEngine.process(template.getType().getPath(), ctx);
    }

    private String getEmailRegistrationText(Template template, User receiver) {
        Assert.notNull(template, ERROR_TEMPLATE_IS_NULL);
        Assert.notNull(template.getType(), ERROR_TEMPLATE_TYPE_IS_NULL);
        final Context ctx = new Context();

        ctx.setVariable("login", receiver.getLogin());
        ctx.setVariable("password", receiver.getPassword());
        return templateEngine.process(template.getType().getPath(), ctx);
    }

    private String getBirthdayEmailText(Template template, User receiver, String imageName) {
        Assert.notNull(template, ERROR_TEMPLATE_IS_NULL);
        Assert.notNull(receiver, "Receiver is null");
        Assert.notNull(template.getType(), ERROR_TEMPLATE_TYPE_IS_NULL);

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
            LocalDate birthLocalDate = new LocalDate(receiver.getDateOfBirth());
            LocalDate now = new LocalDate();
            Years age = Years.yearsBetween(birthLocalDate, now);
            ctx.setVariable("age", age.getYears());
        }

        ctx.setVariable("backgroundColor", template.getBackgroundColor());
        if (imageName != null) {
            ctx.setVariable("image", imageName);
        }

        String companyName = null;
        WarehouseCompany company = receiver.getWarehouseCompany();
        if (company != null) {
            companyName = company.getName();
        }
        ctx.setVariable("company", companyName);
        return templateEngine.process(template.getType().getPath(), ctx);
    }

    private String getEmailFailedNotificationText(EmailFailedNotificationTemplate template) {
        Assert.notNull(template, ERROR_TEMPLATE_IS_NULL);
        Assert.notNull(template.getType(), ERROR_TEMPLATE_TYPE_IS_NULL);
        final Context ctx = new Context();

        ctx.setVariable("message", template.getResult().getMessage());
        return templateEngine.process(template.getType().getPath(), ctx);
    }

}
