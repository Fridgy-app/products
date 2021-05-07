package me.rasztabiga.fridgy.products.web.rest

import me.rasztabiga.fridgy.products.repository.RecipeIngredientRepository
import me.rasztabiga.fridgy.products.service.RecipeIngredientService
import me.rasztabiga.fridgy.products.service.dto.RecipeIngredientDTO
import me.rasztabiga.fridgy.products.web.rest.errors.BadRequestAlertException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import tech.jhipster.web.util.HeaderUtil
import tech.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import java.util.Objects

private const val ENTITY_NAME = "productsRecipeIngredient"
/**
 * REST controller for managing [me.rasztabiga.fridgy.products.domain.RecipeIngredient].
 */
@RestController
@RequestMapping("/api")
class RecipeIngredientResource(
    private val recipeIngredientService: RecipeIngredientService,
    private val recipeIngredientRepository: RecipeIngredientRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val ENTITY_NAME = "productsRecipeIngredient"
    }

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /recipe-ingredients` : Create a new recipeIngredient.
     *
     * @param recipeIngredientDTO the recipeIngredientDTO to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new recipeIngredientDTO, or with status `400 (Bad Request)` if the recipeIngredient has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/recipe-ingredients")
    fun createRecipeIngredient(@RequestBody recipeIngredientDTO: RecipeIngredientDTO): ResponseEntity<RecipeIngredientDTO> {
        log.debug("REST request to save RecipeIngredient : $recipeIngredientDTO")
        if (recipeIngredientDTO.id != null) {
            throw BadRequestAlertException(
                "A new recipeIngredient cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            )
        }
        val result = recipeIngredientService.save(recipeIngredientDTO)
        return ResponseEntity.created(URI("/api/recipe-ingredients/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * {@code PUT  /recipe-ingredients/:id} : Updates an existing recipeIngredient.
     *
     * @param id the id of the recipeIngredientDTO to save.
     * @param recipeIngredientDTO the recipeIngredientDTO to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated recipeIngredientDTO,
     * or with status `400 (Bad Request)` if the recipeIngredientDTO is not valid,
     * or with status `500 (Internal Server Error)` if the recipeIngredientDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/recipe-ingredients/{id}")
    fun updateRecipeIngredient(
        @PathVariable(value = "id", required = false) id: Long,
        @RequestBody recipeIngredientDTO: RecipeIngredientDTO
    ): ResponseEntity<RecipeIngredientDTO> {
        log.debug("REST request to update RecipeIngredient : {}, {}", id, recipeIngredientDTO)
        if (recipeIngredientDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }

        if (!Objects.equals(id, recipeIngredientDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!recipeIngredientRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = recipeIngredientService.save(recipeIngredientDTO)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    true,
                    ENTITY_NAME,
                    recipeIngredientDTO.id.toString()
                )
            )
            .body(result)
    }

    /**
     * {@code PATCH  /recipe-ingredients/:id} : Partial updates given fields of an existing recipeIngredient, field will ignore if it is null
     *
     * @param id the id of the recipeIngredientDTO to save.
     * @param recipeIngredientDTO the recipeIngredientDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated recipeIngredientDTO,
     * or with status {@code 400 (Bad Request)} if the recipeIngredientDTO is not valid,
     * or with status {@code 404 (Not Found)} if the recipeIngredientDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the recipeIngredientDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = ["/recipe-ingredients/{id}"], consumes = ["application/merge-patch+json"])
    @Throws(URISyntaxException::class)
    fun partialUpdateRecipeIngredient(
        @PathVariable(value = "id", required = false) id: Long,
        @RequestBody recipeIngredientDTO: RecipeIngredientDTO
    ): ResponseEntity<RecipeIngredientDTO> {
        log.debug("REST request to partial update RecipeIngredient partially : {}, {}", id, recipeIngredientDTO)
        if (recipeIngredientDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        if (!Objects.equals(id, recipeIngredientDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!recipeIngredientRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = recipeIngredientService.partialUpdate(recipeIngredientDTO)

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, recipeIngredientDTO.id.toString())
        )
    }

    /**
     * `GET  /recipe-ingredients` : get all the recipeIngredients.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of recipeIngredients in body.
     */
    @GetMapping("/recipe-ingredients")
    fun getAllRecipeIngredients(): MutableList<RecipeIngredientDTO> {
        log.debug("REST request to get all RecipeIngredients")

        return recipeIngredientService.findAll()
    }

    /**
     * `GET  /recipe-ingredients/:id` : get the "id" recipeIngredient.
     *
     * @param id the id of the recipeIngredientDTO to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the recipeIngredientDTO, or with status `404 (Not Found)`.
     */
    @GetMapping("/recipe-ingredients/{id}")
    fun getRecipeIngredient(@PathVariable id: Long): ResponseEntity<RecipeIngredientDTO> {
        log.debug("REST request to get RecipeIngredient : $id")
        val recipeIngredientDTO = recipeIngredientService.findOne(id)
        return ResponseUtil.wrapOrNotFound(recipeIngredientDTO)
    }
    /**
     *  `DELETE  /recipe-ingredients/:id` : delete the "id" recipeIngredient.
     *
     * @param id the id of the recipeIngredientDTO to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/recipe-ingredients/{id}")
    fun deleteRecipeIngredient(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete RecipeIngredient : $id")

        recipeIngredientService.delete(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
