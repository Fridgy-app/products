package me.rasztabiga.fridgy.products.service.dto

import java.io.Serializable
import java.util.Objects
import javax.validation.constraints.*

/**
 * A DTO for the [me.rasztabiga.fridgy.products.domain.Product] entity.
 */
data class ProductDTO(

    var id: Long? = null,

    @get: NotNull
    var name: String? = null,

    var eanCode: String? = null,

    var productUnits: MutableSet<ProductUnitDTO> = mutableSetOf(),

    var productCategory: ProductCategoryDTO? = null
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProductDTO) return false
        val productDTO = other
        if (this.id == null) {
            return false
        }
        return Objects.equals(this.id, productDTO.id)
    }

    override fun hashCode() = Objects.hash(this.id)
}
