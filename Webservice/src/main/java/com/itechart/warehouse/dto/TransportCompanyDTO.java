package com.itechart.warehouse.dto;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TransportCompanyDTO {
    private Long id;
    private String name;
    private Boolean isTrusted;
    private String login;
    private String password;
    private Long warehouseCompanyId;
}
