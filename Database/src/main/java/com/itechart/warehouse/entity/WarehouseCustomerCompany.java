package com.itechart.warehouse.entity;

import javax.persistence.*;

@Entity
@Table(name = "warehouse_customer_company")
public class WarehouseCustomerCompany{
    private Long id;
    private String name;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_warehouse_customer_company", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
