package com.itechart.warehouse.entity;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.itechart.warehouse.deserializer.TrimmingJsonDeserializer;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "price_list")
public class PriceList {
    private Long idPriceList;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp endTime;
    private BigDecimal dailyPrice;
    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="idStorageSpaceType")
    @JsonIdentityReference(alwaysAsId=true)
    @JsonProperty("idStorageSpaceType")
    private StorageSpaceType storageSpaceType;
    @JsonIgnore
    private WarehouseCompany warehouseCompany;
    @JsonDeserialize(using=TrimmingJsonDeserializer.class)
    private String comment;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_price_list", unique = true, nullable = false)
    public Long getIdPriceList() {
        return idPriceList;
    }

    public void setIdPriceList(Long idPriceList) {
        this.idPriceList = idPriceList;
    }

    @Column(name = "setting_time", nullable = false)
//    @Temporal(TemporalType.TIMESTAMP)
    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
    }

    @Column(name = "daily_price", nullable = false)
    public BigDecimal getDailyPrice() {
        return dailyPrice;
    }

    public void setDailyPrice(BigDecimal dailyPrice) {
        this.dailyPrice = dailyPrice;
    }

    @ManyToOne
    @JoinColumn(name = "id_storage_space_type", nullable = false)
    public StorageSpaceType getStorageSpaceType() {
        return storageSpaceType;
    }

    public void setStorageSpaceType(StorageSpaceType storageSpaceType) {
        this.storageSpaceType = storageSpaceType;
    }

    @ManyToOne
    @JoinColumn(name = "id_warehouse_company", nullable = false)
    public WarehouseCompany getWarehouseCompany() {
        return warehouseCompany;
    }

    public void setWarehouseCompany(WarehouseCompany warehouseCompany) {
        this.warehouseCompany = warehouseCompany;
    }

    @Column(name = "comment")
    public String getComment(){
        return comment;
    }
    public void setComment(String comment){
        this.comment = comment;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PriceList priceList = (PriceList) o;

        if (idPriceList != null ? !idPriceList.equals(priceList.idPriceList) : priceList.idPriceList != null)
            return false;
        if (endTime != null ? !endTime.equals(priceList.endTime) : priceList.endTime != null) return false;
        if (dailyPrice != null ? !dailyPrice.equals(priceList.dailyPrice) : priceList.dailyPrice != null) return false;
        if (storageSpaceType != null ? !storageSpaceType.equals(priceList.storageSpaceType) : priceList.storageSpaceType != null)
            return false;
        return warehouseCompany != null ? warehouseCompany.equals(priceList.warehouseCompany) : priceList.warehouseCompany == null;
    }

    @Override
    public int hashCode() {
        int result = idPriceList != null ? idPriceList.hashCode() : 0;
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        result = 31 * result + (dailyPrice != null ? dailyPrice.hashCode() : 0);
        result = 31 * result + (storageSpaceType != null ? storageSpaceType.hashCode() : 0);
        result = 31 * result + (warehouseCompany != null ? warehouseCompany.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PriceList{" +
                "idPriceList=" + idPriceList +
                ", endTime=" + endTime +
                ", dailyPrice=" + dailyPrice +
                ", storageSpaceType=" + storageSpaceType +
                ", warehouseCompany=" + warehouseCompany +
                '}';
    }
}
