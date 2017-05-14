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
    private String supplierCompany;
    private String transportCompany;
    private String transportNumber;
    private String transportName;
    private Driver driver;
    private String description;
    private BigDecimal goodsQuantity;
    private Integer goodsEntryCount;
    private String goodsQuantityUnit;
    private String goodsEntryCountUnit;
    private String dispatcher;
    private List<GoodsDTO> goods;

    public void setSupplierCompany(WarehouseCustomerCompany supplierCompany) {
        this.supplierCompany = supplierCompany.getName();
    }

    public void setSupplierCompany(String supplierCompany) {
        this.supplierCompany = supplierCompany;
    }

    public Long getSupplierCompanyId(){
        return Long.valueOf(supplierCompany);
    }

    public void setTransportCompany(TransportCompany transportCompany) {
        this.transportCompany = transportCompany.getName();
    }

    public void setTransportCompany(String  transportCompany) {
        this.transportCompany = transportCompany;
    }

    public Long getTransportCompanyId(){
        return Long.valueOf(transportCompany);
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

    public void setDispatcher(String dispatcher) {
        this.dispatcher = dispatcher;
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
