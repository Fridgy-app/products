package me.rasztabiga.fridgy.products.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductUnitMapperTest {

    private ProductUnitMapper productUnitMapper;

    @BeforeEach
    public void setUp() {
        productUnitMapper = new ProductUnitMapperImpl();
    }
}
