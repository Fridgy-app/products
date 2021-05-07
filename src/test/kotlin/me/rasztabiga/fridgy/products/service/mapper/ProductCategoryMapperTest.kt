package me.rasztabiga.fridgy.products.service.mapper

import org.junit.jupiter.api.BeforeEach

class ProductCategoryMapperTest {

    private lateinit var productCategoryMapper: ProductCategoryMapper

    @BeforeEach
    fun setUp() {
        productCategoryMapper = ProductCategoryMapperImpl()
    }
}
