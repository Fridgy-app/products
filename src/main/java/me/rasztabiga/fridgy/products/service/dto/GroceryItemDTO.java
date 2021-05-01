package me.rasztabiga.fridgy.products.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link me.rasztabiga.fridgy.products.domain.GroceryItem} entity.
 */
public class GroceryItemDTO implements Serializable {

    private Long id;

    private Double quantity;

    private String description;

    private UserDTO user;

    private ProductDTO product;

    private ProductUnitDTO unit;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }

    public ProductDTO getProduct() {
        return product;
    }

    public void setProduct(ProductDTO product) {
        this.product = product;
    }

    public ProductUnitDTO getUnit() {
        return unit;
    }

    public void setUnit(ProductUnitDTO unit) {
        this.unit = unit;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof GroceryItemDTO)) {
            return false;
        }

        GroceryItemDTO groceryItemDTO = (GroceryItemDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, groceryItemDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "GroceryItemDTO{" +
            "id=" + getId() +
            ", quantity=" + getQuantity() +
            ", description='" + getDescription() + "'" +
            ", user='" + getUser() + "'" +
            ", product=" + getProduct() +
            ", unit=" + getUnit() +
            "}";
    }
}
