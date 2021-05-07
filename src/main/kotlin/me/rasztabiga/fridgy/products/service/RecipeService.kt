package me.rasztabiga.fridgy.products.service

import me.rasztabiga.fridgy.products.domain.Recipe
import me.rasztabiga.fridgy.products.repository.RecipeRepository
import me.rasztabiga.fridgy.products.service.dto.RecipeDTO
import me.rasztabiga.fridgy.products.service.mapper.RecipeMapper
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

/**
 * Service Implementation for managing [Recipe].
 */
@Service
@Transactional
class RecipeService(
    private val recipeRepository: RecipeRepository,
    private val recipeMapper: RecipeMapper
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a recipe.
     *
     * @param recipeDTO the entity to save.
     * @return the persisted entity.
     */
    fun save(recipeDTO: RecipeDTO): RecipeDTO {
        log.debug("Request to save Recipe : $recipeDTO")

        var recipe = recipeMapper.toEntity(recipeDTO)
        recipe = recipeRepository.save(recipe)
        return recipeMapper.toDto(recipe)
    }

    /**
     * Partially updates a recipe.
     *
     * @param recipeDTO the entity to update partially.
     * @return the persisted entity.
     */
    fun partialUpdate(recipeDTO: RecipeDTO): Optional<RecipeDTO> {
        log.debug("Request to partially update Recipe : {}", recipeDTO)

        return recipeRepository.findById(recipeDTO.id)
            .map {
                recipeMapper.partialUpdate(it, recipeDTO)
                it
            }
            .map { recipeRepository.save(it) }
            .map { recipeMapper.toDto(it) }
    }

    /**
     * Get all the recipes.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(pageable: Pageable): Page<RecipeDTO> {
        log.debug("Request to get all Recipes")
        return recipeRepository.findAll(pageable)
            .map(recipeMapper::toDto)
    }

    /**
     * Get one recipe by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<RecipeDTO> {
        log.debug("Request to get Recipe : $id")
        return recipeRepository.findById(id)
            .map(recipeMapper::toDto)
    }

    /**
     * Delete the recipe by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete Recipe : $id")

        recipeRepository.deleteById(id)
    }
}
