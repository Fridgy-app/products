package me.rasztabiga.fridgy.products.repository

import me.rasztabiga.fridgy.products.domain.Authority
import org.springframework.data.jpa.repository.JpaRepository

/**
 * Spring Data JPA repository for the [Authority] entity.
 */

interface AuthorityRepository : JpaRepository<Authority, String>
