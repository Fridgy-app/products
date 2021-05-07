package me.rasztabiga.fridgy.products.service.dto

import java.io.Serializable
import java.util.Objects

/**
 * A DTO for the [me.rasztabiga.fridgy.products.domain.RecipeIngredient] entity.
 */
data class RecipeIngredientDTO(

    var id: Long? = null,

    var quantity: Double? = null,

    var product: ProductDTO? = null,

    var productUnit: ProductUnitDTO? = null,

    var recipe: RecipeDTO? = null
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RecipeIngredientDTO) return false
        val recipeIngredientDTO = other
        if (this.id == null) {
            return false
        }
        return Objects.equals(this.id, recipeIngredientDTO.id)
    }

    override fun hashCode() = Objects.hash(this.id)
}
