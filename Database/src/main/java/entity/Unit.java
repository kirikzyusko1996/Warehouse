package entity;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

@Entity
@Table(name = "unit")
public class Unit {
    private Short id;
    private String name;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_unit", nullable = false, insertable = true, updatable = false)
    public Short getId() {
        return id;
    }

    public void setId(Short idUnit) {
        this.id = idUnit;
    }

    @Column(name = "name", nullable = false, insertable = true, updatable = true)
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

        if (id != null ? !id.equals(unit.id) : unit.id != null) return false;
        if (name != null ? !name.equals(unit.name) : unit.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("idUnit", id)
                .append("name", name)
                .toString();
    }
}
