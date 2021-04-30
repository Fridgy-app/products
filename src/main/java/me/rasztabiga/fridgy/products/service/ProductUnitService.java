package me.rasztabiga.fridgy.products.service;

import java.util.List;
import java.util.Optional;
import me.rasztabiga.fridgy.products.domain.ProductUnit;
import me.rasztabiga.fridgy.products.repository.ProductUnitRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link ProductUnit}.
 */
@Service
@Transactional
public class ProductUnitService {

    private final Logger log = LoggerFactory.getLogger(ProductUnitService.class);

    private final ProductUnitRepository productUnitRepository;

    public ProductUnitService(ProductUnitRepository productUnitRepository) {
        this.productUnitRepository = productUnitRepository;
    }

    /**
     * Save a productUnit.
     *
     * @param productUnit the entity to save.
     * @return the persisted entity.
     */
    public ProductUnit save(ProductUnit productUnit) {
        log.debug("Request to save ProductUnit : {}", productUnit);
        return productUnitRepository.save(productUnit);
    }

    /**
     * Partially update a productUnit.
     *
     * @param productUnit the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ProductUnit> partialUpdate(ProductUnit productUnit) {
        log.debug("Request to partially update ProductUnit : {}", productUnit);

        return productUnitRepository
            .findById(productUnit.getId())
            .map(
                existingProductUnit -> {
                    if (productUnit.getName() != null) {
                        existingProductUnit.setName(productUnit.getName());
                    }

                    return existingProductUnit;
                }
            )
            .map(productUnitRepository::save);
    }

    /**
     * Get all the productUnits.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ProductUnit> findAll() {
        log.debug("Request to get all ProductUnits");
        return productUnitRepository.findAll();
    }

    /**
     * Get one productUnit by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ProductUnit> findOne(Long id) {
        log.debug("Request to get ProductUnit : {}", id);
        return productUnitRepository.findById(id);
    }

    /**
     * Delete the productUnit by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete ProductUnit : {}", id);
        productUnitRepository.deleteById(id);
    }
}
