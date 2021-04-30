package me.rasztabiga.fridgy.products.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import javax.validation.constraints.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Product.
 */
@Entity
@Table(name = "product")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "ean_code")
    private String eanCode;

    @ManyToMany
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JoinTable(
        name = "rel_product__product_unit",
        joinColumns = @JoinColumn(name = "product_id"),
        inverseJoinColumns = @JoinColumn(name = "product_unit_id")
    )
    @JsonIgnoreProperties(value = { "products" }, allowSetters = true)
    private Set<ProductUnit> productUnits = new HashSet<>();

    @ManyToOne
    @JsonIgnoreProperties(value = { "products" }, allowSetters = true)
    private ProductCategory productCategory;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Product id(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return this.name;
    }

    public Product name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEanCode() {
        return this.eanCode;
    }

    public Product eanCode(String eanCode) {
        this.eanCode = eanCode;
        return this;
    }

    public void setEanCode(String eanCode) {
        this.eanCode = eanCode;
    }

    public Set<ProductUnit> getProductUnits() {
        return this.productUnits;
    }

    public Product productUnits(Set<ProductUnit> productUnits) {
        this.setProductUnits(productUnits);
        return this;
    }

    public Product addProductUnit(ProductUnit productUnit) {
        this.productUnits.add(productUnit);
        productUnit.getProducts().add(this);
        return this;
    }

    public Product removeProductUnit(ProductUnit productUnit) {
        this.productUnits.remove(productUnit);
        productUnit.getProducts().remove(this);
        return this;
    }

    public void setProductUnits(Set<ProductUnit> productUnits) {
        this.productUnits = productUnits;
    }

    public ProductCategory getProductCategory() {
        return this.productCategory;
    }

    public Product productCategory(ProductCategory productCategory) {
        this.setProductCategory(productCategory);
        return this;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Product)) {
            return false;
        }
        return id != null && id.equals(((Product) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Product{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", eanCode='" + getEanCode() + "'" +
            "}";
    }
}
