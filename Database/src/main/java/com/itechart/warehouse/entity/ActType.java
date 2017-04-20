package com.itechart.warehouse.entity;

import javax.persistence.*;

@Entity
@Table(name = "act_type")
public class ActType {
    private Short idActType;
    private String name;

    @Id
    @Column(name = "id_act_type")
    public Short getIdActType() {
        return idActType;
    }

    public void setIdActType(Short idActType) {
        this.idActType = idActType;
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

        ActType actType = (ActType) o;

        if (idActType != null ? !idActType.equals(actType.idActType) : actType.idActType != null) return false;
        if (name != null ? !name.equals(actType.name) : actType.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = idActType != null ? idActType.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
