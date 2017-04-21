package com.itechart.warehouse.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "price_list")
public class PriceList {
    private Long idPriceList;
    private Timestamp settingTime;
    private BigDecimal dailyPrice;
    private StorageSpaceType storageSpaceType;

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
    public Timestamp getSettingTime() {
        return settingTime;
    }

    public void setSettingTime(Timestamp settingTime) {
        this.settingTime = settingTime;
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




    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PriceList priceList = (PriceList) o;

        if (idPriceList != null ? !idPriceList.equals(priceList.idPriceList) : priceList.idPriceList != null)
            return false;
        if (settingTime != null ? !settingTime.equals(priceList.settingTime) : priceList.settingTime != null)
            return false;
        if (dailyPrice != null ? !dailyPrice.equals(priceList.dailyPrice) : priceList.dailyPrice != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = idPriceList != null ? idPriceList.hashCode() : 0;
        result = 31 * result + (settingTime != null ? settingTime.hashCode() : 0);
        result = 31 * result + (dailyPrice != null ? dailyPrice.hashCode() : 0);
        return result;
    }
}
