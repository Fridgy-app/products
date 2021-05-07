package me.rasztabiga.fridgy.products.domain

import me.rasztabiga.fridgy.products.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RecipeIngredientTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(RecipeIngredient::class)
        val recipeIngredient1 = RecipeIngredient()
        recipeIngredient1.id = 1L
        val recipeIngredient2 = RecipeIngredient()
        recipeIngredient2.id = recipeIngredient1.id
        assertThat(recipeIngredient1).isEqualTo(recipeIngredient2)
        recipeIngredient2.id = 2L
        assertThat(recipeIngredient1).isNotEqualTo(recipeIngredient2)
        recipeIngredient1.id = null
        assertThat(recipeIngredient1).isNotEqualTo(recipeIngredient2)
    }
}
