package com.itechart.warehouse.entity;

import javax.persistence.*;
import java.sql.Timestamp;


@Entity
@Table(name = "invoice_status")
public class InvoiceStatus {
    private Long id;
    private Timestamp date;
    private InvoiceStatusName statusName;
    private User user;
    private Invoice invoice;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_invoice_status")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "date")
    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_status_name")
    public InvoiceStatusName getStatusName() {
        return statusName;
    }

    public void setStatusName(InvoiceStatusName statusName) {
        this.statusName = statusName;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_user")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_invoice")
    public Invoice getInvoice() {
        return invoice;
    }

    public void setInvoice(Invoice invoice) {
        this.invoice = invoice;
    }
}
