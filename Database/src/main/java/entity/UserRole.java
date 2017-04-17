package entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "user_role")
public class UserRole {
    private Long idUserRole;

    @Id
    @Column(name = "id_user_role")
    public Long getIdUserRole() {
        return idUserRole;
    }

    public void setIdUserRole(Long idUserRole) {
        this.idUserRole = idUserRole;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserRole userRole = (UserRole) o;

        if (idUserRole != null ? !idUserRole.equals(userRole.idUserRole) : userRole.idUserRole != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return idUserRole != null ? idUserRole.hashCode() : 0;
    }
}
