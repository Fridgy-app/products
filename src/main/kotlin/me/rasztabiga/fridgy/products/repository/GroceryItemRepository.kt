package me.rasztabiga.fridgy.products.repository

import me.rasztabiga.fridgy.products.domain.GroceryItem
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * Spring Data SQL repository for the [GroceryItem] entity.
 */
@Suppress("unused")
@Repository
interface GroceryItemRepository : JpaRepository<GroceryItem, Long> {

    @Query("select groceryItem from GroceryItem groceryItem where groceryItem.user.login = ?#{principal.preferredUsername}")
    fun findByUserIsCurrentUser(): MutableList<GroceryItem>
}
