package me.rasztabiga.fridgy.products.web.rest

import me.rasztabiga.fridgy.products.repository.GroceryItemRepository
import me.rasztabiga.fridgy.products.service.GroceryItemService
import me.rasztabiga.fridgy.products.service.dto.GroceryItemDTO
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

private const val ENTITY_NAME = "productsGroceryItem"
/**
 * REST controller for managing [me.rasztabiga.fridgy.products.domain.GroceryItem].
 */
@RestController
@RequestMapping("/api")
class GroceryItemResource(
    private val groceryItemService: GroceryItemService,
    private val groceryItemRepository: GroceryItemRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val ENTITY_NAME = "productsGroceryItem"
    }

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /grocery-items` : Create a new groceryItem.
     *
     * @param groceryItemDTO the groceryItemDTO to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new groceryItemDTO, or with status `400 (Bad Request)` if the groceryItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/grocery-items")
    fun createGroceryItem(@RequestBody groceryItemDTO: GroceryItemDTO): ResponseEntity<GroceryItemDTO> {
        log.debug("REST request to save GroceryItem : $groceryItemDTO")
        if (groceryItemDTO.id != null) {
            throw BadRequestAlertException(
                "A new groceryItem cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            )
        }
        val result = groceryItemService.save(groceryItemDTO)
        return ResponseEntity.created(URI("/api/grocery-items/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * {@code PUT  /grocery-items/:id} : Updates an existing groceryItem.
     *
     * @param id the id of the groceryItemDTO to save.
     * @param groceryItemDTO the groceryItemDTO to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated groceryItemDTO,
     * or with status `400 (Bad Request)` if the groceryItemDTO is not valid,
     * or with status `500 (Internal Server Error)` if the groceryItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/grocery-items/{id}")
    fun updateGroceryItem(
        @PathVariable(value = "id", required = false) id: Long,
        @RequestBody groceryItemDTO: GroceryItemDTO
    ): ResponseEntity<GroceryItemDTO> {
        log.debug("REST request to update GroceryItem : {}, {}", id, groceryItemDTO)
        if (groceryItemDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }

        if (!Objects.equals(id, groceryItemDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!groceryItemRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = groceryItemService.save(groceryItemDTO)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    true,
                    ENTITY_NAME,
                    groceryItemDTO.id.toString()
                )
            )
            .body(result)
    }

    /**
     * {@code PATCH  /grocery-items/:id} : Partial updates given fields of an existing groceryItem, field will ignore if it is null
     *
     * @param id the id of the groceryItemDTO to save.
     * @param groceryItemDTO the groceryItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated groceryItemDTO,
     * or with status {@code 400 (Bad Request)} if the groceryItemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the groceryItemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the groceryItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = ["/grocery-items/{id}"], consumes = ["application/merge-patch+json"])
    @Throws(URISyntaxException::class)
    fun partialUpdateGroceryItem(
        @PathVariable(value = "id", required = false) id: Long,
        @RequestBody groceryItemDTO: GroceryItemDTO
    ): ResponseEntity<GroceryItemDTO> {
        log.debug("REST request to partial update GroceryItem partially : {}, {}", id, groceryItemDTO)
        if (groceryItemDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        if (!Objects.equals(id, groceryItemDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!groceryItemRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = groceryItemService.partialUpdate(groceryItemDTO)

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, groceryItemDTO.id.toString())
        )
    }

    /**
     * `GET  /grocery-items` : get all the groceryItems.
     *
     * @param pageable the pagination information.

     * @return the [ResponseEntity] with status `200 (OK)` and the list of groceryItems in body.
     */
    @GetMapping("/grocery-items")
    fun getAllGroceryItems(pageable: Pageable): ResponseEntity<List<GroceryItemDTO>> {
        log.debug("REST request to get a page of GroceryItems")
        val page = groceryItemService.findAll(pageable)
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /grocery-items/:id` : get the "id" groceryItem.
     *
     * @param id the id of the groceryItemDTO to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the groceryItemDTO, or with status `404 (Not Found)`.
     */
    @GetMapping("/grocery-items/{id}")
    fun getGroceryItem(@PathVariable id: Long): ResponseEntity<GroceryItemDTO> {
        log.debug("REST request to get GroceryItem : $id")
        val groceryItemDTO = groceryItemService.findOne(id)
        return ResponseUtil.wrapOrNotFound(groceryItemDTO)
    }
    /**
     *  `DELETE  /grocery-items/:id` : delete the "id" groceryItem.
     *
     * @param id the id of the groceryItemDTO to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/grocery-items/{id}")
    fun deleteGroceryItem(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete GroceryItem : $id")

        groceryItemService.delete(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
