package com.itechart.warehouse.mail;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Date;
import java.util.List;

/**
 * Template of the email.
 */
@Setter
@Getter
@ToString
public class Template {
    private List<Long> receiverIds;
    private String subject;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date date;
    private String backgroundColor;
}
