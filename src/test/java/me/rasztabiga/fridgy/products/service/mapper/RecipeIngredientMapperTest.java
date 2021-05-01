package me.rasztabiga.fridgy.products.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RecipeIngredientMapperTest {

    private RecipeIngredientMapper recipeIngredientMapper;

    @BeforeEach
    public void setUp() {
        recipeIngredientMapper = new RecipeIngredientMapperImpl();
    }
}
