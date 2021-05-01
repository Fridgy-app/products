package me.rasztabiga.fridgy.products.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import me.rasztabiga.fridgy.products.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class GroceryItemDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(GroceryItemDTO.class);
        GroceryItemDTO groceryItemDTO1 = new GroceryItemDTO();
        groceryItemDTO1.setId(1L);
        GroceryItemDTO groceryItemDTO2 = new GroceryItemDTO();
        assertThat(groceryItemDTO1).isNotEqualTo(groceryItemDTO2);
        groceryItemDTO2.setId(groceryItemDTO1.getId());
        assertThat(groceryItemDTO1).isEqualTo(groceryItemDTO2);
        groceryItemDTO2.setId(2L);
        assertThat(groceryItemDTO1).isNotEqualTo(groceryItemDTO2);
        groceryItemDTO1.setId(null);
        assertThat(groceryItemDTO1).isNotEqualTo(groceryItemDTO2);
    }
}
