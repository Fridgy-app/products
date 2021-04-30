package me.rasztabiga.fridgy.products.service;

import java.util.List;
import java.util.Optional;
import me.rasztabiga.fridgy.products.domain.RecipeIngredient;
import me.rasztabiga.fridgy.products.repository.RecipeIngredientRepository;
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

    public RecipeIngredientService(RecipeIngredientRepository recipeIngredientRepository) {
        this.recipeIngredientRepository = recipeIngredientRepository;
    }

    /**
     * Save a recipeIngredient.
     *
     * @param recipeIngredient the entity to save.
     * @return the persisted entity.
     */
    public RecipeIngredient save(RecipeIngredient recipeIngredient) {
        log.debug("Request to save RecipeIngredient : {}", recipeIngredient);
        return recipeIngredientRepository.save(recipeIngredient);
    }

    /**
     * Partially update a recipeIngredient.
     *
     * @param recipeIngredient the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<RecipeIngredient> partialUpdate(RecipeIngredient recipeIngredient) {
        log.debug("Request to partially update RecipeIngredient : {}", recipeIngredient);

        return recipeIngredientRepository
            .findById(recipeIngredient.getId())
            .map(
                existingRecipeIngredient -> {
                    if (recipeIngredient.getQuantity() != null) {
                        existingRecipeIngredient.setQuantity(recipeIngredient.getQuantity());
                    }

                    return existingRecipeIngredient;
                }
            )
            .map(recipeIngredientRepository::save);
    }

    /**
     * Get all the recipeIngredients.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public List<RecipeIngredient> findAll() {
        log.debug("Request to get all RecipeIngredients");
        return recipeIngredientRepository.findAll();
    }

    /**
     * Get one recipeIngredient by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<RecipeIngredient> findOne(Long id) {
        log.debug("Request to get RecipeIngredient : {}", id);
        return recipeIngredientRepository.findById(id);
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
