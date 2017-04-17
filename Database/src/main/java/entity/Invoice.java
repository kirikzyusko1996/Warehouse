package entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.sql.Date;

@Entity
public class Invoice {
    private Long idInvoice;
    private String number;
    private Date issueDate;
    private String transportNumber;
    private String transportName;
    private BigDecimal goodsQuantity;
    private Integer goodsEntryCount;
    private String batchDescription;

    @Id
    @Column(name = "id_invoice")
    public Long getIdInvoice() {
        return idInvoice;
    }

    public void setIdInvoice(Long idInvoice) {
        this.idInvoice = idInvoice;
    }

    @Column(name = "number")
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Column(name = "issue_date")
    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    @Column(name = "transport_number")
    public String getTransportNumber() {
        return transportNumber;
    }

    public void setTransportNumber(String transportNumber) {
        this.transportNumber = transportNumber;
    }

    @Column(name = "transport_name")
    public String getTransportName() {
        return transportName;
    }

    public void setTransportName(String transportName) {
        this.transportName = transportName;
    }

    @Column(name = "goods_quantity")
    public BigDecimal getGoodsQuantity() {
        return goodsQuantity;
    }

    public void setGoodsQuantity(BigDecimal goodsQuantity) {
        this.goodsQuantity = goodsQuantity;
    }

    @Column(name = "goods_entry_count")
    public Integer getGoodsEntryCount() {
        return goodsEntryCount;
    }

    public void setGoodsEntryCount(Integer goodsEntryCount) {
        this.goodsEntryCount = goodsEntryCount;
    }

    @Column(name = "batch_description")
    public String getBatchDescription() {
        return batchDescription;
    }

    public void setBatchDescription(String batchDescription) {
        this.batchDescription = batchDescription;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Invoice invoice = (Invoice) o;

        if (idInvoice != null ? !idInvoice.equals(invoice.idInvoice) : invoice.idInvoice != null) return false;
        if (number != null ? !number.equals(invoice.number) : invoice.number != null) return false;
        if (issueDate != null ? !issueDate.equals(invoice.issueDate) : invoice.issueDate != null) return false;
        if (transportNumber != null ? !transportNumber.equals(invoice.transportNumber) : invoice.transportNumber != null)
            return false;
        if (transportName != null ? !transportName.equals(invoice.transportName) : invoice.transportName != null)
            return false;
        if (goodsQuantity != null ? !goodsQuantity.equals(invoice.goodsQuantity) : invoice.goodsQuantity != null)
            return false;
        if (goodsEntryCount != null ? !goodsEntryCount.equals(invoice.goodsEntryCount) : invoice.goodsEntryCount != null)
            return false;
        if (batchDescription != null ? !batchDescription.equals(invoice.batchDescription) : invoice.batchDescription != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = idInvoice != null ? idInvoice.hashCode() : 0;
        result = 31 * result + (number != null ? number.hashCode() : 0);
        result = 31 * result + (issueDate != null ? issueDate.hashCode() : 0);
        result = 31 * result + (transportNumber != null ? transportNumber.hashCode() : 0);
        result = 31 * result + (transportName != null ? transportName.hashCode() : 0);
        result = 31 * result + (goodsQuantity != null ? goodsQuantity.hashCode() : 0);
        result = 31 * result + (goodsEntryCount != null ? goodsEntryCount.hashCode() : 0);
        result = 31 * result + (batchDescription != null ? batchDescription.hashCode() : 0);
        return result;
    }
}
