package me.rasztabiga.fridgy.products.repository

import me.rasztabiga.fridgy.products.domain.RecipeIngredient
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data SQL repository for the [RecipeIngredient] entity.
 */
@Suppress("unused")
@Repository
interface RecipeIngredientRepository : JpaRepository<RecipeIngredient, Long>
