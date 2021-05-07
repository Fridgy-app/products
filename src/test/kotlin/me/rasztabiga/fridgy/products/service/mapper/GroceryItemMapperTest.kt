package me.rasztabiga.fridgy.products.service.mapper

import org.junit.jupiter.api.BeforeEach

class GroceryItemMapperTest {

    private lateinit var groceryItemMapper: GroceryItemMapper

    @BeforeEach
    fun setUp() {
        groceryItemMapper = GroceryItemMapperImpl()
    }
}
