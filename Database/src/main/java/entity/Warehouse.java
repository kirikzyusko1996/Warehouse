package entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Warehouse {
    private Long idWarehouse;
    private String name;

    @Id
    @Column(name = "id_warehouse")
    public Long getIdWarehouse() {
        return idWarehouse;
    }

    public void setIdWarehouse(Long idWarehouse) {
        this.idWarehouse = idWarehouse;
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

        Warehouse warehouse = (Warehouse) o;

        if (idWarehouse != null ? !idWarehouse.equals(warehouse.idWarehouse) : warehouse.idWarehouse != null)
            return false;
        if (name != null ? !name.equals(warehouse.name) : warehouse.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = idWarehouse != null ? idWarehouse.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
