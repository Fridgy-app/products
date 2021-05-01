package me.rasztabiga.fridgy.products.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RecipeMapperTest {

    private RecipeMapper recipeMapper;

    @BeforeEach
    public void setUp() {
        recipeMapper = new RecipeMapperImpl();
    }
}
