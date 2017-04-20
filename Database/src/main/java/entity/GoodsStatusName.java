package entity;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

@Entity
@Table(name = "goods_status_name")
public class GoodsStatusName {
    private Short id;
    private String name;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_goods_status_name", nullable = false, insertable = true, updatable = false)
    public Short getId() {
        return id;
    }

    public void setId(Short idGoodsStatusName) {
        this.id = idGoodsStatusName;
    }

    @Column(name = "name", unique = true,nullable = false, insertable = true, updatable = true)
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

        GoodsStatusName that = (GoodsStatusName) o;

        if (id != null ? !id.equals(that.id) : that.id != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

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
                .append("idGoodsStatusName", id)
                .append("name", name)
                .toString();
    }
}
