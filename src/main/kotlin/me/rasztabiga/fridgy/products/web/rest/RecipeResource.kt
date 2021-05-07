package me.rasztabiga.fridgy.products.web.rest

import me.rasztabiga.fridgy.products.repository.RecipeRepository
import me.rasztabiga.fridgy.products.service.RecipeService
import me.rasztabiga.fridgy.products.service.dto.RecipeDTO
import me.rasztabiga.fridgy.products.web.rest.errors.BadRequestAlertException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import tech.jhipster.web.util.HeaderUtil
import tech.jhipster.web.util.PaginationUtil
import tech.jhipster.web.util.ResponseUtil
import java.net.URI
import java.net.URISyntaxException
import java.util.Objects
import javax.validation.Valid
import javax.validation.constraints.NotNull

private const val ENTITY_NAME = "productsRecipe"
/**
 * REST controller for managing [me.rasztabiga.fridgy.products.domain.Recipe].
 */
@RestController
@RequestMapping("/api")
class RecipeResource(
    private val recipeService: RecipeService,
    private val recipeRepository: RecipeRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val ENTITY_NAME = "productsRecipe"
    }

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /recipes` : Create a new recipe.
     *
     * @param recipeDTO the recipeDTO to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new recipeDTO, or with status `400 (Bad Request)` if the recipe has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/recipes")
    fun createRecipe(@Valid @RequestBody recipeDTO: RecipeDTO): ResponseEntity<RecipeDTO> {
        log.debug("REST request to save Recipe : $recipeDTO")
        if (recipeDTO.id != null) {
            throw BadRequestAlertException(
                "A new recipe cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            )
        }
        val result = recipeService.save(recipeDTO)
        return ResponseEntity.created(URI("/api/recipes/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * {@code PUT  /recipes/:id} : Updates an existing recipe.
     *
     * @param id the id of the recipeDTO to save.
     * @param recipeDTO the recipeDTO to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated recipeDTO,
     * or with status `400 (Bad Request)` if the recipeDTO is not valid,
     * or with status `500 (Internal Server Error)` if the recipeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/recipes/{id}")
    fun updateRecipe(
        @PathVariable(value = "id", required = false) id: Long,
        @Valid @RequestBody recipeDTO: RecipeDTO
    ): ResponseEntity<RecipeDTO> {
        log.debug("REST request to update Recipe : {}, {}", id, recipeDTO)
        if (recipeDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }

        if (!Objects.equals(id, recipeDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!recipeRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = recipeService.save(recipeDTO)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    true,
                    ENTITY_NAME,
                    recipeDTO.id.toString()
                )
            )
            .body(result)
    }

    /**
     * {@code PATCH  /recipes/:id} : Partial updates given fields of an existing recipe, field will ignore if it is null
     *
     * @param id the id of the recipeDTO to save.
     * @param recipeDTO the recipeDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated recipeDTO,
     * or with status {@code 400 (Bad Request)} if the recipeDTO is not valid,
     * or with status {@code 404 (Not Found)} if the recipeDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the recipeDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = ["/recipes/{id}"], consumes = ["application/merge-patch+json"])
    @Throws(URISyntaxException::class)
    fun partialUpdateRecipe(
        @PathVariable(value = "id", required = false) id: Long,
        @NotNull @RequestBody recipeDTO: RecipeDTO
    ): ResponseEntity<RecipeDTO> {
        log.debug("REST request to partial update Recipe partially : {}, {}", id, recipeDTO)
        if (recipeDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        if (!Objects.equals(id, recipeDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!recipeRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = recipeService.partialUpdate(recipeDTO)

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, recipeDTO.id.toString())
        )
    }

    /**
     * `GET  /recipes` : get all the recipes.
     *
     * @param pageable the pagination information.

     * @return the [ResponseEntity] with status `200 (OK)` and the list of recipes in body.
     */
    @GetMapping("/recipes")
    fun getAllRecipes(pageable: Pageable): ResponseEntity<List<RecipeDTO>> {
        log.debug("REST request to get a page of Recipes")
        val page = recipeService.findAll(pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /recipes/:id` : get the "id" recipe.
     *
     * @param id the id of the recipeDTO to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the recipeDTO, or with status `404 (Not Found)`.
     */
    @GetMapping("/recipes/{id}")
    fun getRecipe(@PathVariable id: Long): ResponseEntity<RecipeDTO> {
        log.debug("REST request to get Recipe : $id")
        val recipeDTO = recipeService.findOne(id)
        return ResponseUtil.wrapOrNotFound(recipeDTO)
    }
    /**
     *  `DELETE  /recipes/:id` : delete the "id" recipe.
     *
     * @param id the id of the recipeDTO to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/recipes/{id}")
    fun deleteRecipe(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Recipe : $id")

        recipeService.delete(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
