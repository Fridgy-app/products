package me.rasztabiga.fridgy.products.service.dto

import java.io.Serializable
import java.util.Objects

/**
 * A DTO for the [me.rasztabiga.fridgy.products.domain.GroceryItem] entity.
 */
data class GroceryItemDTO(

    var id: Long? = null,

    var quantity: Double? = null,

    var description: String? = null,

    var user: UserDTO? = null,

    var product: ProductDTO? = null,

    var unit: ProductUnitDTO? = null
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GroceryItemDTO) return false
        val groceryItemDTO = other
        if (this.id == null) {
            return false
        }
        return Objects.equals(this.id, groceryItemDTO.id)
    }

    override fun hashCode() = Objects.hash(this.id)
}
