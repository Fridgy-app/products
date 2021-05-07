package me.rasztabiga.fridgy.products.service.dto

import me.rasztabiga.fridgy.products.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProductDTOTest {

    @Test
    fun dtoEqualsVerifier() {
        equalsVerifier(ProductDTO::class)
        val productDTO1 = ProductDTO()
        productDTO1.id = 1L
        val productDTO2 = ProductDTO()
        assertThat(productDTO1).isNotEqualTo(productDTO2)
        productDTO2.id = productDTO1.id
        assertThat(productDTO1).isEqualTo(productDTO2)
        productDTO2.id = 2L
        assertThat(productDTO1).isNotEqualTo(productDTO2)
        productDTO1.id = null
        assertThat(productDTO1).isNotEqualTo(productDTO2)
    }
}
