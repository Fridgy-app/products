package me.rasztabiga.fridgy.products.service.dto

import me.rasztabiga.fridgy.products.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProductUnitDTOTest {

    @Test
    fun dtoEqualsVerifier() {
        equalsVerifier(ProductUnitDTO::class)
        val productUnitDTO1 = ProductUnitDTO()
        productUnitDTO1.id = 1L
        val productUnitDTO2 = ProductUnitDTO()
        assertThat(productUnitDTO1).isNotEqualTo(productUnitDTO2)
        productUnitDTO2.id = productUnitDTO1.id
        assertThat(productUnitDTO1).isEqualTo(productUnitDTO2)
        productUnitDTO2.id = 2L
        assertThat(productUnitDTO1).isNotEqualTo(productUnitDTO2)
        productUnitDTO1.id = null
        assertThat(productUnitDTO1).isNotEqualTo(productUnitDTO2)
    }
}
