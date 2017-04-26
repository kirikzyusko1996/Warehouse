package com.itechart.warehouse.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;

@Entity
@Table(name = "invoice")
public class Invoice {
    private Long id;
    private String number;
    private Date issueDate;
    private String transportNumber;
    private String transportName;
    private BigDecimal goodsQuantity;
    private Integer goodsEntryCount;
    private String description;
    private TransportCompany transportCompany;
    private WarehouseCompany warehouseCompany;
    private WarehouseCustomerCompany supplierCompany;
    private WarehouseCustomerCompany receiverCompany;
    private Driver driver;
    private Unit goodsQuantityUnit;
    private Unit goodsEntryCountUnit;
    private List<Goods> incomingGoods;
    private List<Goods> outgoingGoods;

    @OneToMany(mappedBy = "incomingInvoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Goods> getIncomingGoods() {
        return incomingGoods;
    }

    public void setIncomingGoods(List<Goods> incomingGoods) {
        this.incomingGoods = incomingGoods;
    }
    @OneToMany(mappedBy = "outgoingInvoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Goods> getOutgoingGoods() {
        return outgoingGoods;
    }

    public void setOutgoingGoods(List<Goods> outgoingGoods) {
        this.outgoingGoods = outgoingGoods;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_invoice", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "number", nullable = false)
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Column(name = "issue_date", nullable = false)
    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    @Column(name = "transport_number")
    public String getTransportNumber() {
        return transportNumber;
    }

    public void setTransportNumber(String transportNumber) {
        this.transportNumber = transportNumber;
    }

    @Column(name = "transport_name")
    public String getTransportName() {
        return transportName;
    }

    public void setTransportName(String transportName) {
        this.transportName = transportName;
    }

    @Column(name = "goods_quantity")
    public BigDecimal getGoodsQuantity() {
        return goodsQuantity;
    }

    public void setGoodsQuantity(BigDecimal goodsQuantity) {
        this.goodsQuantity = goodsQuantity;
    }

    @Column(name = "goods_entry_count")
    public Integer getGoodsEntryCount() {
        return goodsEntryCount;
    }

    public void setGoodsEntryCount(Integer goodsEntryCount) {
        this.goodsEntryCount = goodsEntryCount;
    }

    @Column(name = "batch_description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_transport_company")
    public TransportCompany getTransportCompany() {
        return transportCompany;
    }

    public void setTransportCompany(TransportCompany transportCompany) {
        this.transportCompany = transportCompany;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_warehouse_company")
    public WarehouseCompany getWarehouseCompany() {
        return warehouseCompany;
    }

    public void setWarehouseCompany(WarehouseCompany warehouseCompany) {
        this.warehouseCompany = warehouseCompany;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_supplier_company")
    public WarehouseCustomerCompany getSupplierCompany() {
        return supplierCompany;
    }

    public void setSupplierCompany(WarehouseCustomerCompany supplierCompany) {
        this.supplierCompany = supplierCompany;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_receiver_company")
    public WarehouseCustomerCompany getReceiverCompany() {
        return receiverCompany;
    }

    public void setReceiverCompany(WarehouseCustomerCompany receiverCompany) {
        this.receiverCompany = receiverCompany;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_driver")
    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_goods_quantity_unit")
    public Unit getGoodsQuantityUnit() {
        return goodsQuantityUnit;
    }

    public void setGoodsQuantityUnit(Unit goodsQuantityUnit) {
        this.goodsQuantityUnit = goodsQuantityUnit;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_goods_entry_count_unit")
    public Unit getGoodsEntryCountUnit() {
        return goodsEntryCountUnit;
    }

    public void setGoodsEntryCountUnit(Unit goodsEntryCountUnit) {
        this.goodsEntryCountUnit = goodsEntryCountUnit;
    }
}
