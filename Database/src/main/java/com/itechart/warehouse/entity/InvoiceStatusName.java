package com.itechart.warehouse.entity;

import javax.persistence.*;


@Entity
@Table(name = "invoice_status_name")
public class InvoiceStatusName {
    private Short id;
    private String name;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_invoice_status_name", nullable = false, unique = true)
    public Short getId() {
        return id;
    }

    public void setId(Short id) {
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
