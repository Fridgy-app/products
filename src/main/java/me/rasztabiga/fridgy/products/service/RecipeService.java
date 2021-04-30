package me.rasztabiga.fridgy.products.service;

import java.util.Optional;
import me.rasztabiga.fridgy.products.domain.Recipe;
import me.rasztabiga.fridgy.products.repository.RecipeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link Recipe}.
 */
@Service
@Transactional
public class RecipeService {

    private final Logger log = LoggerFactory.getLogger(RecipeService.class);

    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    /**
     * Save a recipe.
     *
     * @param recipe the entity to save.
     * @return the persisted entity.
     */
    public Recipe save(Recipe recipe) {
        log.debug("Request to save Recipe : {}", recipe);
        return recipeRepository.save(recipe);
    }

    /**
     * Partially update a recipe.
     *
     * @param recipe the entity to update partially.
     * @return the persisted entity.
     */
    public Optional<Recipe> partialUpdate(Recipe recipe) {
        log.debug("Request to partially update Recipe : {}", recipe);

        return recipeRepository
            .findById(recipe.getId())
            .map(
                existingRecipe -> {
                    if (recipe.getName() != null) {
                        existingRecipe.setName(recipe.getName());
                    }
                    if (recipe.getInstructionsBody() != null) {
                        existingRecipe.setInstructionsBody(recipe.getInstructionsBody());
                    }

                    return existingRecipe;
                }
            )
            .map(recipeRepository::save);
    }

    /**
     * Get all the recipes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    public Page<Recipe> findAll(Pageable pageable) {
        log.debug("Request to get all Recipes");
        return recipeRepository.findAll(pageable);
    }

    /**
     * Get one recipe by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    public Optional<Recipe> findOne(Long id) {
        log.debug("Request to get Recipe : {}", id);
        return recipeRepository.findById(id);
    }

    /**
     * Delete the recipe by id.
     *
     * @param id the id of the entity.
     */
    public void delete(Long id) {
        log.debug("Request to delete Recipe : {}", id);
        recipeRepository.deleteById(id);
    }
}
