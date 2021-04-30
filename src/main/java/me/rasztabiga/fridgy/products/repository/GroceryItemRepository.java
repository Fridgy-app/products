package me.rasztabiga.fridgy.products.repository;

import java.util.List;
import me.rasztabiga.fridgy.products.domain.GroceryItem;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the GroceryItem entity.
 */
@SuppressWarnings("unused")
@Repository
public interface GroceryItemRepository extends JpaRepository<GroceryItem, Long> {
    @Query("select groceryItem from GroceryItem groceryItem where groceryItem.user.login = ?#{principal.preferredUsername}")
    List<GroceryItem> findByUserIsCurrentUser();
}
