package me.rasztabiga.fridgy.products.service.dto

import me.rasztabiga.fridgy.products.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RecipeIngredientDTOTest {

    @Test
    fun dtoEqualsVerifier() {
        equalsVerifier(RecipeIngredientDTO::class)
        val recipeIngredientDTO1 = RecipeIngredientDTO()
        recipeIngredientDTO1.id = 1L
        val recipeIngredientDTO2 = RecipeIngredientDTO()
        assertThat(recipeIngredientDTO1).isNotEqualTo(recipeIngredientDTO2)
        recipeIngredientDTO2.id = recipeIngredientDTO1.id
        assertThat(recipeIngredientDTO1).isEqualTo(recipeIngredientDTO2)
        recipeIngredientDTO2.id = 2L
        assertThat(recipeIngredientDTO1).isNotEqualTo(recipeIngredientDTO2)
        recipeIngredientDTO1.id = null
        assertThat(recipeIngredientDTO1).isNotEqualTo(recipeIngredientDTO2)
    }
}
