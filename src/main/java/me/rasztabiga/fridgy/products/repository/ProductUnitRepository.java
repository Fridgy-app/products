package me.rasztabiga.fridgy.products.repository;

import me.rasztabiga.fridgy.products.domain.ProductUnit;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data SQL repository for the ProductUnit entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProductUnitRepository extends JpaRepository<ProductUnit, Long> {}
