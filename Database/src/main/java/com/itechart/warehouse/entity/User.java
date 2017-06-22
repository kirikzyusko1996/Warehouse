package com.itechart.warehouse.entity;


import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;


@Entity
@Table(name = "user")
@ToString(exclude = {"warehouseCompany", "warehouse", "roles", "acts", "goodsStatusList", "invoiceStatusList"})
@EqualsAndHashCode(exclude = {"warehouseCompany", "warehouse", "roles", "acts", "goodsStatusList", "invoiceStatusList"})
public class User {
    private Long id;
    private String firstName;
    private String lastName;
    private String patronymic;
    private Date dateOfBirth;
    private String city;
    private String street;
    private String house;
    private String apartment;
    private String email;
    private String login;
    private String password;
    private WarehouseCompany warehouseCompany;
    private Warehouse warehouse;
    private List<Role> roles;
    private List<Act> acts;
    private List<GoodsStatus> goodsStatusList;
    private List<InvoiceStatus> invoiceStatusList;
    private Date deleted;
    private Long presetId;

    @Column(name = "preset")
    public Long getPresetId() {
        return presetId;
    }

    public void setPresetId(Long presetId) {
        this.presetId = presetId;
    }

    public Date getDeleted() {
        return deleted;
    }

    public void setDeleted(Date deleted) {
        this.deleted = deleted;
    }

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<InvoiceStatus> getInvoiceStatusList() {
        return invoiceStatusList;
    }

    public void setInvoiceStatusList(List<InvoiceStatus> invoiceStatusList) {
        this.invoiceStatusList = invoiceStatusList;
    }

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<GoodsStatus> getGoodsStatusList() {
        return goodsStatusList;
    }

    public void setGoodsStatusList(List<GoodsStatus> goodsStatusList) {
        this.goodsStatusList = goodsStatusList;
    }

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    public List<Act> getActs() {
        return acts;
    }

    public void setActs(List<Act> acts) {
        this.acts = acts;
    }

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_role",
            joinColumns = {@JoinColumn(name = "id_user")},
            inverseJoinColumns = {@JoinColumn(name = "id_role")})
    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    public Long getId() {
        return id;
    }

    public void setId(Long idUser) {
        this.id = idUser;
    }

    @Column(name = "first_name", length = 50)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name = "last_name", length = 50)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @Column(name = "patronymic", length = 50)
    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    @Column(name = "date_of_birth")
    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    @Column(name = "city", length = 50)
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    @Column(name = "street", length = 50)
    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @Column(name = "house", length = 50)
    public String getHouse() {
        return house;
    }

    public void setHouse(String house) {
        this.house = house;
    }

    @Column(name = "apartment", length = 50)
    public String getApartment() {
        return apartment;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    @Column(name = "email", unique = true, length = 50)
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Column(name = "login", unique = true, length = 50)
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @Column(name = "password", length = 50)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_warehouse_company")
    public WarehouseCompany getWarehouseCompany() {
        return warehouseCompany;
    }

    public void setWarehouseCompany(WarehouseCompany warehouseCompany) {
        this.warehouseCompany = warehouseCompany;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_warehouse")
    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }


    public boolean hasRole(String role) {
        boolean has = false;
        for (Role r : this.roles) {
            if (r.getName().equals(role)) {
                has = true;
                break;
            }
        }
        return has;
    }

}
