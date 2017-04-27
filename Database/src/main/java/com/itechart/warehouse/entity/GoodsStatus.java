package com.itechart.warehouse.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "goods_status")
public class GoodsStatus {
    private Long id;
    private Timestamp date;
    private String note;

    private GoodsStatusName goodsStatusName;
    private User user;
    private Goods goods;


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_goods_status_name")
    public GoodsStatusName getGoodsStatusName() {
        return goodsStatusName;
    }

    public void setGoodsStatusName(GoodsStatusName goodsStatusName) {
        this.goodsStatusName = goodsStatusName;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_goods")
    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_goods_status", nullable = false, insertable = true, updatable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long idGoodsStatus) {
        this.id = idGoodsStatus;
    }

    @Column(name = "date", nullable = false, insertable = true, updatable = false)
    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    @Column(name = "note", nullable = true, insertable = true, updatable = true)
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

        if (id != null ? !id.equals(that.id) : that.id != null)
            return false;
        if (date != null ? !date.equals(that.date) : that.date != null) return false;
        if (note != null ? !note.equals(that.note) : that.note != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (note != null ? note.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("idGoodsStatus", id)
                .append("date", date)
                .append("note", note)
                .append("goodsStatusName", goodsStatusName)
                .append("user", user)
                .append("goodsList", goods)
                .toString();
    }
}
