package com.itechart.warehouse.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Date;

@Entity
public class Driver {
    private Long id;
    private String fullName;
    private String passportNumber;
    private String countryCode;
    private String issuedBy;
    private Date issueDate;
    @JsonIgnore
    private TransportCompany transportCompany;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_driver", unique = true, nullable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long idDriver) {
        this.id = idDriver;
    }

    @Column(name = "full_name", nullable = false)
    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Column(name = "passport_number", unique = true, nullable = false)
    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    @Column(name = "country_code", nullable = false)
    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Column(name = "issued_by", nullable = false)
    public String getIssuedBy() {
        return issuedBy;
    }

    public void setIssuedBy(String issuedBy) {
        this.issuedBy = issuedBy;
    }

    @Column(name = "issue_date", nullable = false)
    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_transport_company", nullable = false)
    public TransportCompany getTransportCompany() {
        return transportCompany;
    }

    public void setTransportCompany(TransportCompany transportCompany) {
        this.transportCompany = transportCompany;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Driver driver = (Driver) o;

        if (id != null ? !id.equals(driver.id) : driver.id != null) return false;
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
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (fullName != null ? fullName.hashCode() : 0);
        result = 31 * result + (passportNumber != null ? passportNumber.hashCode() : 0);
        result = 31 * result + (countryCode != null ? countryCode.hashCode() : 0);
        result = 31 * result + (issuedBy != null ? issuedBy.hashCode() : 0);
        result = 31 * result + (issueDate != null ? issueDate.hashCode() : 0);
        return result;
    }
}
