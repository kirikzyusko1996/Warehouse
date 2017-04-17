package entity;

import javax.persistence.*;

@Entity
@Table(name = "goods_status_name")
public class GoodsStatusName {
    private Short idGoodsStatusName;
    private String name;

    @Id
    @Column(name = "id_goods_status_name")
    public Short getIdGoodsStatusName() {
        return idGoodsStatusName;
    }

    public void setIdGoodsStatusName(Short idGoodsStatusName) {
        this.idGoodsStatusName = idGoodsStatusName;
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

        GoodsStatusName that = (GoodsStatusName) o;

        if (idGoodsStatusName != null ? !idGoodsStatusName.equals(that.idGoodsStatusName) : that.idGoodsStatusName != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = idGoodsStatusName != null ? idGoodsStatusName.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
