package com.itechart.warehouse.entity;

import javax.persistence.*;

@Entity
public class Warehouse {
    private Long idWarehouse;
    private String name;
    private WarehouseCompany warehouseCompany;

    @Id
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Warehouse warehouse = (Warehouse) o;

        if (idWarehouse != null ? !idWarehouse.equals(warehouse.idWarehouse) : warehouse.idWarehouse != null)
            return false;
        if (name != null ? !name.equals(warehouse.name) : warehouse.name != null) return false;
        return warehouseCompany != null ? warehouseCompany.equals(warehouse.warehouseCompany) : warehouse.warehouseCompany == null;
    }

    @Override
    public int hashCode() {
        int result = idWarehouse != null ? idWarehouse.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (warehouseCompany != null ? warehouseCompany.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Warehouse{" +
                "idWarehouse=" + idWarehouse +
                ", name='" + name + '\'' +
                ", warehouseCompany=" + warehouseCompany +
                '}';
    }
}
