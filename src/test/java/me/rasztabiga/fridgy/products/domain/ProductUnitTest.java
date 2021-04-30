package me.rasztabiga.fridgy.products.domain;

import static org.assertj.core.api.Assertions.assertThat;

import me.rasztabiga.fridgy.products.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductUnitTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductUnit.class);
        ProductUnit productUnit1 = new ProductUnit();
        productUnit1.setId(1L);
        ProductUnit productUnit2 = new ProductUnit();
        productUnit2.setId(productUnit1.getId());
        assertThat(productUnit1).isEqualTo(productUnit2);
        productUnit2.setId(2L);
        assertThat(productUnit1).isNotEqualTo(productUnit2);
        productUnit1.setId(null);
        assertThat(productUnit1).isNotEqualTo(productUnit2);
    }
}
