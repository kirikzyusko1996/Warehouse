package com.itechart.warehouse.entity;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "storage_space_type")
public class StorageSpaceType {
    private Short idStorageSpaceType;
    private String name;
    private Set<StorageSpace> storageSpaces;
    private Set<PriceList> priceList;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_storage_space_type", unique = true, nullable = false)
    public Short getIdStorageSpaceType() {
        return idStorageSpaceType;
    }

    public void setIdStorageSpaceType(Short idStorageSpaceType) {
        this.idStorageSpaceType = idStorageSpaceType;
    }

    @Column(name = "name", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addStorageSpace(StorageSpace storageSpace){
        storageSpace.setStorageSpaceType(this);
        storageSpaces.add(storageSpace);
    }
    @OneToMany(mappedBy = "storageSpaceType", fetch = FetchType.LAZY)
    public Set<StorageSpace> getStorageSpaces() {
        return storageSpaces;
    }

    public void setStorageSpaces(Set<StorageSpace> storageSpaces) {
        this.storageSpaces = storageSpaces;
    }


    public void addPrice(PriceList price){
        price.setStorageSpaceType(this);
        priceList.add(price);
    }
    @OneToMany(mappedBy = "storageSpaceType", fetch = FetchType.LAZY)
    public Set<PriceList> getPriceList() {
        return priceList;
    }
    public void setPriceList(Set<PriceList> priceList) {
        this.priceList = priceList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StorageSpaceType that = (StorageSpaceType) o;

        if (idStorageSpaceType != null ? !idStorageSpaceType.equals(that.idStorageSpaceType) : that.idStorageSpaceType != null)
            return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = idStorageSpaceType != null ? idStorageSpaceType.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
