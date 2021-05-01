package me.rasztabiga.fridgy.products.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import me.rasztabiga.fridgy.products.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RecipeIngredientDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(RecipeIngredientDTO.class);
        RecipeIngredientDTO recipeIngredientDTO1 = new RecipeIngredientDTO();
        recipeIngredientDTO1.setId(1L);
        RecipeIngredientDTO recipeIngredientDTO2 = new RecipeIngredientDTO();
        assertThat(recipeIngredientDTO1).isNotEqualTo(recipeIngredientDTO2);
        recipeIngredientDTO2.setId(recipeIngredientDTO1.getId());
        assertThat(recipeIngredientDTO1).isEqualTo(recipeIngredientDTO2);
        recipeIngredientDTO2.setId(2L);
        assertThat(recipeIngredientDTO1).isNotEqualTo(recipeIngredientDTO2);
        recipeIngredientDTO1.setId(null);
        assertThat(recipeIngredientDTO1).isNotEqualTo(recipeIngredientDTO2);
    }
}
