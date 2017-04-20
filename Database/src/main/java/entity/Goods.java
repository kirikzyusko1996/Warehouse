package entity;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "goods")
public class Goods implements Serializable {
    private Long id;
    private String name;
    private BigDecimal quantity;
    private BigDecimal weight;
    private BigDecimal price;
    private StorageSpaceType storageType;
    private Unit quantityUnit;
    private Unit weightUnit;
    private Unit priceUnit;
    private Invoice incomingInvoice;
    private Invoice outgoingInvoice;
    private Set<Act> acts;

    public void addAct(Act act) {
        acts.add(act);
    }

    public void removeAct(Act act) {
        acts.remove(act);
    }

    @OneToMany(mappedBy = "goods", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    public Set<Act> getActs() {
        return acts;
    }

    public void setActs(Set<Act> acts) {
        this.acts = acts;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_storage_type")
    public StorageSpaceType getStorageType() {
        return storageType;
    }

    public void setStorageType(StorageSpaceType storageType) {
        this.storageType = storageType;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_quantity_unit")
    public Unit getQuantityUnit() {
        return quantityUnit;
    }

    public void setQuantityUnit(Unit quantityUnit) {
        this.quantityUnit = quantityUnit;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_weight_unit")
    public Unit getWeightUnit() {
        return weightUnit;
    }

    public void setWeightUnit(Unit weightUnit) {
        this.weightUnit = weightUnit;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_price_unit")
    public Unit getPriceUnit() {
        return priceUnit;
    }

    public void setPriceUnit(Unit priceUnit) {
        this.priceUnit = priceUnit;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_incoming_invoice")
    public Invoice getIncomingInvoice() {
        return incomingInvoice;
    }

    public void setIncomingInvoice(Invoice incomingInvoice) {
        this.incomingInvoice = incomingInvoice;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_outgoing_invoice")
    public Invoice getOutgoingInvoice() {
        return outgoingInvoice;
    }

    public void setOutgoingInvoice(Invoice outgoingInvoice) {
        this.outgoingInvoice = outgoingInvoice;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_goods", nullable = false, insertable = true, updatable = false)
    public Long getId() {
        return id;
    }

    public void setId(Long idGoods) {
        this.id = idGoods;
    }

    @Column(name = "name", nullable = false, insertable = true, updatable = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "quantity", nullable = false, insertable = true, updatable = true, precision = 10, scale = 3)
    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    @Column(name = "weight", nullable = false, insertable = true, updatable = true, precision = 10, scale = 3)
    public BigDecimal getWeight() {
        return weight;
    }

    public void setWeight(BigDecimal weight) {
        this.weight = weight;
    }

    @Column(name = "price", nullable = false, insertable = true, updatable = true, precision = 12, scale = 2)
    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Goods goods = (Goods) o;

        if (id != null ? !id.equals(goods.id) : goods.id != null) return false;
        if (name != null ? !name.equals(goods.name) : goods.name != null) return false;
        if (quantity != null ? !quantity.equals(goods.quantity) : goods.quantity != null) return false;
        if (weight != null ? !weight.equals(goods.weight) : goods.weight != null) return false;
        if (price != null ? !price.equals(goods.price) : goods.price != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (quantity != null ? quantity.hashCode() : 0);
        result = 31 * result + (weight != null ? weight.hashCode() : 0);
        result = 31 * result + (price != null ? price.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("idGoods", id)
                .append("name", name)
                .append("quantity", quantity)
                .append("weight", weight)
                .append("price", price)
                .append("storageType", storageType)
                .append("quantityUnit", quantityUnit)
                .append("weightUnit", weightUnit)
                .append("priceUnit", priceUnit)
                .toString();
    }
}
