package me.rasztabiga.fridgy.products.repository

import me.rasztabiga.fridgy.products.domain.ProductCategory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data SQL repository for the [ProductCategory] entity.
 */
@Suppress("unused")
@Repository
interface ProductCategoryRepository : JpaRepository<ProductCategory, Long>
