package entity;

import javax.persistence.*;

@Entity
@Table(name = "transport_company", schema = "warehouse", catalog = "")
public class TransportCompany {
    private Long idTransportCompany;
    private String name;
    private Boolean isTrusted;

    @Id
    @Column(name = "id_transport_company")
    public Long getIdTransportCompany() {
        return idTransportCompany;
    }

    public void setIdTransportCompany(Long idTransportCompany) {
        this.idTransportCompany = idTransportCompany;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "is_trusted")
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

        if (idTransportCompany != null ? !idTransportCompany.equals(that.idTransportCompany) : that.idTransportCompany != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (isTrusted != null ? !isTrusted.equals(that.isTrusted) : that.isTrusted != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = idTransportCompany != null ? idTransportCompany.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (isTrusted != null ? isTrusted.hashCode() : 0);
        return result;
    }
}
