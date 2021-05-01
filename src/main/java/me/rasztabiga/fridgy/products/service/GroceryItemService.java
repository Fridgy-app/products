package me.rasztabiga.fridgy.products.service;

import java.util.Optional;
import me.rasztabiga.fridgy.products.domain.GroceryItem;
import me.rasztabiga.fridgy.products.repository.GroceryItemRepository;
import me.rasztabiga.fridgy.products.service.dto.GroceryItemDTO;
import me.rasztabiga.fridgy.products.service.mapper.GroceryItemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link GroceryItem}.
 */
@Service
@Transactional
public class GroceryItemService {

    private final Logger log = LoggerFactory.getLogger(GroceryItemService.class);

    private final GroceryItemRepository groceryItemRepository;

    private final GroceryItemMapper groceryItemMapper;

    public GroceryItemService(GroceryItemRepository groceryItemRepository, GroceryItemMapper groceryItemMapper) {
        this.groceryItemRepository = groceryItemRepository;
        this.groceryItemMapper = groceryItemMapper;
    }

    /**
     * Save a groceryItem.
     *
     * @param groceryItemDTO the entity to save.
     * @return the persisted entity.
     */
    public GroceryItemDTO save(GroceryItemDTO groceryItemDTO) {
        log.debug("Request to save GroceryItem : {}", groceryItemDTO);
        GroceryItem groceryItem = groceryItemMapper.toEntity(groceryItemDTO);
        groceryItem = groceryItemRepository.save(groceryItem);
        return groceryItemMapper.toDto(groceryItem);
    }

    /**
     * Partially update a groceryItem.
     *
     * @param groceryItemDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<GroceryItemDTO> partialUpdate(GroceryItemDTO groceryItemDTO) {
        log.debug("Request to partially update GroceryItem : {}", groceryItemDTO);

        return groceryItemRepository
            .findById(groceryItemDTO.getId())
            .map(
                existingGroceryItem -> {
                    groceryItemMapper.partialUpdate(existingGroceryItem, groceryItemDTO);
                    return existingGroceryItem;
                }
            )
            .map(groceryItemRepository::save)
            .map(groceryItemMapper::toDto);
    }

    /**
     * Get all the groceryItems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<GroceryItemDTO> findAll(Pageable pageable) {
        log.debug("Request to get all GroceryItems");
        return groceryItemRepository.findAll(pageable).map(groceryItemMapper::toDto);
    }

    /**
     * Get one groceryItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<GroceryItemDTO> findOne(Long id) {
        log.debug("Request to get GroceryItem : {}", id);
        return groceryItemRepository.findById(id).map(groceryItemMapper::toDto);
    }

    /**
     * Delete the groceryItem by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete GroceryItem : {}", id);
        groceryItemRepository.deleteById(id);
    }
}
