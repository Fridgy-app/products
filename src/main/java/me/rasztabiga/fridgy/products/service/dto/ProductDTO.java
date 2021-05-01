package me.rasztabiga.fridgy.products.service.dto;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link me.rasztabiga.fridgy.products.domain.Product} entity.
 */
public class ProductDTO implements Serializable {

    private Long id;

    @NotNull
    private String name;

    private String eanCode;

    private Set<ProductUnitDTO> productUnits = new HashSet<>();

    private ProductCategoryDTO productCategory;

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

    public String getEanCode() {
        return eanCode;
    }

    public void setEanCode(String eanCode) {
        this.eanCode = eanCode;
    }

    public Set<ProductUnitDTO> getProductUnits() {
        return productUnits;
    }

    public void setProductUnits(Set<ProductUnitDTO> productUnits) {
        this.productUnits = productUnits;
    }

    public ProductCategoryDTO getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(ProductCategoryDTO productCategory) {
        this.productCategory = productCategory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProductDTO)) {
            return false;
        }

        ProductDTO productDTO = (ProductDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, productDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProductDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", eanCode='" + getEanCode() + "'" +
            ", productUnits=" + getProductUnits() +
            ", productCategory=" + getProductCategory() +
            "}";
    }
}
