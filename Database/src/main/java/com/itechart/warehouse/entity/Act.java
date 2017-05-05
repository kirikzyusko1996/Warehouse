package com.itechart.warehouse.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.ToString;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@ToString(exclude = {"user, goods"})
@Entity
@Table(name = "act")
public class Act {
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private Timestamp date;
    @JsonIgnore
    private User user;
    @JsonIgnore
    private List<Goods> goods;
    private ActType actType;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "act_goods",
            joinColumns = {@JoinColumn(name = "id_act")},
            inverseJoinColumns = {@JoinColumn(name = "id_goods")})
    public List<Goods> getGoods() {
        return goods;
    }

    public void setGoods(List<Goods> goods) {
        this.goods = goods;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_user")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_act_type")
    public ActType getActType() {
        return actType;
    }

    public void setActType(ActType actType) {
        this.actType = actType;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_act", nullable = false, insertable = true, updatable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long idAct) {
        this.id = idAct;
    }

    @Column(name = "date", nullable = false, insertable = true, updatable = false)
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

        if (id != null ? !id.equals(act.id) : act.id != null) return false;
        if (date != null ? !date.equals(act.date) : act.date != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }


}
