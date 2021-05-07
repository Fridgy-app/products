package me.rasztabiga.fridgy.products.service.dto

import me.rasztabiga.fridgy.products.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RecipeDTOTest {

    @Test
    fun dtoEqualsVerifier() {
        equalsVerifier(RecipeDTO::class)
        val recipeDTO1 = RecipeDTO()
        recipeDTO1.id = 1L
        val recipeDTO2 = RecipeDTO()
        assertThat(recipeDTO1).isNotEqualTo(recipeDTO2)
        recipeDTO2.id = recipeDTO1.id
        assertThat(recipeDTO1).isEqualTo(recipeDTO2)
        recipeDTO2.id = 2L
        assertThat(recipeDTO1).isNotEqualTo(recipeDTO2)
        recipeDTO1.id = null
        assertThat(recipeDTO1).isNotEqualTo(recipeDTO2)
    }
}
