package com.itechart.warehouse.mail;

/**
 * Enumeration of template types with paths to them.
 */
public enum TemplateEnum {
    BIRTHDAY("templates/birthdayTemplate"),
    EMAIL_SENDING_FAILED("templates/emailFailedNotificationTemplate"),
    REGISTRATION("templates/registrationTemplate");

    private String path;

    TemplateEnum(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
