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
    private WarehouseCustomerCompany receiverCompany;
    private TransportCompany transportCompany;
    private String transportNumber;
    private String transportName;
    private DriverDTO driver;
    private String description;
    private BigDecimal goodsQuantity;
    private Integer goodsEntryCount;
    private String goodsQuantityUnit;
    private String goodsEntryCountUnit;
    private String manager;
    private List<GoodsDTO> goods;

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

    public void setManager(String manager) {
        this.manager = manager;
    }

    public void setGoodsQuantityUnit(Unit goodsQuantityUnit){
        this.goodsQuantityUnit = goodsQuantityUnit.getName();
    }

    public void setGoodsQuantityUnit(String goodsQuantityUnit){
        this.goodsQuantityUnit = goodsQuantityUnit;
    }

    public void setGoodsEntryCountUnit(Unit goodsEntryCountUnit){
        this.goodsEntryCountUnit = goodsEntryCountUnit.getName();
    }

    public void setGoodsEntryCountUnit(String goodsEntryCountUnit){
        this.goodsEntryCountUnit = goodsEntryCountUnit;
    }
}


