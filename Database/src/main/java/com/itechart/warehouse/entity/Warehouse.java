package com.itechart.warehouse.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
public class Warehouse {
    private Long idWarehouse;
    private String name;
    private Boolean status;
    private Float x;
    private Float y;
    private WarehouseCompany warehouseCompany;
    private List<StorageSpace> storageSpaceList;
    private List<Invoice> invoices;
    private List<Act> acts;
    private List<Goods> goodsList;

    @JsonIgnore
    @OneToMany(mappedBy = "warehouse", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<Goods> getGoodsList() {
        return goodsList;
    }

    public void setGoodsList(List<Goods> goodsList) {
        this.goodsList = goodsList;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "warehouse", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<Act> getActs() {
        return acts;
    }

    public void setActs(List<Act> acts) {
        this.acts = acts;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "warehouse", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<Invoice> getInvoices() {
        return invoices;
    }

    public void setInvoices(List<Invoice> invoices) {
        this.invoices = invoices;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "warehouse", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<StorageSpace> getStorageSpaceList() {
        return storageSpaceList;
    }

    public void setStorageSpaceList(List<StorageSpace> storageSpaceList) {
        this.storageSpaceList = storageSpaceList;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_warehouse")
    public Long getIdWarehouse() {
        return idWarehouse;
    }

    public void setIdWarehouse(Long idWarehouse) {
        this.idWarehouse = idWarehouse;
    }

    @Column(name = "name", length = 50)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_warehouse_company")
    public WarehouseCompany getWarehouseCompany() {
        return warehouseCompany;
    }

    public void setWarehouseCompany(WarehouseCompany warehouseCompany) {
        this.warehouseCompany = warehouseCompany;
    }

    @Column(name = "status")
    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Column(name = "x")
    public Float getX() {
        return x;
    }

    public void setX(Float x) {
        this.x = x;
    }

    @Column(name = "y")
    public Float getY() {
        return y;
    }

    public void setY(Float y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Warehouse warehouse = (Warehouse) o;

        if (idWarehouse != null ? !idWarehouse.equals(warehouse.idWarehouse) : warehouse.idWarehouse != null)
            return false;
        if (name != null ? !name.equals(warehouse.name) : warehouse.name != null) return false;

        if (status != null ? !status.equals(warehouse.status) : warehouse.status != null) return false;
        if (x != null ? !x.equals(warehouse.x) : warehouse.x != null) return false;
        if (y != null ? !y.equals(warehouse.y) : warehouse.y != null) return false;

        return warehouseCompany != null ? warehouseCompany.equals(warehouse.warehouseCompany) : warehouse.warehouseCompany == null;
    }

    @Override
    public int hashCode() {
        int result = idWarehouse != null ? idWarehouse.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (x != null ? x.hashCode() : 0);
        result = 31 * result + (y != null ? y.hashCode() : 0);
        result = 31 * result + (warehouseCompany != null ? warehouseCompany.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Warehouse{" +
                "idWarehouse=" + idWarehouse +
                ", name='" + name + '\'' +
                ", status='" + status + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", warehouseCompany=" + warehouseCompany +
                '}';
    }
}
