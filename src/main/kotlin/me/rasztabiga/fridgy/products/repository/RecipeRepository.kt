package me.rasztabiga.fridgy.products.repository

import me.rasztabiga.fridgy.products.domain.Recipe
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data SQL repository for the [Recipe] entity.
 */
@Suppress("unused")
@Repository
interface RecipeRepository : JpaRepository<Recipe, Long>
