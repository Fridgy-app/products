package me.rasztabiga.fridgy.products.repository;

import me.rasztabiga.fridgy.products.domain.RecipeIngredient;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the RecipeIngredient entity.
 */
@SuppressWarnings("unused")
@Repository
public interface RecipeIngredientRepository extends JpaRepository<RecipeIngredient, Long> {}
