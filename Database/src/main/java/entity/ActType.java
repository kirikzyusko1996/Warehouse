package entity;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

@Entity
@Table(name = "act_type")
public class ActType {
    private Short id;
    private String name;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_act_type", nullable = false, insertable = true, updatable = false)
    public Short getId() {
        return id;
    }

    public void setId(Short idActType) {
        this.id = idActType;
    }

    @Column(name = "name", unique = true, nullable = false, insertable = true, updatable = true)
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

        ActType actType = (ActType) o;

        if (id != null ? !id.equals(actType.id) : actType.id != null) return false;
        if (name != null ? !name.equals(actType.name) : actType.name != null) return false;

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
                .append("idActType", id)
                .append("name", name)
                .toString();
    }
}
