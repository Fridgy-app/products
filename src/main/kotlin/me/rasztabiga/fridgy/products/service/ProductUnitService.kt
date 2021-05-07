package me.rasztabiga.fridgy.products.service

import me.rasztabiga.fridgy.products.domain.ProductUnit
import me.rasztabiga.fridgy.products.repository.ProductUnitRepository
import me.rasztabiga.fridgy.products.service.dto.ProductUnitDTO
import me.rasztabiga.fridgy.products.service.mapper.ProductUnitMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

/**
 * Service Implementation for managing [ProductUnit].
 */
@Service
@Transactional
class ProductUnitService(
    private val productUnitRepository: ProductUnitRepository,
    private val productUnitMapper: ProductUnitMapper
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a productUnit.
     *
     * @param productUnitDTO the entity to save.
     * @return the persisted entity.
     */
    fun save(productUnitDTO: ProductUnitDTO): ProductUnitDTO {
        log.debug("Request to save ProductUnit : $productUnitDTO")

        var productUnit = productUnitMapper.toEntity(productUnitDTO)
        productUnit = productUnitRepository.save(productUnit)
        return productUnitMapper.toDto(productUnit)
    }

    /**
     * Partially updates a productUnit.
     *
     * @param productUnitDTO the entity to update partially.
     * @return the persisted entity.
     */
    fun partialUpdate(productUnitDTO: ProductUnitDTO): Optional<ProductUnitDTO> {
        log.debug("Request to partially update ProductUnit : {}", productUnitDTO)

        return productUnitRepository.findById(productUnitDTO.id)
            .map {
                productUnitMapper.partialUpdate(it, productUnitDTO)
                it
            }
            .map { productUnitRepository.save(it) }
            .map { productUnitMapper.toDto(it) }
    }

    /**
     * Get all the productUnits.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(): MutableList<ProductUnitDTO> {
        log.debug("Request to get all ProductUnits")
        return productUnitRepository.findAll()
            .mapTo(mutableListOf(), productUnitMapper::toDto)
    }

    /**
     * Get one productUnit by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<ProductUnitDTO> {
        log.debug("Request to get ProductUnit : $id")
        return productUnitRepository.findById(id)
            .map(productUnitMapper::toDto)
    }

    /**
     * Delete the productUnit by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete ProductUnit : $id")

        productUnitRepository.deleteById(id)
    }
}
