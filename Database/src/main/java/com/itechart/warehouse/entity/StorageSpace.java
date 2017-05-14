package com.itechart.warehouse.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "storage_space")
public class StorageSpace {
    private Long idStorageSpace;
    private StorageSpaceType storageSpaceType;
    @JsonIgnore
    private Warehouse warehouse;

    private List<StorageCell> storageCellList;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_storage_space", unique = true, nullable = false)
    public Long getIdStorageSpace() {
        return idStorageSpace;
    }

    public void setIdStorageSpace(Long idStorageSpace) {
        this.idStorageSpace = idStorageSpace;
    }

    @ManyToOne
    @JoinColumn(name = "id_storage_space_type", nullable = false)
    public StorageSpaceType getStorageSpaceType() {
        return storageSpaceType;
    }

    public void setStorageSpaceType(StorageSpaceType storageSpaceType) {
        this.storageSpaceType = storageSpaceType;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_warehouse", nullable = false)
    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public void addStorageCell(StorageCell storageCell){
        storageCell.setStorageSpace(this);
        storageCellList.add(storageCell);
    }

    @OneToMany(mappedBy = "storageSpace", fetch = FetchType.LAZY)
    public List<StorageCell> getStorageCellList() {
        return storageCellList;
    }
    public void setStorageCellList(List<StorageCell> storageCellList) {
        this.storageCellList = storageCellList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StorageSpace that = (StorageSpace) o;

        if (idStorageSpace != null ? !idStorageSpace.equals(that.idStorageSpace) : that.idStorageSpace != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        return idStorageSpace != null ? idStorageSpace.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "StorageSpace{" +
                "idStorageSpace=" + idStorageSpace +
                ", storageSpaceType=" + storageSpaceType.getName() +
                //", warehouse=" + warehouse +
                ", storageCellList=" + storageCellList +
                '}';
    }
}
