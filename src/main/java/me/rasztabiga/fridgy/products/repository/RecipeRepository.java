package me.rasztabiga.fridgy.products.repository;

import java.util.List;
import me.rasztabiga.fridgy.products.domain.Recipe;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the Recipe entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    @Query("select recipe from Recipe recipe where recipe.user.login = ?#{principal.preferredUsername}")
    List<Recipe> findByUserIsCurrentUser();
}
