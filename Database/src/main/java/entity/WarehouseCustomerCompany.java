package entity;

import javax.persistence.*;

@Entity
@Table(name = "warehouse_customer_company")
public class WarehouseCustomerCompany{
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
}
