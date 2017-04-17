package entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.sql.Date;

@Entity
public class Driver {
    private Long idDriver;
    private String fullName;
    private String passportNumber;
    private String countryCode;
    private String issuedBy;
    private Date issueDate;

    @Id
    @Column(name = "id_driver")
    public Long getIdDriver() {
        return idDriver;
    }

    public void setIdDriver(Long idDriver) {
        this.idDriver = idDriver;
    }

    @Column(name = "full_name")
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Column(name = "passport_number")
    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    @Column(name = "country_code")
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Column(name = "issued_by")
    public String getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }

    @Column(name = "issue_date")
    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Driver driver = (Driver) o;

        if (idDriver != null ? !idDriver.equals(driver.idDriver) : driver.idDriver != null) return false;
        if (fullName != null ? !fullName.equals(driver.fullName) : driver.fullName != null) return false;
        if (passportNumber != null ? !passportNumber.equals(driver.passportNumber) : driver.passportNumber != null)
            return false;
        if (countryCode != null ? !countryCode.equals(driver.countryCode) : driver.countryCode != null) return false;
        if (issuedBy != null ? !issuedBy.equals(driver.issuedBy) : driver.issuedBy != null) return false;
        if (issueDate != null ? !issueDate.equals(driver.issueDate) : driver.issueDate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = idDriver != null ? idDriver.hashCode() : 0;
        result = 31 * result + (fullName != null ? fullName.hashCode() : 0);
        result = 31 * result + (passportNumber != null ? passportNumber.hashCode() : 0);
        result = 31 * result + (countryCode != null ? countryCode.hashCode() : 0);
        result = 31 * result + (issuedBy != null ? issuedBy.hashCode() : 0);
        result = 31 * result + (issueDate != null ? issueDate.hashCode() : 0);
        return result;
    }
}
