package me.rasztabiga.fridgy.products.service.dto

import me.rasztabiga.fridgy.products.web.rest.equalsVerifier
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GroceryItemDTOTest {

    @Test
    fun dtoEqualsVerifier() {
        equalsVerifier(GroceryItemDTO::class)
        val groceryItemDTO1 = GroceryItemDTO()
        groceryItemDTO1.id = 1L
        val groceryItemDTO2 = GroceryItemDTO()
        assertThat(groceryItemDTO1).isNotEqualTo(groceryItemDTO2)
        groceryItemDTO2.id = groceryItemDTO1.id
        assertThat(groceryItemDTO1).isEqualTo(groceryItemDTO2)
        groceryItemDTO2.id = 2L
        assertThat(groceryItemDTO1).isNotEqualTo(groceryItemDTO2)
        groceryItemDTO1.id = null
        assertThat(groceryItemDTO1).isNotEqualTo(groceryItemDTO2)
    }
}
