package com.itechart.warehouse.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "goods")
@ToString(exclude = {"incomingInvoice", "outgoingInvoice", "acts", "statuses", "cells", "warehouse", "currentStatus", "registeredStatus", "movedOutStatus"})
@EqualsAndHashCode(exclude = {"incomingInvoice", "outgoingInvoice", "acts", "statuses", "cells", "warehouse", "currentStatus", "registeredStatus", "movedOutStatus"})
public class Goods {
    private Long id;
    private String name;
    private BigDecimal quantity;
    private BigDecimal weight;
    private BigDecimal price;
    @JsonIgnore
    private StorageSpaceType storageType;
    private QuantityUnit quantityUnit;
    private WeightUnit weightUnit;
    private PriceUnit priceUnit;
    @JsonIgnore
    private Invoice incomingInvoice;
    @JsonIgnore
    private Invoice outgoingInvoice;
    @JsonIgnore
    private List<Act> acts;
    @JsonIgnore
    private List<GoodsStatus> statuses;
    @JsonIgnore
    private List<StorageCell> cells;
    private Date deleted;
    @JsonIgnore
    private Warehouse warehouse;
    @JsonIgnore
    private GoodsStatus currentStatus;
    @JsonIgnore
    private GoodsStatus registeredStatus;
    @JsonIgnore
    private GoodsStatus movedOutStatus;
    @JsonIgnore
    private Strategy strategy;

    public Goods() {
    }
//цена, количестводнейреализации, категория товара, ?количество => стратегия
    public Goods(Goods goods) {
        if (goods == null) return;
        this.id = goods.id;
        this.name = goods.name;
        this.quantity = goods.quantity;
        this.weight = goods.weight;
        this.price = goods.price;
        this.storageType = goods.storageType;
        this.quantityUnit = goods.quantityUnit;
        this.weightUnit = goods.weightUnit;
        this.priceUnit = goods.priceUnit;
        this.incomingInvoice = goods.incomingInvoice;
        this.outgoingInvoice = goods.outgoingInvoice;
        this.acts = goods.acts;
        this.statuses = goods.statuses;
        this.cells = goods.cells;
        this.warehouse = goods.warehouse;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_registered_status")
    public GoodsStatus getRegisteredStatus() {
        return registeredStatus;
    }

    public void setRegisteredStatus(GoodsStatus registeredStatus) {
        this.registeredStatus = registeredStatus;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_moved_out_status")
    public GoodsStatus getMovedOutStatus() {
        return movedOutStatus;
    }

    public void setMovedOutStatus(GoodsStatus movedOutStatus) {
        this.movedOutStatus = movedOutStatus;
    }

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_current_status")
    public GoodsStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(GoodsStatus currentStatus) {
        this.currentStatus = currentStatus;
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_warehouse")
    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }

    @OneToMany(mappedBy = "goods", fetch = FetchType.LAZY)
    public List<StorageCell> getCells() {
        return cells;
    }

    public void setCells(List<StorageCell> cells) {
        this.cells = cells;
    }

    public void addStatus(GoodsStatus status) {
        if (statuses == null)
            statuses = new ArrayList<>();
        statuses.add(status);
    }

    public void removeStatus(GoodsStatus status) {
        statuses.remove(status);
    }

    @OneToMany(mappedBy = "goods", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<GoodsStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<GoodsStatus> statuses) {
        this.statuses = statuses;
    }

    public void addAct(Act act) {
        acts.add(act);
    }

    public void removeAct(Act act) {
        acts.remove(act);
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "act_goods",
            joinColumns = {@JoinColumn(name = "id_goods")},
            inverseJoinColumns = {@JoinColumn(name = "id_act")})
    public List<Act> getActs() {
        return acts;
    }

    public void setActs(List<Act> acts) {
        this.acts = acts;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_storage_type")
    public StorageSpaceType getStorageType() {
        return storageType;
    }

    public void setStorageType(StorageSpaceType storageType) {
        this.storageType = storageType;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_quantity_unit")
    public QuantityUnit getQuantityUnit() {
        return quantityUnit;
    }

    public void setQuantityUnit(QuantityUnit quantityUnit) {
        this.quantityUnit = quantityUnit;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_weight_unit")
    public WeightUnit getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(WeightUnit weightUnit) {
        this.weightUnit = weightUnit;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_price_unit")
    public PriceUnit getPriceUnit() {
        return priceUnit;
    }

    public void setPriceUnit(PriceUnit priceUnit) {
        this.priceUnit = priceUnit;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_incoming_invoice")
    public Invoice getIncomingInvoice() {
        return incomingInvoice;
    }

    public void setIncomingInvoice(Invoice incomingInvoice) {
        this.incomingInvoice = incomingInvoice;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_outgoing_invoice")
    public Invoice getOutgoingInvoice() {
        return outgoingInvoice;
    }

    public void setOutgoingInvoice(Invoice outgoingInvoice) {
        this.outgoingInvoice = outgoingInvoice;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_goods", nullable = false, insertable = true, updatable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long idGoods) {
        this.id = idGoods;
    }

    @Column(name = "name", nullable = false, insertable = true, updatable = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "quantity", nullable = false, insertable = true, updatable = true, precision = 10, scale = 3)
    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    @Column(name = "weight", nullable = false, insertable = true, updatable = true, precision = 10, scale = 3)
    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    @Column(name = "price", nullable = false, insertable = true, updatable = true, precision = 12, scale = 2)
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_strategy")
    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }
}
