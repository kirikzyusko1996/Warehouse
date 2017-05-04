package com.itechart.warehouse.entity;

import javax.persistence.*;

@Entity
@Table(name = "transport_company")
public class TransportCompany {
    private Long id;
    private String name;
    private Boolean isTrusted;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_transport_company", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long idTransportCompany) {
        this.id = idTransportCompany;
    }

    @Column(name = "name", unique = true, nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "is_trusted", nullable = false)
    public Boolean getTrusted() {
        return isTrusted;
    }

    public void setTrusted(Boolean trusted) {
        isTrusted = trusted;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TransportCompany that = (TransportCompany) o;

        if (id != null ? !id.equals(that.id) : that.id != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (isTrusted != null ? !isTrusted.equals(that.isTrusted) : that.isTrusted != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (isTrusted != null ? isTrusted.hashCode() : 0);
        return result;
    }
}
