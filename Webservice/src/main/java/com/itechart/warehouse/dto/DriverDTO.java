package com.itechart.warehouse.dto;

import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class DriverDTO {
    private Long id;
    private String fullName;
    private String passportNumber;
    private String countryCode;
    private String issuedBy;
    private Date issueDate;
    private TransportCompanyDTO transportCompany;
}
