package com.itechart.warehouse.entity;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "goods_status")
@ToString(exclude = {"goodsStatusName", "user", "goods"})
@EqualsAndHashCode(exclude = {"goodsStatusName", "user", "goods"})
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
}
