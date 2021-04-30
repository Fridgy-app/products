package me.rasztabiga.fridgy.products.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A RecipeIngredient.
 */
@Entity
@Table(name = "recipe_ingredient")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class RecipeIngredient implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "quantity")
    private Double quantity;

    @ManyToOne
    @JsonIgnoreProperties(value = { "productUnits", "productCategory" }, allowSetters = true)
    private Product product;

    @ManyToOne
    @JsonIgnoreProperties(value = { "products" }, allowSetters = true)
    private ProductUnit productUnit;

    @ManyToOne
    @JsonIgnoreProperties(value = { "recipeIngredients" }, allowSetters = true)
    private Recipe recipe;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RecipeIngredient id(Long id) {
        this.id = id;
        return this;
    }

    public Double getQuantity() {
        return this.quantity;
    }

    public RecipeIngredient quantity(Double quantity) {
        this.quantity = quantity;
        return this;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Product getProduct() {
        return this.product;
    }

    public RecipeIngredient product(Product product) {
        this.setProduct(product);
        return this;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public ProductUnit getProductUnit() {
        return this.productUnit;
    }

    public RecipeIngredient productUnit(ProductUnit productUnit) {
        this.setProductUnit(productUnit);
        return this;
    }

    public void setProductUnit(ProductUnit productUnit) {
        this.productUnit = productUnit;
    }

    public Recipe getRecipe() {
        return this.recipe;
    }

    public RecipeIngredient recipe(Recipe recipe) {
        this.setRecipe(recipe);
        return this;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RecipeIngredient)) {
            return false;
        }
        return id != null && id.equals(((RecipeIngredient) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RecipeIngredient{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            "}";
    }
}
