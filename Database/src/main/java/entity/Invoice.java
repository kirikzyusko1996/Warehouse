package entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Date;

@Entity
@Table(name = "invoice")
public class Invoice {
    private Long idInvoice;
    private String number;
    private Date issueDate;
    private String transportNumber;
    private String transportName;
    private BigDecimal goodsQuantity;
    private Integer goodsEntryCount;
    private String batchDescription;
    private TransportCompany transportCompany;
    private WarehouseCompany warehouseCompany;
    private WarehouseCustomerCompany supplierCompany;
    private WarehouseCustomerCompany receiverCompany;
    private Driver driver;
    private Unit goodsQuantityUnit;
    private Unit goodsEntryCountUnit;


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_invoice")
    public Long getIdInvoice() {
        return idInvoice;
    }

    public void setIdInvoice(Long idInvoice) {
        this.idInvoice = idInvoice;
    }

    @Column(name = "number")
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Column(name = "issue_date")
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
    public String getBatchDescription() {
        return batchDescription;
    }

    public void setBatchDescription(String batchDescription) {
        this.batchDescription = batchDescription;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_transport_company")
    public TransportCompany getTransportCompany() {
        return transportCompany;
    }

    public void setTransportCompany(TransportCompany transportCompany) {
        this.transportCompany = transportCompany;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_warehouse_company")
    public WarehouseCompany getWarehouseCompany() {
        return warehouseCompany;
    }

    public void setWarehouseCompany(WarehouseCompany warehouseCompany) {
        this.warehouseCompany = warehouseCompany;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_supplier_company")
    public WarehouseCustomerCompany getSupplierCompany() {
        return supplierCompany;
    }

    public void setSupplierCompany(WarehouseCustomerCompany supplierCompany) {
        this.supplierCompany = supplierCompany;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_receiver_company")
    public WarehouseCustomerCompany getReceiverCompany() {
        return receiverCompany;
    }

    public void setReceiverCompany(WarehouseCustomerCompany receiverCompany) {
        this.receiverCompany = receiverCompany;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_driver")
    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_goods_quantity_unit")
    public Unit getGoodsQuantityUnit() {
        return goodsQuantityUnit;
    }

    public void setGoodsQuantityUnit(Unit goodsQuantityUnit) {
        this.goodsQuantityUnit = goodsQuantityUnit;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_goods_entry_count_unit")
    public Unit getGoodsEntryCountUnit() {
        return goodsEntryCountUnit;
    }

    public void setGoodsEntryCountUnit(Unit goodsEntryCountUnit) {
        this.goodsEntryCountUnit = goodsEntryCountUnit;
    }
}
