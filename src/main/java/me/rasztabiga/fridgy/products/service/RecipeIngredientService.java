package me.rasztabiga.fridgy.products.service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import me.rasztabiga.fridgy.products.domain.RecipeIngredient;
import me.rasztabiga.fridgy.products.repository.RecipeIngredientRepository;
import me.rasztabiga.fridgy.products.service.dto.RecipeIngredientDTO;
import me.rasztabiga.fridgy.products.service.mapper.RecipeIngredientMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link RecipeIngredient}.
 */
@Service
@Transactional
public class RecipeIngredientService {

    private final Logger log = LoggerFactory.getLogger(RecipeIngredientService.class);

    private final RecipeIngredientRepository recipeIngredientRepository;

    private final RecipeIngredientMapper recipeIngredientMapper;

    public RecipeIngredientService(RecipeIngredientRepository recipeIngredientRepository, RecipeIngredientMapper recipeIngredientMapper) {
        this.recipeIngredientRepository = recipeIngredientRepository;
        this.recipeIngredientMapper = recipeIngredientMapper;
    }

    /**
     * Save a recipeIngredient.
     *
     * @param recipeIngredientDTO the entity to save.
     * @return the persisted entity.
     */
    public RecipeIngredientDTO save(RecipeIngredientDTO recipeIngredientDTO) {
        log.debug("Request to save RecipeIngredient : {}", recipeIngredientDTO);
        RecipeIngredient recipeIngredient = recipeIngredientMapper.toEntity(recipeIngredientDTO);
        recipeIngredient = recipeIngredientRepository.save(recipeIngredient);
        return recipeIngredientMapper.toDto(recipeIngredient);
    }

    /**
     * Partially update a recipeIngredient.
     *
     * @param recipeIngredientDTO the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<RecipeIngredientDTO> partialUpdate(RecipeIngredientDTO recipeIngredientDTO) {
        log.debug("Request to partially update RecipeIngredient : {}", recipeIngredientDTO);

        return recipeIngredientRepository
            .findById(recipeIngredientDTO.getId())
            .map(
                existingRecipeIngredient -> {
                    recipeIngredientMapper.partialUpdate(existingRecipeIngredient, recipeIngredientDTO);
                    return existingRecipeIngredient;
                }
            )
            .map(recipeIngredientRepository::save)
            .map(recipeIngredientMapper::toDto);
    }

    /**
     * Get all the recipeIngredients.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<RecipeIngredientDTO> findAll() {
        log.debug("Request to get all RecipeIngredients");
        return recipeIngredientRepository
            .findAll()
            .stream()
            .map(recipeIngredientMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one recipeIngredient by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RecipeIngredientDTO> findOne(Long id) {
        log.debug("Request to get RecipeIngredient : {}", id);
        return recipeIngredientRepository.findById(id).map(recipeIngredientMapper::toDto);
    }

    /**
     * Delete the recipeIngredient by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete RecipeIngredient : {}", id);
        recipeIngredientRepository.deleteById(id);
    }
}
