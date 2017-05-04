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

    @Setter(AccessLevel.NONE)
    private String supplierCompany;

    @Setter(AccessLevel.NONE)
    private String transportCompany;

    private String transportNumber;
    private String transportName;
    private DriverDTO driver;
    private String description;
    private BigDecimal goodsQuantity;
    private Integer goodsEntryCount;
    private String goodsQuantityUnit;
    private String goodsEntryCountUnit;

    @Setter(AccessLevel.NONE)
    private String dispatcher;

    private Timestamp registrationDate;
    private List<GoodsDTO> goods;

    private String status;

    public void setSupplierCompany(WarehouseCustomerCompany supplierCompany) {
        this.supplierCompany = supplierCompany.getName();
    }

    public void setTransportCompany(TransportCompany transportCompany) {
        this.transportCompany = transportCompany.getName();
    }

    public void setDispatcher(User dispatcher) {
        StringBuilder dispatcherName = new StringBuilder();
        if (StringUtils.isNotEmpty(dispatcher.getLastName())){
            dispatcherName.append(dispatcher.getLastName());
        }
        if (StringUtils.isNotEmpty(dispatcher.getFirstName())){
            dispatcherName.append(dispatcher.getFirstName());
        }

        this.dispatcher = dispatcherName.toString();
    }
}
