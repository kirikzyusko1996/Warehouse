package com.itechart.warehouse.entity;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "goods_status_name")
@ToString
@EqualsAndHashCode
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

    @Column(name = "name", unique = true, nullable = false, insertable = true, updatable = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
