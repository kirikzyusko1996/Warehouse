package com.itechart.warehouse.mail;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;
import com.itechart.warehouse.entity.User;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;
import org.springframework.mail.MailException;

/**
 * Class containing info about result thrown during email sending process.
 */
@Setter
@Getter
class EmailSendingError {
    @JsonIgnore
    private MailException exception;
    @JsonIgnore
    private User receiver;
    @JsonIgnore
    private DateTime time;


    EmailSendingError(MailException exception, User receiver, DateTime time) {
        this.exception = exception;
        this.receiver = receiver;
        this.time = time;
    }

    @Override
    @JsonValue
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (receiver != null) {
            sb.append("Failed to send email to user: {");
            if (receiver.getId() != null) {
                sb.append("id: ");
                sb.append(receiver.getId());
            }
            if (receiver.getLastName() != null) {
                sb.append(", last name: ");
                sb.append(receiver.getLastName());
            }
            if (receiver.getFirstName() != null) {
                sb.append(", first name: ");
                sb.append(receiver.getFirstName());
            }
            if (receiver.getPatronymic() != null) {
                sb.append(", patronymic: ");
                sb.append(receiver.getPatronymic());
            }
            if (receiver.getEmail() != null) {
                sb.append(", email: ");
                sb.append(receiver.getEmail());
            }
            sb.append("}\n");
        }
        if (time != null) {
            sb.append("; time: ");
            sb.append(time.toString("dd-MM-yyyy HH:mm:ss"));
        }
        if (exception != null) {
            sb.append("; cause: ");
            sb.append(exception.getCause());
        }
        return sb.toString();
    }
}
