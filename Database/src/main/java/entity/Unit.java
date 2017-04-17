package entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Unit {
    private Short idUnit;
    private String name;

    @Id
    @Column(name = "id_unit")
    public Short getIdUnit() {
        return idUnit;
    }

    public void setIdUnit(Short idUnit) {
        this.idUnit = idUnit;
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

        Unit unit = (Unit) o;

        if (idUnit != null ? !idUnit.equals(unit.idUnit) : unit.idUnit != null) return false;
        if (name != null ? !name.equals(unit.name) : unit.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = idUnit != null ? idUnit.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
