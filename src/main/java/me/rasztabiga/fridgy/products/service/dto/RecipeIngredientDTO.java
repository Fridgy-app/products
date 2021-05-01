package me.rasztabiga.fridgy.products.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link me.rasztabiga.fridgy.products.domain.RecipeIngredient} entity.
 */
public class RecipeIngredientDTO implements Serializable {

    private Long id;

    private Double quantity;

    private ProductDTO product;

    private ProductUnitDTO productUnit;

    private RecipeDTO recipe;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    public ProductUnitDTO getProductUnit() {
        return productUnit;
    }

    public void setProductUnit(ProductUnitDTO productUnit) {
        this.productUnit = productUnit;
    }

    public RecipeDTO getRecipe() {
        return recipe;
    }

    public void setRecipe(RecipeDTO recipe) {
        this.recipe = recipe;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RecipeIngredientDTO)) {
            return false;
        }

        RecipeIngredientDTO recipeIngredientDTO = (RecipeIngredientDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, recipeIngredientDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "RecipeIngredientDTO{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", product=" + getProduct() +
            ", productUnit=" + getProductUnit() +
            ", recipe=" + getRecipe() +
            "}";
    }
}
