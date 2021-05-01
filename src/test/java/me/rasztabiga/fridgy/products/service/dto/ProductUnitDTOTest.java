package me.rasztabiga.fridgy.products.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import me.rasztabiga.fridgy.products.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProductUnitDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProductUnitDTO.class);
        ProductUnitDTO productUnitDTO1 = new ProductUnitDTO();
        productUnitDTO1.setId(1L);
        ProductUnitDTO productUnitDTO2 = new ProductUnitDTO();
        assertThat(productUnitDTO1).isNotEqualTo(productUnitDTO2);
        productUnitDTO2.setId(productUnitDTO1.getId());
        assertThat(productUnitDTO1).isEqualTo(productUnitDTO2);
        productUnitDTO2.setId(2L);
        assertThat(productUnitDTO1).isNotEqualTo(productUnitDTO2);
        productUnitDTO1.setId(null);
        assertThat(productUnitDTO1).isNotEqualTo(productUnitDTO2);
    }
}
