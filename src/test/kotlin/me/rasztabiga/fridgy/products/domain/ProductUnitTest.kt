package me.rasztabiga.fridgy.products.domain

import me.rasztabiga.fridgy.products.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ProductUnitTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(ProductUnit::class)
        val productUnit1 = ProductUnit()
        productUnit1.id = 1L
        val productUnit2 = ProductUnit()
        productUnit2.id = productUnit1.id
        assertThat(productUnit1).isEqualTo(productUnit2)
        productUnit2.id = 2L
        assertThat(productUnit1).isNotEqualTo(productUnit2)
        productUnit1.id = null
        assertThat(productUnit1).isNotEqualTo(productUnit2)
    }
}
