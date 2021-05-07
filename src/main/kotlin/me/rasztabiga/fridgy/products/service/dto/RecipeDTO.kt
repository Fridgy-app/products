package me.rasztabiga.fridgy.products.service.dto

import java.io.Serializable
import java.util.Objects
import javax.validation.constraints.*

/**
 * A DTO for the [me.rasztabiga.fridgy.products.domain.Recipe] entity.
 */
data class RecipeDTO(

    var id: Long? = null,

    @get: NotNull
    var name: String? = null,

    @get: NotNull
    var instructionsBody: String? = null
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RecipeDTO) return false
        val recipeDTO = other
        if (this.id == null) {
            return false
        }
        return Objects.equals(this.id, recipeDTO.id)
    }

    override fun hashCode() = Objects.hash(this.id)
}
