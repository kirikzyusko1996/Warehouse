package com.itechart.warehouse.dto;

import com.itechart.warehouse.entity.TransportCompany;
import com.itechart.warehouse.entity.User;
import com.itechart.warehouse.entity.WarehouseCustomerCompany;
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

    @Setter(AccessLevel.NONE)
    private String receiverCompany;

    @Setter(AccessLevel.NONE)
    private String transportCompany;

    private String transportNumber;
    private String transportName;
    private DriverDTO driver;
    private String description;
    private BigDecimal goodsQuantity;
    private Integer goodsEntryCount;

    @Setter(AccessLevel.NONE)
    private String manager;

    private Timestamp registrationDate;
    private List<GoodsInvoiceDTO> goods;

    public void setReceiverCompany(WarehouseCustomerCompany receiverCompany) {
        this.receiverCompany = receiverCompany.getName();
    }

    public void setTransportCompany(TransportCompany transportCompany) {
        this.transportCompany = transportCompany.getName();
    }

    public void setManager(User manager) {
        StringBuilder dispatcherName = new StringBuilder();
        if (StringUtils.isNotEmpty(manager.getLastName())){
            dispatcherName.append(manager.getLastName());
        }
        if (StringUtils.isNotEmpty(manager.getFirstName())){
            dispatcherName.append(manager.getFirstName());
        }

        this.manager = dispatcherName.toString();
    }
}
