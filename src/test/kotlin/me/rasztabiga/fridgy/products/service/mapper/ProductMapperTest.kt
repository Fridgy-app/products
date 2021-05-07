package me.rasztabiga.fridgy.products.service.mapper

import org.junit.jupiter.api.BeforeEach

class ProductMapperTest {

    private lateinit var productMapper: ProductMapper

    @BeforeEach
    fun setUp() {
        productMapper = ProductMapperImpl()
    }
}
