package me.rasztabiga.fridgy.products.repository

import me.rasztabiga.fridgy.products.domain.Product
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.Optional

/**
 * Spring Data SQL repository for the [Product] entity.
 */
@Repository
interface ProductRepository : JpaRepository<Product, Long> {

    @Query(
        value = "select distinct product from Product product left join fetch product.productUnits",
        countQuery = "select count(distinct product) from Product product"
    )
    fun findAllWithEagerRelationships(pageable: Pageable): Page<Product>

    @Query("select distinct product from Product product left join fetch product.productUnits")
    fun findAllWithEagerRelationships(): MutableList<Product>

    @Query("select product from Product product left join fetch product.productUnits where product.id =:id")
    fun findOneWithEagerRelationships(@Param("id") id: Long): Optional<Product>
}
