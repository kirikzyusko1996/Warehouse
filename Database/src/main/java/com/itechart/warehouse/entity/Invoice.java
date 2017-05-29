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
    private Warehouse warehouse;
    private WarehouseCustomerCompany supplierCompany;
    private WarehouseCustomerCompany receiverCompany;
    private Driver driver;
    private Unit goodsQuantityUnit;
    private Unit goodsEntryCountUnit;
    private List<Goods> incomingGoods;
    private List<Goods> outgoingGoods;
    private InvoiceStatus invoiceStatus;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_warehouse")
    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }


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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @OneToOne(fetch = FetchType.EAGER)
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

    @OneToOne (mappedBy="invoice")
    public InvoiceStatus getInvoiceStatus() {
        return invoiceStatus;
    }

    public void setInvoiceStatus(InvoiceStatus invoiceStatus) {
        this.invoiceStatus = invoiceStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Invoice invoice = (Invoice) o;

        if (id != null ? !id.equals(invoice.id) : invoice.id != null) return false;
        if (number != null ? !number.equals(invoice.number) : invoice.number != null) return false;
        if (issueDate != null ? !issueDate.equals(invoice.issueDate) : invoice.issueDate != null) return false;
        if (transportNumber != null ? !transportNumber.equals(invoice.transportNumber) : invoice.transportNumber != null)
            return false;
        if (transportName != null ? !transportName.equals(invoice.transportName) : invoice.transportName != null)
            return false;
        if (goodsQuantity != null ? !goodsQuantity.equals(invoice.goodsQuantity) : invoice.goodsQuantity != null)
            return false;
        if (goodsEntryCount != null ? !goodsEntryCount.equals(invoice.goodsEntryCount) : invoice.goodsEntryCount != null)
            return false;
        if (description != null ? !description.equals(invoice.description) : invoice.description != null) return false;
        if (transportCompany != null ? !transportCompany.equals(invoice.transportCompany) : invoice.transportCompany != null)
            return false;
        if (warehouse != null ? !warehouse.equals(invoice.warehouse) : invoice.warehouse != null)
            return false;
        if (supplierCompany != null ? !supplierCompany.equals(invoice.supplierCompany) : invoice.supplierCompany != null)
            return false;
        if (receiverCompany != null ? !receiverCompany.equals(invoice.receiverCompany) : invoice.receiverCompany != null)
            return false;
        if (driver != null ? !driver.equals(invoice.driver) : invoice.driver != null) return false;
        if (goodsQuantityUnit != null ? !goodsQuantityUnit.equals(invoice.goodsQuantityUnit) : invoice.goodsQuantityUnit != null)
            return false;
        if (goodsEntryCountUnit != null ? !goodsEntryCountUnit.equals(invoice.goodsEntryCountUnit) : invoice.goodsEntryCountUnit != null)
            return false;
        if (incomingGoods != null ? !incomingGoods.equals(invoice.incomingGoods) : invoice.incomingGoods != null)
            return false;
        return outgoingGoods != null ? outgoingGoods.equals(invoice.outgoingGoods) : invoice.outgoingGoods == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (number != null ? number.hashCode() : 0);
        result = 31 * result + (issueDate != null ? issueDate.hashCode() : 0);
        result = 31 * result + (transportNumber != null ? transportNumber.hashCode() : 0);
        result = 31 * result + (transportName != null ? transportName.hashCode() : 0);
        result = 31 * result + (goodsQuantity != null ? goodsQuantity.hashCode() : 0);
        result = 31 * result + (goodsEntryCount != null ? goodsEntryCount.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (transportCompany != null ? transportCompany.hashCode() : 0);
        result = 31 * result + (warehouse != null ? warehouse.hashCode() : 0);
        result = 31 * result + (supplierCompany != null ? supplierCompany.hashCode() : 0);
        result = 31 * result + (receiverCompany != null ? receiverCompany.hashCode() : 0);
        result = 31 * result + (driver != null ? driver.hashCode() : 0);
        result = 31 * result + (goodsQuantityUnit != null ? goodsQuantityUnit.hashCode() : 0);
        result = 31 * result + (goodsEntryCountUnit != null ? goodsEntryCountUnit.hashCode() : 0);
        result = 31 * result + (incomingGoods != null ? incomingGoods.hashCode() : 0);
        result = 31 * result + (outgoingGoods != null ? outgoingGoods.hashCode() : 0);
        return result;
    }
}
