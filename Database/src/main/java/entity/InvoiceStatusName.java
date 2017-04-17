package entity;

import javax.persistence.*;

/**
 * Created by Анна on 17.04.2017.
 */
@Entity
@Table(name = "invoice_status_name", schema = "warehouse", catalog = "")
public class InvoiceStatusName {
    private Short idInvoiceStatusName;
    private String name;

    @Id
    @Column(name = "id_invoice_status_name")
    public Short getIdInvoiceStatusName() {
        return idInvoiceStatusName;
    }

    public void setIdInvoiceStatusName(Short idInvoiceStatusName) {
        this.idInvoiceStatusName = idInvoiceStatusName;
    }

    @Basic
    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InvoiceStatusName that = (InvoiceStatusName) o;

        if (idInvoiceStatusName != null ? !idInvoiceStatusName.equals(that.idInvoiceStatusName) : that.idInvoiceStatusName != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = idInvoiceStatusName != null ? idInvoiceStatusName.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
