package me.rasztabiga.fridgy.products.web.rest

import me.rasztabiga.fridgy.products.repository.ProductUnitRepository
import me.rasztabiga.fridgy.products.service.ProductUnitService
import me.rasztabiga.fridgy.products.service.dto.ProductUnitDTO
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
import javax.validation.Valid
import javax.validation.constraints.NotNull

private const val ENTITY_NAME = "productsProductUnit"
/**
 * REST controller for managing [me.rasztabiga.fridgy.products.domain.ProductUnit].
 */
@RestController
@RequestMapping("/api")
class ProductUnitResource(
    private val productUnitService: ProductUnitService,
    private val productUnitRepository: ProductUnitRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val ENTITY_NAME = "productsProductUnit"
    }

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /product-units` : Create a new productUnit.
     *
     * @param productUnitDTO the productUnitDTO to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new productUnitDTO, or with status `400 (Bad Request)` if the productUnit has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/product-units")
    fun createProductUnit(@Valid @RequestBody productUnitDTO: ProductUnitDTO): ResponseEntity<ProductUnitDTO> {
        log.debug("REST request to save ProductUnit : $productUnitDTO")
        if (productUnitDTO.id != null) {
            throw BadRequestAlertException(
                "A new productUnit cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            )
        }
        val result = productUnitService.save(productUnitDTO)
        return ResponseEntity.created(URI("/api/product-units/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * {@code PUT  /product-units/:id} : Updates an existing productUnit.
     *
     * @param id the id of the productUnitDTO to save.
     * @param productUnitDTO the productUnitDTO to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated productUnitDTO,
     * or with status `400 (Bad Request)` if the productUnitDTO is not valid,
     * or with status `500 (Internal Server Error)` if the productUnitDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/product-units/{id}")
    fun updateProductUnit(
        @PathVariable(value = "id", required = false) id: Long,
        @Valid @RequestBody productUnitDTO: ProductUnitDTO
    ): ResponseEntity<ProductUnitDTO> {
        log.debug("REST request to update ProductUnit : {}, {}", id, productUnitDTO)
        if (productUnitDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }

        if (!Objects.equals(id, productUnitDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!productUnitRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = productUnitService.save(productUnitDTO)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    true,
                    ENTITY_NAME,
                    productUnitDTO.id.toString()
                )
            )
            .body(result)
    }

    /**
     * {@code PATCH  /product-units/:id} : Partial updates given fields of an existing productUnit, field will ignore if it is null
     *
     * @param id the id of the productUnitDTO to save.
     * @param productUnitDTO the productUnitDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productUnitDTO,
     * or with status {@code 400 (Bad Request)} if the productUnitDTO is not valid,
     * or with status {@code 404 (Not Found)} if the productUnitDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the productUnitDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = ["/product-units/{id}"], consumes = ["application/merge-patch+json"])
    @Throws(URISyntaxException::class)
    fun partialUpdateProductUnit(
        @PathVariable(value = "id", required = false) id: Long,
        @NotNull @RequestBody productUnitDTO: ProductUnitDTO
    ): ResponseEntity<ProductUnitDTO> {
        log.debug("REST request to partial update ProductUnit partially : {}, {}", id, productUnitDTO)
        if (productUnitDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        if (!Objects.equals(id, productUnitDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!productUnitRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = productUnitService.partialUpdate(productUnitDTO)

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productUnitDTO.id.toString())
        )
    }

    /**
     * `GET  /product-units` : get all the productUnits.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of productUnits in body.
     */
    @GetMapping("/product-units")
    fun getAllProductUnits(): MutableList<ProductUnitDTO> {
        log.debug("REST request to get all ProductUnits")

        return productUnitService.findAll()
    }

    /**
     * `GET  /product-units/:id` : get the "id" productUnit.
     *
     * @param id the id of the productUnitDTO to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the productUnitDTO, or with status `404 (Not Found)`.
     */
    @GetMapping("/product-units/{id}")
    fun getProductUnit(@PathVariable id: Long): ResponseEntity<ProductUnitDTO> {
        log.debug("REST request to get ProductUnit : $id")
        val productUnitDTO = productUnitService.findOne(id)
        return ResponseUtil.wrapOrNotFound(productUnitDTO)
    }
    /**
     *  `DELETE  /product-units/:id` : delete the "id" productUnit.
     *
     * @param id the id of the productUnitDTO to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/product-units/{id}")
    fun deleteProductUnit(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete ProductUnit : $id")

        productUnitService.delete(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
