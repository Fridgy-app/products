package me.rasztabiga.fridgy.products.web.rest

import me.rasztabiga.fridgy.products.IntegrationTest
import me.rasztabiga.fridgy.products.domain.Product
import me.rasztabiga.fridgy.products.repository.ProductRepository
import me.rasztabiga.fridgy.products.service.ProductService
import me.rasztabiga.fridgy.products.service.mapper.ProductMapper
import me.rasztabiga.fridgy.products.web.rest.errors.ExceptionTranslator
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.Extensions
import org.mockito.ArgumentMatchers.*
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.data.domain.PageImpl
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.http.MediaType
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.transaction.annotation.Transactional
import org.springframework.validation.Validator
import java.util.Random
import java.util.concurrent.atomic.AtomicLong
import javax.persistence.EntityManager
import kotlin.test.assertNotNull

/**
 * Integration tests for the [ProductResource] REST controller.
 */
@IntegrationTest
@Extensions(
    ExtendWith(MockitoExtension::class)
)
@AutoConfigureMockMvc
@WithMockUser
class ProductResourceIT {
    @Autowired
    private lateinit var productRepository: ProductRepository

    @Mock
    private lateinit var productRepositoryMock: ProductRepository

    @Autowired
    private lateinit var productMapper: ProductMapper

    @Mock
    private lateinit var productServiceMock: ProductService

    @Autowired
    private lateinit var jacksonMessageConverter: MappingJackson2HttpMessageConverter

    @Autowired
    private lateinit var pageableArgumentResolver: PageableHandlerMethodArgumentResolver

    @Autowired
    private lateinit var exceptionTranslator: ExceptionTranslator

    @Autowired
    private lateinit var validator: Validator

    @Autowired
    private lateinit var em: EntityManager

    @Autowired
    private lateinit var restProductMockMvc: MockMvc

    private lateinit var product: Product

    @BeforeEach
    fun initTest() {
        product = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createProduct() {
        val databaseSizeBeforeCreate = productRepository.findAll().size

        // Create the Product
        val productDTO = productMapper.toDto(product)
        restProductMockMvc.perform(
            post(ENTITY_API_URL).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productDTO))
        ).andExpect(status().isCreated)

        // Validate the Product in the database
        val productList = productRepository.findAll()
        assertThat(productList).hasSize(databaseSizeBeforeCreate + 1)
        val testProduct = productList[productList.size - 1]

