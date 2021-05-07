package me.rasztabiga.fridgy.products.service.mapper

import org.junit.jupiter.api.BeforeEach

class ProductUnitMapperTest {

    private lateinit var productUnitMapper: ProductUnitMapper

    @BeforeEach
    fun setUp() {
        productUnitMapper = ProductUnitMapperImpl()
    }
}
