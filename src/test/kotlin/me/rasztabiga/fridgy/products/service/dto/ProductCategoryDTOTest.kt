package me.rasztabiga.fridgy.products.service.dto

import me.rasztabiga.fridgy.products.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProductCategoryDTOTest {

    @Test
    fun dtoEqualsVerifier() {
        equalsVerifier(ProductCategoryDTO::class)
        val productCategoryDTO1 = ProductCategoryDTO()
        productCategoryDTO1.id = 1L
        val productCategoryDTO2 = ProductCategoryDTO()
        assertThat(productCategoryDTO1).isNotEqualTo(productCategoryDTO2)
        productCategoryDTO2.id = productCategoryDTO1.id
        assertThat(productCategoryDTO1).isEqualTo(productCategoryDTO2)
        productCategoryDTO2.id = 2L
        assertThat(productCategoryDTO1).isNotEqualTo(productCategoryDTO2)
        productCategoryDTO1.id = null
        assertThat(productCategoryDTO1).isNotEqualTo(productCategoryDTO2)
    }
}
