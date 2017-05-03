package com.itechart.warehouse.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "warehouse_company")
public class WarehouseCompany {
    private Long idWarehouseCompany;
    private String name;
    private Boolean status;
    private List<Warehouse> warehouses;
    private List<PriceList> priceList;
    private List<User> users;

    @OneToMany(mappedBy = "warehouseCompany", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @OneToMany(mappedBy = "warehouseCompany", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Warehouse> getWarehouses() {
        return warehouses;
    }

    public void setWarehouses(List<Warehouse> warehouses) {
        this.warehouses = warehouses;
    }

    @OneToMany(mappedBy = "warehouseCompany", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<PriceList> getPriceList() {
        return priceList;
    }

    public void setPriceList(List<PriceList> priceList) {
        this.priceList = priceList;
    }


    @Id
    @Column(name = "id_warehouse_company")
    public Long getIdWarehouseCompany() {
        return idWarehouseCompany;
    }

    public void setIdWarehouseCompany(Long idWarehouseCompany) {
        this.idWarehouseCompany = idWarehouseCompany;
    }

    @Column(name = "name", length = 50)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "status")
    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WarehouseCompany that = (WarehouseCompany) o;

        if (idWarehouseCompany != null ? !idWarehouseCompany.equals(that.idWarehouseCompany) : that.idWarehouseCompany != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = idWarehouseCompany != null ? idWarehouseCompany.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "WarehouseCompany{" +
                "idWarehouseCompany=" + idWarehouseCompany +
                ", name='" + name + '\'' +
                ", status=" + status +
                '}';
    }
}
