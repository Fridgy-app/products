package me.rasztabiga.fridgy.products.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GroceryItemMapperTest {

    private GroceryItemMapper groceryItemMapper;

    @BeforeEach
    public void setUp() {
        groceryItemMapper = new GroceryItemMapperImpl();
    }
}
