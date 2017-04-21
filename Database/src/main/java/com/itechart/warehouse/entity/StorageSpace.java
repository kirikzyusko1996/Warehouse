package com.itechart.warehouse.entity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "storage_space")
public class StorageSpace {
    private Long idStorageSpace;
    private StorageSpaceType storageSpaceType;
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

    @ManyToOne
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

    @OneToMany(mappedBy = "storageSpace")
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
}
