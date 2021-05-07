package me.rasztabiga.fridgy.products.service

import me.rasztabiga.fridgy.products.domain.GroceryItem
import me.rasztabiga.fridgy.products.repository.GroceryItemRepository
import me.rasztabiga.fridgy.products.service.dto.GroceryItemDTO
import me.rasztabiga.fridgy.products.service.mapper.GroceryItemMapper
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

/**
 * Service Implementation for managing [GroceryItem].
 */
@Service
@Transactional
class GroceryItemService(
    private val groceryItemRepository: GroceryItemRepository,
    private val groceryItemMapper: GroceryItemMapper
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a groceryItem.
     *
     * @param groceryItemDTO the entity to save.
     * @return the persisted entity.
     */
    fun save(groceryItemDTO: GroceryItemDTO): GroceryItemDTO {
        log.debug("Request to save GroceryItem : $groceryItemDTO")

        var groceryItem = groceryItemMapper.toEntity(groceryItemDTO)
        groceryItem = groceryItemRepository.save(groceryItem)
        return groceryItemMapper.toDto(groceryItem)
    }

    /**
     * Partially updates a groceryItem.
     *
     * @param groceryItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    fun partialUpdate(groceryItemDTO: GroceryItemDTO): Optional<GroceryItemDTO> {
        log.debug("Request to partially update GroceryItem : {}", groceryItemDTO)

        return groceryItemRepository.findById(groceryItemDTO.id)
            .map {
                groceryItemMapper.partialUpdate(it, groceryItemDTO)
                it
            }
            .map { groceryItemRepository.save(it) }
            .map { groceryItemMapper.toDto(it) }
    }

    /**
     * Get all the groceryItems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<GroceryItemDTO> {
        log.debug("Request to get all GroceryItems")
        return groceryItemRepository.findAll(pageable)
            .map(groceryItemMapper::toDto)
    }

    /**
     * Get one groceryItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<GroceryItemDTO> {
        log.debug("Request to get GroceryItem : $id")
        return groceryItemRepository.findById(id)
            .map(groceryItemMapper::toDto)
    }

    /**
     * Delete the groceryItem by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete GroceryItem : $id")

        groceryItemRepository.deleteById(id)
    }
}
