package com.itechart.warehouse.mail;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Template class containing info about errors occurred during email sending.
 */
@Setter
@Getter
@ToString
public class EmailFailedNotificationTemplate extends Template {
    private EmailSendingResult result;
    private String emailAdress;

}
