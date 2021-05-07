package me.rasztabiga.fridgy.products.repository

import me.rasztabiga.fridgy.products.domain.ProductUnit
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Spring Data SQL repository for the [ProductUnit] entity.
 */
@Suppress("unused")
@Repository
interface ProductUnitRepository : JpaRepository<ProductUnit, Long>
