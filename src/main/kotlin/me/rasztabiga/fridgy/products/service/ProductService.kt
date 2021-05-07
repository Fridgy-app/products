package me.rasztabiga.fridgy.products.service

import me.rasztabiga.fridgy.products.domain.Product
import me.rasztabiga.fridgy.products.repository.ProductRepository
import me.rasztabiga.fridgy.products.service.dto.ProductDTO
import me.rasztabiga.fridgy.products.service.mapper.ProductMapper
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

/**
 * Service Implementation for managing [Product].
 */
@Service
@Transactional
class ProductService(
    private val productRepository: ProductRepository,
    private val productMapper: ProductMapper
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a product.
     *
     * @param productDTO the entity to save.
     * @return the persisted entity.
     */
    fun save(productDTO: ProductDTO): ProductDTO {
        log.debug("Request to save Product : $productDTO")

        var product = productMapper.toEntity(productDTO)
        product = productRepository.save(product)
        return productMapper.toDto(product)
    }

    /**
     * Partially updates a product.
     *
     * @param productDTO the entity to update partially.
     * @return the persisted entity.
     */
    fun partialUpdate(productDTO: ProductDTO): Optional<ProductDTO> {
        log.debug("Request to partially update Product : {}", productDTO)

        return productRepository.findById(productDTO.id)
            .map {
                productMapper.partialUpdate(it, productDTO)
                it
            }
            .map { productRepository.save(it) }
            .map { productMapper.toDto(it) }
    }

    /**
     * Get all the products.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<ProductDTO> {
        log.debug("Request to get all Products")
        return productRepository.findAll(pageable)
            .map(productMapper::toDto)
    }

    /**
     * Get all the products with eager load of many-to-many relationships.
     *
     * @return the list of entities.
     */
    fun findAllWithEagerRelationships(pageable: Pageable) =
        productRepository.findAllWithEagerRelationships(pageable).map(productMapper::toDto)

    /**
     * Get one product by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<ProductDTO> {
        log.debug("Request to get Product : $id")
        return productRepository.findOneWithEagerRelationships(id)
            .map(productMapper::toDto)
    }

    /**
     * Delete the product by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete Product : $id")

        productRepository.deleteById(id)
    }
}
