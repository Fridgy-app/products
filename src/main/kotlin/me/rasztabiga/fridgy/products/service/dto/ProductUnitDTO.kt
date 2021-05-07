package me.rasztabiga.fridgy.products.service.dto

import java.io.Serializable
import java.util.Objects
import javax.validation.constraints.*

/**
 * A DTO for the [me.rasztabiga.fridgy.products.domain.ProductUnit] entity.
 */
data class ProductUnitDTO(

    var id: Long? = null,

    @get: NotNull
    var name: String? = null
) : Serializable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ProductUnitDTO) return false
        val productUnitDTO = other
        if (this.id == null) {
            return false
        }
        return Objects.equals(this.id, productUnitDTO.id)
    }

    override fun hashCode() = Objects.hash(this.id)
}
