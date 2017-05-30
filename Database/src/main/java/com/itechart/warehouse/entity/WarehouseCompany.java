package com.itechart.warehouse.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "warehouse_company")
public class WarehouseCompany {
    private Long idWarehouseCompany;
    private String name;
    private Boolean status;
    private Float x;
    private Float y;
    private List<Warehouse> warehouses;
    private List<PriceList> priceList;
    private List<User> users;
    private List<WarehouseCompanyStatus> statuses;
    private List<TransportCompany> transportCompanies;
    private List<WarehouseCustomerCompany> customerCompanies;

    @JsonIgnore
    @OneToMany(mappedBy = "warehouseCompany", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "warehouseCompany", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<Warehouse> getWarehouses() {
        return warehouses;
    }

    public void setWarehouses(List<Warehouse> warehouses) {
        this.warehouses = warehouses;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "warehouseCompany", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<PriceList> getPriceList() {
        return priceList;
    }

    public void setPriceList(List<PriceList> priceList) {
        this.priceList = priceList;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "warehouseCompany", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<WarehouseCompanyStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<WarehouseCompanyStatus> statuses) {
        this.statuses = statuses;
    }

    @JsonIgnore
    @OneToMany(mappedBy = "warehouseCompany", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<TransportCompany> getTransportCompanies() {
        return transportCompanies;
    }

    public void setTransportCompanies(List<TransportCompany> transportCompanies) {
        this.transportCompanies = transportCompanies;
    }
    @JsonIgnore
    @OneToMany(mappedBy = "warehouseCompany", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public List<WarehouseCustomerCompany> getCustomerCompanies() {
        return customerCompanies;
    }

    public void setCustomerCompanies(List<WarehouseCustomerCompany> customerCompanies) {
        this.customerCompanies = customerCompanies;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

        WarehouseCompany that = (WarehouseCompany) o;

        if (idWarehouseCompany != null ? !idWarehouseCompany.equals(that.idWarehouseCompany) : that.idWarehouseCompany != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (x != null ? !x.equals(that.x) : that.x != null) return false;
        if (y != null ? !y.equals(that.y) : that.y != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = idWarehouseCompany != null ? idWarehouseCompany.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (x != null ? x.hashCode() : 0);
        result = 31 * result + (y != null ? y.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "WarehouseCompany{" +
                "idWarehouseCompany=" + idWarehouseCompany +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
