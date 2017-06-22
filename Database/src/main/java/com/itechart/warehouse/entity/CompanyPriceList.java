package com.itechart.warehouse.entity;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.itechart.warehouse.deserializer.TrimmingJsonDeserializer;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Created by Alexey on 15.06.2017.
 */
@Entity
@Table(name = "company_price_list")
public class CompanyPriceList {
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp startTime;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp endTime;
    private BigDecimal pricePerMonth;
    @JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="idWarehouseCompany")
    @JsonIdentityReference(alwaysAsId=true)
    @JsonProperty("idWarehouseCompany")
    private WarehouseCompany warehouseCompany;
    @JsonDeserialize(using=TrimmingJsonDeserializer.class)
    private String comment;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "price_per_month", nullable = false)
    public BigDecimal getPricePerMonth() {
        return pricePerMonth;
    }

    public void setPricePerMonth(BigDecimal pricePerMonth) {
        this.pricePerMonth = pricePerMonth;
    }

    @Column(name = "setting_time")
    public Timestamp getStartTime() {
        return startTime;
    }
    public void setStartTime(Timestamp startTime) {
        this.startTime = startTime;
    }

    @Column(name = "end_time")
    public Timestamp getEndTime() {
        return endTime;
    }

    public void setEndTime(Timestamp endTime) {
        this.endTime = endTime;
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

        CompanyPriceList that = (CompanyPriceList) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (startTime != null ? !startTime.equals(that.startTime) : that.startTime != null) return false;
        if (endTime != null ? !endTime.equals(that.endTime) : that.endTime != null) return false;
        if (pricePerMonth != null ? !pricePerMonth.equals(that.pricePerMonth) : that.pricePerMonth != null)
            return false;
        if (warehouseCompany != null ? !warehouseCompany.equals(that.warehouseCompany) : that.warehouseCompany != null)
            return false;
        return comment != null ? comment.equals(that.comment) : that.comment == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (startTime != null ? startTime.hashCode() : 0);
        result = 31 * result + (endTime != null ? endTime.hashCode() : 0);
        result = 31 * result + (pricePerMonth != null ? pricePerMonth.hashCode() : 0);
        result = 31 * result + (warehouseCompany != null ? warehouseCompany.hashCode() : 0);
        result = 31 * result + (comment != null ? comment.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CompanyPriceList{" +
                "id=" + id +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", pricePerMonth=" + pricePerMonth +
                ", warehouseCompany=" + warehouseCompany +
                ", comment='" + comment + '\'' +
                '}';
    }
}