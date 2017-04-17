package entity;

import javax.persistence.*;

@Entity
@Table(name = "storage_cell")
public class StorageCell {
    private Long idStorageCell;
    private String number;

    @Id
    @Column(name = "id_storage_cell")
    public Long getIdStorageCell() {
        return idStorageCell;
    }

    public void setIdStorageCell(Long idStorageCell) {
        this.idStorageCell = idStorageCell;
    }

    @Column(name = "number")
    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StorageCell that = (StorageCell) o;

        if (idStorageCell != null ? !idStorageCell.equals(that.idStorageCell) : that.idStorageCell != null)
            return false;
        if (number != null ? !number.equals(that.number) : that.number != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = idStorageCell != null ? idStorageCell.hashCode() : 0;
        result = 31 * result + (number != null ? number.hashCode() : 0);
        return result;
    }
}
