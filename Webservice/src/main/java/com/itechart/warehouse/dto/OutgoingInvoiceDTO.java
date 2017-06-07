package com.itechart.warehouse.dto;

import com.itechart.warehouse.entity.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.List;

@Getter
@Setter
public class OutgoingInvoiceDTO {
    private Long id;
    private String number;
    private Date issueDate;
    private WarehouseCustomerCompanyDTO receiverCompany;
    private TransportCompanyDTO transportCompany;
    private String transportNumber;
    private String transportName;
    private DriverDTO driver;
    private String description;
    private Integer goodsEntryCount;
    private String manager;
    private List<GoodsDTO> goods;
    private String status;
    private Timestamp registrationDate;

    public void setManager(User manager) {
        StringBuilder dispatcherName = new StringBuilder();
        if (StringUtils.isNotEmpty(manager.getLastName())){
            dispatcherName.append(manager.getLastName());
        }

        this.manager = dispatcherName.toString();
    }

    public void setManager(String manager) {
        this.manager = manager;
    }
}


