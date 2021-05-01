package me.rasztabiga.fridgy.products.service.dto;

import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link me.rasztabiga.fridgy.products.domain.ProductUnit} entity.
 */
public class ProductUnitDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductUnitDTO)) {
            return false;
        }

        ProductUnitDTO productUnitDTO = (ProductUnitDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productUnitDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductUnitDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            "}";
    }
}
