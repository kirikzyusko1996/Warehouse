package entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "storage_space", schema = "warehouse", catalog = "")
public class StorageSpace {
    private Long idStorageSpace;

    @Id
    @Column(name = "id_storage_space")
    public Long getIdStorageSpace() {
        return idStorageSpace;
    }

    public void setIdStorageSpace(Long idStorageSpace) {
        this.idStorageSpace = idStorageSpace;
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
