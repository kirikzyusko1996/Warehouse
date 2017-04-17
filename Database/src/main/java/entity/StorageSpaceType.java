package entity;

import javax.persistence.*;

@Entity
@Table(name = "storage_space_type", schema = "warehouse", catalog = "")
public class StorageSpaceType {
    private Short idStorageSpaceType;
    private String name;

    @Id
    @Column(name = "id_storage_space_type")
    public Short getIdStorageSpaceType() {
        return idStorageSpaceType;
    }

    public void setIdStorageSpaceType(Short idStorageSpaceType) {
        this.idStorageSpaceType = idStorageSpaceType;
    }

    @Column(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
