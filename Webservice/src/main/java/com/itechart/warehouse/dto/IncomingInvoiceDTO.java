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
public class IncomingInvoiceDTO {
    private Long id;
    private String number;
    private Date issueDate;
    private WarehouseCustomerCompanyDTO supplierCompany;
    private TransportCompanyDTO transportCompany;
    private String transportNumber;
    private String transportName;
    private DriverDTO driver;
    private String description;
    private String goodsEntryCount;
    private String dispatcher;
    private List<GoodsDTO> goods;
    private String status;
    private Timestamp registrationDate;

    public void setDispatcher(User dispatcher) {
        StringBuilder dispatcherName = new StringBuilder();
        if (StringUtils.isNotEmpty(dispatcher.getLastName())){
            dispatcherName.append(dispatcher.getLastName());
        }

        this.dispatcher = dispatcherName.toString();
    }

    public void setDispatcher(String dispatcher) {
        this.dispatcher = dispatcher;
    }
}