        assertThat(testProduct.name).isEqualTo(DEFAULT_NAME)
        assertThat(testProduct.eanCode).isEqualTo(DEFAULT_EAN_CODE)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createProductWithExistingId() {
        // Create the Product with an existing ID
        product.id = 1L
        val productDTO = productMapper.toDto(product)

        val databaseSizeBeforeCreate = productRepository.findAll().size

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductMockMvc.perform(
            post(ENTITY_API_URL).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productDTO))
        ).andExpect(status().isBadRequest)

        // Validate the Product in the database
        val productList = productRepository.findAll()
        assertThat(productList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = productRepository.findAll().size
        // set the field null
        product.name = null

        // Create the Product, which fails.
        val productDTO = productMapper.toDto(product)

        restProductMockMvc.perform(
            post(ENTITY_API_URL).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productDTO))
        ).andExpect(status().isBadRequest)

        val productList = productRepository.findAll()
        assertThat(productList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllProducts() {
        // Initialize the database
        productRepository.saveAndFlush(product)

        // Get all the productList
        restProductMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(product.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].eanCode").value(hasItem(DEFAULT_EAN_CODE)))
    }

    @Suppress("unchecked")
    @Throws(Exception::class)
    fun getAllProductsWithEagerRelationshipsIsEnabled() {
        `when`(productServiceMock.findAllWithEagerRelationships(any())).thenReturn(PageImpl(mutableListOf()))

        restProductMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true"))
            .andExpect(status().isOk)

        verify(productServiceMock, times(1)).findAllWithEagerRelationships(any())
    }

    @Suppress("unchecked")
    @Throws(Exception::class)
    fun getAllProductsWithEagerRelationshipsIsNotEnabled() {
        `when`(productServiceMock.findAllWithEagerRelationships(any())).thenReturn(PageImpl(mutableListOf()))

        restProductMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true"))
            .andExpect(status().isOk)

        verify(productServiceMock, times(1)).findAllWithEagerRelationships(any())
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getProduct() {
        // Initialize the database
        productRepository.saveAndFlush(product)

        val id = product.id
        assertNotNull(id)

        // Get the product
        restProductMockMvc.perform(get(ENTITY_API_URL_ID, product.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(product.id?.toInt()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.eanCode").value(DEFAULT_EAN_CODE))
    }
    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingProduct() {
        // Get the product
        restProductMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun putNewProduct() {
        // Initialize the database
        productRepository.saveAndFlush(product)

        val databaseSizeBeforeUpdate = productRepository.findAll().size

        // Update the product
        val updatedProduct = productRepository.findById(product.id).get()
        // Disconnect from session so that the updates on updatedProduct are not directly saved in db
        em.detach(updatedProduct)
        updatedProduct.name = UPDATED_NAME
        updatedProduct.eanCode = UPDATED_EAN_CODE
        val productDTO = productMapper.toDto(updatedProduct)

        restProductMockMvc.perform(
            put(ENTITY_API_URL_ID, productDTO.id).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productDTO))
        ).andExpect(status().isOk)

        // Validate the Product in the database
        val productList = productRepository.findAll()
        assertThat(productList).hasSize(databaseSizeBeforeUpdate)
        val testProduct = productList[productList.size - 1]
        assertThat(testProduct.name).isEqualTo(UPDATED_NAME)
        assertThat(testProduct.eanCode).isEqualTo(UPDATED_EAN_CODE)
    }

    @Test
    @Transactional
    fun putNonExistingProduct() {
        val databaseSizeBeforeUpdate = productRepository.findAll().size
        product.id = count.incrementAndGet()

        // Create the Product
        val productDTO = productMapper.toDto(product)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductMockMvc.perform(
            put(ENTITY_API_URL_ID, productDTO.id).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the Product in the database
        val productList = productRepository.findAll()
        assertThat(productList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithIdMismatchProduct() {
        val databaseSizeBeforeUpdate = productRepository.findAll().size
        product.id = count.incrementAndGet()

        // Create the Product
        val productDTO = productMapper.toDto(product)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc.perform(
            put(ENTITY_API_URL_ID, count.incrementAndGet()).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productDTO))
        ).andExpect(status().isBadRequest)

        // Validate the Product in the database
        val productList = productRepository.findAll()
        assertThat(productList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithMissingIdPathParamProduct() {
        val databaseSizeBeforeUpdate = productRepository.findAll().size
        product.id = count.incrementAndGet()

        // Create the Product
        val productDTO = productMapper.toDto(product)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc.perform(
            put(ENTITY_API_URL).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productDTO))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the Product in the database
        val productList = productRepository.findAll()
        assertThat(productList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun partialUpdateProductWithPatch() {

        // Initialize the database
        productRepository.saveAndFlush(product)

        val databaseSizeBeforeUpdate = productRepository.findAll().size

// Update the product using partial update
        val partialUpdatedProduct = Product().apply {
            id = product.id
        }

        restProductMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedProduct.id).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedProduct))
        )
            .andExpect(status().isOk)

// Validate the Product in the database
        val productList = productRepository.findAll()
        assertThat(productList).hasSize(databaseSizeBeforeUpdate)
        val testProduct = productList.last()
        assertThat(testProduct.name).isEqualTo(DEFAULT_NAME)
        assertThat(testProduct.eanCode).isEqualTo(DEFAULT_EAN_CODE)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun fullUpdateProductWithPatch() {

        // Initialize the database
        productRepository.saveAndFlush(product)

        val databaseSizeBeforeUpdate = productRepository.findAll().size

// Update the product using partial update
        val partialUpdatedProduct = Product().apply {
            id = product.id

            name = UPDATED_NAME
            eanCode = UPDATED_EAN_CODE
        }

        restProductMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedProduct.id).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedProduct))
        )
            .andExpect(status().isOk)

// Validate the Product in the database
        val productList = productRepository.findAll()
        assertThat(productList).hasSize(databaseSizeBeforeUpdate)
        val testProduct = productList.last()
        assertThat(testProduct.name).isEqualTo(UPDATED_NAME)
        assertThat(testProduct.eanCode).isEqualTo(UPDATED_EAN_CODE)
    }

    @Throws(Exception::class)
    fun patchNonExistingProduct() {
        val databaseSizeBeforeUpdate = productRepository.findAll().size
        product.id = count.incrementAndGet()

        // Create the Product
        val productDTO = productMapper.toDto(product)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductMockMvc.perform(
            patch(ENTITY_API_URL_ID, productDTO.id).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(productDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the Product in the database
        val productList = productRepository.findAll()
        assertThat(productList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithIdMismatchProduct() {
        val databaseSizeBeforeUpdate = productRepository.findAll().size
        product.id = count.incrementAndGet()

        // Create the Product
        val productDTO = productMapper.toDto(product)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc.perform(
            patch(ENTITY_API_URL_ID, count.incrementAndGet()).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(productDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the Product in the database
        val productList = productRepository.findAll()
        assertThat(productList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithMissingIdPathParamProduct() {
        val databaseSizeBeforeUpdate = productRepository.findAll().size
        product.id = count.incrementAndGet()

        // Create the Product
        val productDTO = productMapper.toDto(product)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductMockMvc.perform(
            patch(ENTITY_API_URL).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(productDTO))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the Product in the database
        val productList = productRepository.findAll()
        assertThat(productList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteProduct() {
        // Initialize the database
        productRepository.saveAndFlush(product)

        val databaseSizeBeforeDelete = productRepository.findAll().size

        // Delete the product
        restProductMockMvc.perform(
            delete(ENTITY_API_URL_ID, product.id).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val productList = productRepository.findAll()
        assertThat(productList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_NAME = "AAAAAAAAAA"
        private const val UPDATED_NAME = "BBBBBBBBBB"

        private const val DEFAULT_EAN_CODE = "AAAAAAAAAA"
        private const val UPDATED_EAN_CODE = "BBBBBBBBBB"

        private val ENTITY_API_URL: String = "/api/products"
        private val ENTITY_API_URL_ID: String = ENTITY_API_URL + "/{id}"

        private val random: Random = Random()
        private val count: AtomicLong = AtomicLong(random.nextInt().toLong() + (2 * Integer.MAX_VALUE))

        /**
         * Create an entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createEntity(em: EntityManager): Product {
            val product = Product(

                name = DEFAULT_NAME,

                eanCode = DEFAULT_EAN_CODE

            )

            return product
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Product {
            val product = Product(

                name = UPDATED_NAME,

                eanCode = UPDATED_EAN_CODE

            )

            return product
        }
    }
}
