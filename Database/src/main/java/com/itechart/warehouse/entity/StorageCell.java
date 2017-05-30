package com.itechart.warehouse.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;

@Entity
@Table(name = "storage_cell")
public class StorageCell {
    private Long idStorageCell;
    private String number;
    @JsonIgnore
    private StorageSpace storageSpace;
    private Boolean status;

    private Goods goods;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_storage_cell", unique = true, nullable = false)
    public Long getIdStorageCell() {
        return idStorageCell;
    }

    public void setIdStorageCell(Long idStorageCell) {
        this.idStorageCell = idStorageCell;
    }

    @Column(name = "number", nullable = false)
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_storage_space", nullable = false)
    public StorageSpace getStorageSpace() {
        return storageSpace;
    }

    public void setStorageSpace(StorageSpace storageSpace) {
        this.storageSpace = storageSpace;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_goods")
    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    @Column(name = "status")
    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StorageCell that = (StorageCell) o;

        if (idStorageCell != null ? !idStorageCell.equals(that.idStorageCell) : that.idStorageCell != null)
            return false;
        if (number != null ? !number.equals(that.number) : that.number != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = idStorageCell != null ? idStorageCell.hashCode() : 0;
        result = 31 * result + (number != null ? number.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "StorageCell{" +
                "idStorageCell=" + idStorageCell +
                ", number='" + number + '\'' +
                ", status='"+ status + '\''+
                '}';
    }
}
