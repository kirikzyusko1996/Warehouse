package com.itechart.warehouse.mail;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.itechart.warehouse.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Class containing info about errors occurred during email sending
 * and receivers to whom emails were not sent.
 */
@Setter
@Getter
public class EmailSendingResult {
    @JsonIgnore
    private Template template;
    @JsonIgnore
    private List<EmailSendingError> errors = new ArrayList<>();
    @JsonIgnore
    private List<User> successList = new ArrayList<>();

    EmailSendingResult(Template template) {
        this.template = template;
    }

    public void addError(EmailSendingError e) {
        errors.add(e);
    }

    public void removeError(EmailSendingError e) {
        errors.remove(e);
    }

    public void addSuccess(User user) {
        successList.add(user);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    @JsonValue
    public String getMessage() {
        StringBuilder message = new StringBuilder();

        if (CollectionUtils.isNotEmpty(successList)) {
            message.append("Emails successfully sent to users: {");
            for (User user : successList) {
                appendUser(message, user);
            }
            message.append("\r\n");
        }

        if (hasErrors()) {
            message.append("Email sending failed:\r\n");
            for (EmailSendingError e : errors) {
                message.append("{");
                message.append(e.toString());
                message.append("};\r\n");
            }
            return message.toString();
        }

        return message.toString();
    }

    private void appendUser(StringBuilder message, User user) {
        message.append("{");
        if (user.getId() != null) {
            message.append("id: ");
            message.append(user.getId());
        }
        if (user.getLastName() != null) {
            message.append(", last name: ");
            message.append(user.getLastName());
        }
        if (user.getFirstName() != null) {
            message.append(", first name: ");
            message.append(user.getFirstName());
        }
        if (user.getPatronymic() != null) {
            message.append(", patronymic: ");
            message.append(user.getPatronymic());
        }
        if (user.getEmail() != null) {
            message.append(", email: ");
            message.append(user.getEmail());
        }
        message.append("}\r\n");
    }
}
