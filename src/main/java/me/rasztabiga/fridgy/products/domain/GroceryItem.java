package me.rasztabiga.fridgy.products.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A GroceryItem.
 */
@Entity
@Table(name = "grocery_item")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class GroceryItem implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "quantity")
    private Double quantity;

    @Column(name = "description")
    private String description;

    @ManyToOne
    private User user;

    @ManyToOne
    @JsonIgnoreProperties(value = { "productUnits", "productCategory" }, allowSetters = true)
    private Product product;

    @ManyToOne
    @JsonIgnoreProperties(value = { "products" }, allowSetters = true)
    private ProductUnit unit;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public GroceryItem id(Long id) {
        this.id = id;
        return this;
    }

    public Double getQuantity() {
        return this.quantity;
    }

    public GroceryItem quantity(Double quantity) {
        this.quantity = quantity;
        return this;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return this.description;
    }

    public GroceryItem description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public User getUser() {
        return this.user;
    }

    public GroceryItem user(User user) {
        this.setUser(user);
        return this;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return this.product;
    }

    public GroceryItem product(Product product) {
        this.setProduct(product);
        return this;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProductUnit getUnit() {
        return this.unit;
    }

    public GroceryItem unit(ProductUnit productUnit) {
        this.setUnit(productUnit);
        return this;
    }

    public void setUnit(ProductUnit productUnit) {
        this.unit = productUnit;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GroceryItem)) {
            return false;
        }
        return id != null && id.equals(((GroceryItem) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GroceryItem{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
