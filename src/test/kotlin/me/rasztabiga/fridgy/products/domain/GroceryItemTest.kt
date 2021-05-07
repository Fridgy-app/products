package me.rasztabiga.fridgy.products.domain

import me.rasztabiga.fridgy.products.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GroceryItemTest {

    @Test
    fun equalsVerifier() {
        equalsVerifier(GroceryItem::class)
        val groceryItem1 = GroceryItem()
        groceryItem1.id = 1L
        val groceryItem2 = GroceryItem()
        groceryItem2.id = groceryItem1.id
        assertThat(groceryItem1).isEqualTo(groceryItem2)
        groceryItem2.id = 2L
        assertThat(groceryItem1).isNotEqualTo(groceryItem2)
        groceryItem1.id = null
        assertThat(groceryItem1).isNotEqualTo(groceryItem2)
    }
}
