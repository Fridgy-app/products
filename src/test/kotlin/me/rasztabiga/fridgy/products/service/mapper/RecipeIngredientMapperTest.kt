package me.rasztabiga.fridgy.products.service.mapper

import org.junit.jupiter.api.BeforeEach

class RecipeIngredientMapperTest {

    private lateinit var recipeIngredientMapper: RecipeIngredientMapper

    @BeforeEach
    fun setUp() {
        recipeIngredientMapper = RecipeIngredientMapperImpl()
    }
}
