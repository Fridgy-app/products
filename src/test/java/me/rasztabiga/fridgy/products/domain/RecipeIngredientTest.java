package me.rasztabiga.fridgy.products.domain;

import static org.assertj.core.api.Assertions.assertThat;

import me.rasztabiga.fridgy.products.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class RecipeIngredientTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RecipeIngredient.class);
        RecipeIngredient recipeIngredient1 = new RecipeIngredient();
        recipeIngredient1.setId(1L);
        RecipeIngredient recipeIngredient2 = new RecipeIngredient();
        recipeIngredient2.setId(recipeIngredient1.getId());
        assertThat(recipeIngredient1).isEqualTo(recipeIngredient2);
        recipeIngredient2.setId(2L);
        assertThat(recipeIngredient1).isNotEqualTo(recipeIngredient2);
        recipeIngredient1.setId(null);
        assertThat(recipeIngredient1).isNotEqualTo(recipeIngredient2);
    }
}
