package me.rasztabiga.fridgy.products.domain;

import static org.assertj.core.api.Assertions.assertThat;

import me.rasztabiga.fridgy.products.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class GroceryItemTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(GroceryItem.class);
        GroceryItem groceryItem1 = new GroceryItem();
        groceryItem1.setId(1L);
        GroceryItem groceryItem2 = new GroceryItem();
        groceryItem2.setId(groceryItem1.getId());
        assertThat(groceryItem1).isEqualTo(groceryItem2);
        groceryItem2.setId(2L);
        assertThat(groceryItem1).isNotEqualTo(groceryItem2);
        groceryItem1.setId(null);
        assertThat(groceryItem1).isNotEqualTo(groceryItem2);
    }
}
