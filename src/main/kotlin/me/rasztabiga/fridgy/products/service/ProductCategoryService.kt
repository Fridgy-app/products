package me.rasztabiga.fridgy.products.service

import me.rasztabiga.fridgy.products.domain.ProductCategory
import me.rasztabiga.fridgy.products.repository.ProductCategoryRepository
import me.rasztabiga.fridgy.products.service.dto.ProductCategoryDTO
import me.rasztabiga.fridgy.products.service.mapper.ProductCategoryMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

/**
 * Service Implementation for managing [ProductCategory].
 */
@Service
@Transactional
class ProductCategoryService(
    private val productCategoryRepository: ProductCategoryRepository,
    private val productCategoryMapper: ProductCategoryMapper
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a productCategory.
     *
     * @param productCategoryDTO the entity to save.
     * @return the persisted entity.
     */
    fun save(productCategoryDTO: ProductCategoryDTO): ProductCategoryDTO {
        log.debug("Request to save ProductCategory : $productCategoryDTO")

        var productCategory = productCategoryMapper.toEntity(productCategoryDTO)
        productCategory = productCategoryRepository.save(productCategory)
        return productCategoryMapper.toDto(productCategory)
    }

    /**
     * Partially updates a productCategory.
     *
     * @param productCategoryDTO the entity to update partially.
     * @return the persisted entity.
     */
    fun partialUpdate(productCategoryDTO: ProductCategoryDTO): Optional<ProductCategoryDTO> {
        log.debug("Request to partially update ProductCategory : {}", productCategoryDTO)

        return productCategoryRepository.findById(productCategoryDTO.id)
            .map {
                productCategoryMapper.partialUpdate(it, productCategoryDTO)
                it
            }
            .map { productCategoryRepository.save(it) }
            .map { productCategoryMapper.toDto(it) }
    }

    /**
     * Get all the productCategories.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(): MutableList<ProductCategoryDTO> {
        log.debug("Request to get all ProductCategories")
        return productCategoryRepository.findAll()
            .mapTo(mutableListOf(), productCategoryMapper::toDto)
    }

    /**
     * Get one productCategory by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<ProductCategoryDTO> {
        log.debug("Request to get ProductCategory : $id")
        return productCategoryRepository.findById(id)
            .map(productCategoryMapper::toDto)
    }

    /**
     * Delete the productCategory by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete ProductCategory : $id")

        productCategoryRepository.deleteById(id)
    }
}
