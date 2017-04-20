package com.itechart.warehouse.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Timestamp;

@Entity
public class Act {
    private Long idAct;
    private Timestamp date;

    @Id
    @Column(name = "id_act")
    public Long getIdAct() {
        return idAct;
    }

    public void setIdAct(Long idAct) {
        this.idAct = idAct;
    }

    @Column(name = "date")
    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Act act = (Act) o;

        if (idAct != null ? !idAct.equals(act.idAct) : act.idAct != null) return false;
        if (date != null ? !date.equals(act.date) : act.date != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = idAct != null ? idAct.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }
}
