package me.rasztabiga.fridgy.products.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import me.rasztabiga.fridgy.products.domain.ProductUnit;
import me.rasztabiga.fridgy.products.repository.ProductUnitRepository;
import me.rasztabiga.fridgy.products.service.dto.ProductUnitDTO;
import me.rasztabiga.fridgy.products.service.mapper.ProductUnitMapper;
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

    private final ProductUnitMapper productUnitMapper;

    public ProductUnitService(ProductUnitRepository productUnitRepository, ProductUnitMapper productUnitMapper) {
        this.productUnitRepository = productUnitRepository;
        this.productUnitMapper = productUnitMapper;
    }

    /**
     * Save a productUnit.
     *
     * @param productUnitDTO the entity to save.
     * @return the persisted entity.
     */
    public ProductUnitDTO save(ProductUnitDTO productUnitDTO) {
        log.debug("Request to save ProductUnit : {}", productUnitDTO);
        ProductUnit productUnit = productUnitMapper.toEntity(productUnitDTO);
        productUnit = productUnitRepository.save(productUnit);
        return productUnitMapper.toDto(productUnit);
    }

    /**
     * Partially update a productUnit.
     *
     * @param productUnitDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<ProductUnitDTO> partialUpdate(ProductUnitDTO productUnitDTO) {
        log.debug("Request to partially update ProductUnit : {}", productUnitDTO);

        return productUnitRepository
            .findById(productUnitDTO.getId())
            .map(
                existingProductUnit -> {
                    productUnitMapper.partialUpdate(existingProductUnit, productUnitDTO);
                    return existingProductUnit;
                }
            )
            .map(productUnitRepository::save)
            .map(productUnitMapper::toDto);
    }

    /**
     * Get all the productUnits.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<ProductUnitDTO> findAll() {
        log.debug("Request to get all ProductUnits");
        return productUnitRepository.findAll().stream().map(productUnitMapper::toDto).collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one productUnit by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<ProductUnitDTO> findOne(Long id) {
        log.debug("Request to get ProductUnit : {}", id);
        return productUnitRepository.findById(id).map(productUnitMapper::toDto);
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
