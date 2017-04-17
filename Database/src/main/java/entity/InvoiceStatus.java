package entity;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by Анна on 17.04.2017.
 */
@Entity
@Table(name = "invoice_status", schema = "warehouse", catalog = "")
public class InvoiceStatus {
    private Long idInvoiceStatus;
    private Timestamp date;

    @Id
    @Column(name = "id_invoice_status")
    public Long getIdInvoiceStatus() {
        return idInvoiceStatus;
    }

    public void setIdInvoiceStatus(Long idInvoiceStatus) {
        this.idInvoiceStatus = idInvoiceStatus;
    }

    @Basic
    @Column(name = "date")
    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InvoiceStatus that = (InvoiceStatus) o;

        if (idInvoiceStatus != null ? !idInvoiceStatus.equals(that.idInvoiceStatus) : that.idInvoiceStatus != null)
            return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = idInvoiceStatus != null ? idInvoiceStatus.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }
}
