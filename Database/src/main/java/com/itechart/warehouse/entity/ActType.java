package com.itechart.warehouse.entity;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Table(name = "act_type")
@ToString
@EqualsAndHashCode
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
}
