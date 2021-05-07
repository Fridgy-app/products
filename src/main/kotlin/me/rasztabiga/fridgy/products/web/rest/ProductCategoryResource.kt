package me.rasztabiga.fridgy.products.web.rest

import me.rasztabiga.fridgy.products.repository.ProductCategoryRepository
import me.rasztabiga.fridgy.products.service.ProductCategoryService
import me.rasztabiga.fridgy.products.service.dto.ProductCategoryDTO
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

private const val ENTITY_NAME = "productsProductCategory"
/**
 * REST controller for managing [me.rasztabiga.fridgy.products.domain.ProductCategory].
 */
@RestController
@RequestMapping("/api")
class ProductCategoryResource(
    private val productCategoryService: ProductCategoryService,
    private val productCategoryRepository: ProductCategoryRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val ENTITY_NAME = "productsProductCategory"
    }

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /product-categories` : Create a new productCategory.
     *
     * @param productCategoryDTO the productCategoryDTO to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new productCategoryDTO, or with status `400 (Bad Request)` if the productCategory has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/product-categories")
    fun createProductCategory(@Valid @RequestBody productCategoryDTO: ProductCategoryDTO): ResponseEntity<ProductCategoryDTO> {
        log.debug("REST request to save ProductCategory : $productCategoryDTO")
        if (productCategoryDTO.id != null) {
            throw BadRequestAlertException(
                "A new productCategory cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            )
        }
        val result = productCategoryService.save(productCategoryDTO)
        return ResponseEntity.created(URI("/api/product-categories/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * {@code PUT  /product-categories/:id} : Updates an existing productCategory.
     *
     * @param id the id of the productCategoryDTO to save.
     * @param productCategoryDTO the productCategoryDTO to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated productCategoryDTO,
     * or with status `400 (Bad Request)` if the productCategoryDTO is not valid,
     * or with status `500 (Internal Server Error)` if the productCategoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/product-categories/{id}")
    fun updateProductCategory(
        @PathVariable(value = "id", required = false) id: Long,
        @Valid @RequestBody productCategoryDTO: ProductCategoryDTO
    ): ResponseEntity<ProductCategoryDTO> {
        log.debug("REST request to update ProductCategory : {}, {}", id, productCategoryDTO)
        if (productCategoryDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }

        if (!Objects.equals(id, productCategoryDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!productCategoryRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = productCategoryService.save(productCategoryDTO)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    true,
                    ENTITY_NAME,
                    productCategoryDTO.id.toString()
                )
            )
            .body(result)
    }

    /**
     * {@code PATCH  /product-categories/:id} : Partial updates given fields of an existing productCategory, field will ignore if it is null
     *
     * @param id the id of the productCategoryDTO to save.
     * @param productCategoryDTO the productCategoryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productCategoryDTO,
     * or with status {@code 400 (Bad Request)} if the productCategoryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the productCategoryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the productCategoryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = ["/product-categories/{id}"], consumes = ["application/merge-patch+json"])
    @Throws(URISyntaxException::class)
    fun partialUpdateProductCategory(
        @PathVariable(value = "id", required = false) id: Long,
        @NotNull @RequestBody productCategoryDTO: ProductCategoryDTO
    ): ResponseEntity<ProductCategoryDTO> {
        log.debug("REST request to partial update ProductCategory partially : {}, {}", id, productCategoryDTO)
        if (productCategoryDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        if (!Objects.equals(id, productCategoryDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!productCategoryRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = productCategoryService.partialUpdate(productCategoryDTO)

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productCategoryDTO.id.toString())
        )
    }

    /**
     * `GET  /product-categories` : get all the productCategories.
     *

     * @return the [ResponseEntity] with status `200 (OK)` and the list of productCategories in body.
     */
    @GetMapping("/product-categories")
    fun getAllProductCategories(): MutableList<ProductCategoryDTO> {
        log.debug("REST request to get all ProductCategories")

        return productCategoryService.findAll()
    }

    /**
     * `GET  /product-categories/:id` : get the "id" productCategory.
     *
     * @param id the id of the productCategoryDTO to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the productCategoryDTO, or with status `404 (Not Found)`.
     */
    @GetMapping("/product-categories/{id}")
    fun getProductCategory(@PathVariable id: Long): ResponseEntity<ProductCategoryDTO> {
        log.debug("REST request to get ProductCategory : $id")
        val productCategoryDTO = productCategoryService.findOne(id)
        return ResponseUtil.wrapOrNotFound(productCategoryDTO)
    }
    /**
     *  `DELETE  /product-categories/:id` : delete the "id" productCategory.
     *
     * @param id the id of the productCategoryDTO to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/product-categories/{id}")
    fun deleteProductCategory(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete ProductCategory : $id")

        productCategoryService.delete(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
