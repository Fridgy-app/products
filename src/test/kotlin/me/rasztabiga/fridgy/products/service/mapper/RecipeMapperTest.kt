package me.rasztabiga.fridgy.products.service.mapper

import org.junit.jupiter.api.BeforeEach

class RecipeMapperTest {

    private lateinit var recipeMapper: RecipeMapper

    @BeforeEach
    fun setUp() {
        recipeMapper = RecipeMapperImpl()
    }
}
