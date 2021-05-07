package me.rasztabiga.fridgy.products.web.rest

import me.rasztabiga.fridgy.products.repository.ProductRepository
import me.rasztabiga.fridgy.products.service.ProductService
import me.rasztabiga.fridgy.products.service.dto.ProductDTO
import me.rasztabiga.fridgy.products.web.rest.errors.BadRequestAlertException
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
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

private const val ENTITY_NAME = "productsProduct"
/**
 * REST controller for managing [me.rasztabiga.fridgy.products.domain.Product].
 */
@RestController
@RequestMapping("/api")
class ProductResource(
    private val productService: ProductService,
    private val productRepository: ProductRepository
) {

    private val log = LoggerFactory.getLogger(javaClass)

    companion object {
        const val ENTITY_NAME = "productsProduct"
    }

    @Value("\${jhipster.clientApp.name}")
    private var applicationName: String? = null

    /**
     * `POST  /products` : Create a new product.
     *
     * @param productDTO the productDTO to create.
     * @return the [ResponseEntity] with status `201 (Created)` and with body the new productDTO, or with status `400 (Bad Request)` if the product has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/products")
    fun createProduct(@Valid @RequestBody productDTO: ProductDTO): ResponseEntity<ProductDTO> {
        log.debug("REST request to save Product : $productDTO")
        if (productDTO.id != null) {
            throw BadRequestAlertException(
                "A new product cannot already have an ID",
                ENTITY_NAME,
                "idexists"
            )
        }
        val result = productService.save(productDTO)
        return ResponseEntity.created(URI("/api/products/${result.id}"))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.id.toString()))
            .body(result)
    }

    /**
     * {@code PUT  /products/:id} : Updates an existing product.
     *
     * @param id the id of the productDTO to save.
     * @param productDTO the productDTO to update.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the updated productDTO,
     * or with status `400 (Bad Request)` if the productDTO is not valid,
     * or with status `500 (Internal Server Error)` if the productDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/products/{id}")
    fun updateProduct(
        @PathVariable(value = "id", required = false) id: Long,
        @Valid @RequestBody productDTO: ProductDTO
    ): ResponseEntity<ProductDTO> {
        log.debug("REST request to update Product : {}, {}", id, productDTO)
        if (productDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }

        if (!Objects.equals(id, productDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!productRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = productService.save(productDTO)
        return ResponseEntity.ok()
            .headers(
                HeaderUtil.createEntityUpdateAlert(
                    applicationName,
                    true,
                    ENTITY_NAME,
                    productDTO.id.toString()
                )
            )
            .body(result)
    }

    /**
     * {@code PATCH  /products/:id} : Partial updates given fields of an existing product, field will ignore if it is null
     *
     * @param id the id of the productDTO to save.
     * @param productDTO the productDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated productDTO,
     * or with status {@code 400 (Bad Request)} if the productDTO is not valid,
     * or with status {@code 404 (Not Found)} if the productDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the productDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = ["/products/{id}"], consumes = ["application/merge-patch+json"])
    @Throws(URISyntaxException::class)
    fun partialUpdateProduct(
        @PathVariable(value = "id", required = false) id: Long,
        @NotNull @RequestBody productDTO: ProductDTO
    ): ResponseEntity<ProductDTO> {
        log.debug("REST request to partial update Product partially : {}, {}", id, productDTO)
        if (productDTO.id == null) {
            throw BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull")
        }
        if (!Objects.equals(id, productDTO.id)) {
            throw BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid")
        }

        if (!productRepository.existsById(id)) {
            throw BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound")
        }

        val result = productService.partialUpdate(productDTO)

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, productDTO.id.toString())
        )
    }

    /**
     * `GET  /products` : get all the products.
     *
     * @param pageable the pagination information.
     * @param eagerload flag to eager load entities from relationships (This is applicable for many-to-many).
     * @return the [ResponseEntity] with status `200 (OK)` and the list of products in body.
     */
    @GetMapping("/products")
    fun getAllProducts(pageable: Pageable, @RequestParam(required = false, defaultValue = "false") eagerload: Boolean): ResponseEntity<List<ProductDTO>> {
        log.debug("REST request to get a page of Products")
        val page: Page<ProductDTO> = if (eagerload) {
            productService.findAllWithEagerRelationships(pageable)
        } else {
            productService.findAll(pageable)
        }
        val headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page)
        return ResponseEntity.ok().headers(headers).body(page.content)
    }

    /**
     * `GET  /products/:id` : get the "id" product.
     *
     * @param id the id of the productDTO to retrieve.
     * @return the [ResponseEntity] with status `200 (OK)` and with body the productDTO, or with status `404 (Not Found)`.
     */
    @GetMapping("/products/{id}")
    fun getProduct(@PathVariable id: Long): ResponseEntity<ProductDTO> {
        log.debug("REST request to get Product : $id")
        val productDTO = productService.findOne(id)
        return ResponseUtil.wrapOrNotFound(productDTO)
    }
    /**
     *  `DELETE  /products/:id` : delete the "id" product.
     *
     * @param id the id of the productDTO to delete.
     * @return the [ResponseEntity] with status `204 (NO_CONTENT)`.
     */
    @DeleteMapping("/products/{id}")
    fun deleteProduct(@PathVariable id: Long): ResponseEntity<Void> {
        log.debug("REST request to delete Product : $id")

        productService.delete(id)
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString())).build()
    }
}
