package me.rasztabiga.fridgy.products.service;

import java.util.Optional;
import me.rasztabiga.fridgy.products.domain.GroceryItem;
import me.rasztabiga.fridgy.products.repository.GroceryItemRepository;
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

    public GroceryItemService(GroceryItemRepository groceryItemRepository) {
        this.groceryItemRepository = groceryItemRepository;
    }

    /**
     * Save a groceryItem.
     *
     * @param groceryItem the entity to save.
     * @return the persisted entity.
     */
    public GroceryItem save(GroceryItem groceryItem) {
        log.debug("Request to save GroceryItem : {}", groceryItem);
        return groceryItemRepository.save(groceryItem);
    }

    /**
     * Partially update a groceryItem.
     *
     * @param groceryItem the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<GroceryItem> partialUpdate(GroceryItem groceryItem) {
        log.debug("Request to partially update GroceryItem : {}", groceryItem);

        return groceryItemRepository
            .findById(groceryItem.getId())
            .map(
                existingGroceryItem -> {
                    if (groceryItem.getQuantity() != null) {
                        existingGroceryItem.setQuantity(groceryItem.getQuantity());
                    }
                    if (groceryItem.getDescription() != null) {
                        existingGroceryItem.setDescription(groceryItem.getDescription());
                    }

                    return existingGroceryItem;
                }
            )
            .map(groceryItemRepository::save);
    }

    /**
     * Get all the groceryItems.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<GroceryItem> findAll(Pageable pageable) {
        log.debug("Request to get all GroceryItems");
        return groceryItemRepository.findAll(pageable);
    }

    /**
     * Get one groceryItem by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<GroceryItem> findOne(Long id) {
        log.debug("Request to get GroceryItem : {}", id);
        return groceryItemRepository.findById(id);
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
