package entity;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "warehouse_customer_company")
public class WarehouseCustomerCompany implements Serializable{
    private static final long serialVersionUID = 1L;
    private Long idWarehouseCustomerCompany;
    private String name;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_warehouse_customer_company")
    public Long getIdWarehouseCustomerCompany() {
        return idWarehouseCustomerCompany;
    }

    public void setIdWarehouseCustomerCompany(Long idWarehouseCustomerCompany) {
        this.idWarehouseCustomerCompany = idWarehouseCustomerCompany;
    }

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

        WarehouseCustomerCompany that = (WarehouseCustomerCompany) o;

        if (idWarehouseCustomerCompany != null ? !idWarehouseCustomerCompany.equals(that.idWarehouseCustomerCompany) : that.idWarehouseCustomerCompany != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = idWarehouseCustomerCompany != null ? idWarehouseCustomerCompany.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
