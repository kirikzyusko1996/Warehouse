package com.itechart.warehouse.entity;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "goods_status")
public class GoodsStatus {
    private Long idGoodsStatus;
    private Timestamp date;
    private String note;

    @Id
    @Column(name = "id_goods_status")
    public Long getIdGoodsStatus() {
        return idGoodsStatus;
    }

    public void setIdGoodsStatus(Long idGoodsStatus) {
        this.idGoodsStatus = idGoodsStatus;
    }

    @Column(name = "date")
    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    @Column(name = "note")
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GoodsStatus that = (GoodsStatus) o;

        if (idGoodsStatus != null ? !idGoodsStatus.equals(that.idGoodsStatus) : that.idGoodsStatus != null)
            return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (note != null ? !note.equals(that.note) : that.note != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = idGoodsStatus != null ? idGoodsStatus.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (note != null ? note.hashCode() : 0);
        return result;
    }
}
