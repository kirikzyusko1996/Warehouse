package entity;

import javax.persistence.*;
import java.util.List;

@Entity
public class StorageSpace {
    private Long idStorageSpace;
    private StorageSpaceType storageSpaceType;
    private List<StorageCell> storageCellList;
    private Warehouse warehouse;

    @Id
    @Column(name = "id_storage_space")
    public Long getIdStorageSpace() {
        return idStorageSpace;
    }

    public void setIdStorageSpace(Long idStorageSpace) {
        this.idStorageSpace = idStorageSpace;
    }

    @ManyToOne
    @JoinColumn(name = "id_storage_space_type")
    public StorageSpaceType getStorageSpaceType() {
        return storageSpaceType;
    }

    public void setStorageSpaceType(StorageSpaceType storageSpaceType) {
        this.storageSpaceType = storageSpaceType;
    }

    @ManyToOne
    @JoinColumn(name = "id_warehouse")
    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    @OneToMany(mappedBy = "storageSpace")
    public void addStorageCell(StorageCell storageCell){
        storageCell.setStorageSpace(this);
        storageCellList.add(storageCell);
    }

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
