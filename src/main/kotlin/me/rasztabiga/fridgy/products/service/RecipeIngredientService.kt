package me.rasztabiga.fridgy.products.service

import me.rasztabiga.fridgy.products.domain.RecipeIngredient
import me.rasztabiga.fridgy.products.repository.RecipeIngredientRepository
import me.rasztabiga.fridgy.products.service.dto.RecipeIngredientDTO
import me.rasztabiga.fridgy.products.service.mapper.RecipeIngredientMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.Optional

/**
 * Service Implementation for managing [RecipeIngredient].
 */
@Service
@Transactional
class RecipeIngredientService(
    private val recipeIngredientRepository: RecipeIngredientRepository,
    private val recipeIngredientMapper: RecipeIngredientMapper
) {

    private val log = LoggerFactory.getLogger(javaClass)

    /**
     * Save a recipeIngredient.
     *
     * @param recipeIngredientDTO the entity to save.
     * @return the persisted entity.
     */
    fun save(recipeIngredientDTO: RecipeIngredientDTO): RecipeIngredientDTO {
        log.debug("Request to save RecipeIngredient : $recipeIngredientDTO")

        var recipeIngredient = recipeIngredientMapper.toEntity(recipeIngredientDTO)
        recipeIngredient = recipeIngredientRepository.save(recipeIngredient)
        return recipeIngredientMapper.toDto(recipeIngredient)
    }

    /**
     * Partially updates a recipeIngredient.
     *
     * @param recipeIngredientDTO the entity to update partially.
     * @return the persisted entity.
     */
    fun partialUpdate(recipeIngredientDTO: RecipeIngredientDTO): Optional<RecipeIngredientDTO> {
        log.debug("Request to partially update RecipeIngredient : {}", recipeIngredientDTO)

        return recipeIngredientRepository.findById(recipeIngredientDTO.id)
            .map {
                recipeIngredientMapper.partialUpdate(it, recipeIngredientDTO)
                it
            }
            .map { recipeIngredientRepository.save(it) }
            .map { recipeIngredientMapper.toDto(it) }
    }

    /**
     * Get all the recipeIngredients.
     *
     * @return the list of entities.
     */
    @Transactional(readOnly = true)
    fun findAll(): MutableList<RecipeIngredientDTO> {
        log.debug("Request to get all RecipeIngredients")
        return recipeIngredientRepository.findAll()
            .mapTo(mutableListOf(), recipeIngredientMapper::toDto)
    }

    /**
     * Get one recipeIngredient by id.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    @Transactional(readOnly = true)
    fun findOne(id: Long): Optional<RecipeIngredientDTO> {
        log.debug("Request to get RecipeIngredient : $id")
        return recipeIngredientRepository.findById(id)
            .map(recipeIngredientMapper::toDto)
    }

    /**
     * Delete the recipeIngredient by id.
     *
     * @param id the id of the entity.
     */
    fun delete(id: Long) {
        log.debug("Request to delete RecipeIngredient : $id")

        recipeIngredientRepository.deleteById(id)
    }
}
