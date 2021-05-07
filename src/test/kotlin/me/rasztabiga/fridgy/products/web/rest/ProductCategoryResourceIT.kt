package me.rasztabiga.fridgy.products.web.rest

import me.rasztabiga.fridgy.products.IntegrationTest
import me.rasztabiga.fridgy.products.domain.ProductCategory
import me.rasztabiga.fridgy.products.repository.ProductCategoryRepository
import me.rasztabiga.fridgy.products.service.mapper.ProductCategoryMapper
import me.rasztabiga.fridgy.products.web.rest.errors.ExceptionTranslator
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.Matchers.hasItem
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
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
 * Integration tests for the [ProductCategoryResource] REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProductCategoryResourceIT {
    @Autowired
    private lateinit var productCategoryRepository: ProductCategoryRepository

    @Autowired
    private lateinit var productCategoryMapper: ProductCategoryMapper

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
    private lateinit var restProductCategoryMockMvc: MockMvc

    private lateinit var productCategory: ProductCategory

    @BeforeEach
    fun initTest() {
        productCategory = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createProductCategory() {
        val databaseSizeBeforeCreate = productCategoryRepository.findAll().size

        // Create the ProductCategory
        val productCategoryDTO = productCategoryMapper.toDto(productCategory)
        restProductCategoryMockMvc.perform(
            post(ENTITY_API_URL).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productCategoryDTO))
        ).andExpect(status().isCreated)

        // Validate the ProductCategory in the database
        val productCategoryList = productCategoryRepository.findAll()
        assertThat(productCategoryList).hasSize(databaseSizeBeforeCreate + 1)
        val testProductCategory = productCategoryList[productCategoryList.size - 1]

        assertThat(testProductCategory.name).isEqualTo(DEFAULT_NAME)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createProductCategoryWithExistingId() {
        // Create the ProductCategory with an existing ID
        productCategory.id = 1L
        val productCategoryDTO = productCategoryMapper.toDto(productCategory)

        val databaseSizeBeforeCreate = productCategoryRepository.findAll().size

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductCategoryMockMvc.perform(
            post(ENTITY_API_URL).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productCategoryDTO))
        ).andExpect(status().isBadRequest)

        // Validate the ProductCategory in the database
        val productCategoryList = productCategoryRepository.findAll()
        assertThat(productCategoryList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = productCategoryRepository.findAll().size
        // set the field null
        productCategory.name = null

        // Create the ProductCategory, which fails.
        val productCategoryDTO = productCategoryMapper.toDto(productCategory)

        restProductCategoryMockMvc.perform(
            post(ENTITY_API_URL).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productCategoryDTO))
        ).andExpect(status().isBadRequest)

        val productCategoryList = productCategoryRepository.findAll()
        assertThat(productCategoryList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllProductCategories() {
        // Initialize the database
        productCategoryRepository.saveAndFlush(productCategory)

        // Get all the productCategoryList
        restProductCategoryMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productCategory.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getProductCategory() {
        // Initialize the database
        productCategoryRepository.saveAndFlush(productCategory)

        val id = productCategory.id
        assertNotNull(id)

        // Get the productCategory
        restProductCategoryMockMvc.perform(get(ENTITY_API_URL_ID, productCategory.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(productCategory.id?.toInt()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
    }
    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingProductCategory() {
        // Get the productCategory
        restProductCategoryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun putNewProductCategory() {
        // Initialize the database
        productCategoryRepository.saveAndFlush(productCategory)

        val databaseSizeBeforeUpdate = productCategoryRepository.findAll().size

        // Update the productCategory
        val updatedProductCategory = productCategoryRepository.findById(productCategory.id).get()
        // Disconnect from session so that the updates on updatedProductCategory are not directly saved in db
        em.detach(updatedProductCategory)
        updatedProductCategory.name = UPDATED_NAME
        val productCategoryDTO = productCategoryMapper.toDto(updatedProductCategory)

        restProductCategoryMockMvc.perform(
            put(ENTITY_API_URL_ID, productCategoryDTO.id).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productCategoryDTO))
        ).andExpect(status().isOk)

        // Validate the ProductCategory in the database
        val productCategoryList = productCategoryRepository.findAll()
        assertThat(productCategoryList).hasSize(databaseSizeBeforeUpdate)
        val testProductCategory = productCategoryList[productCategoryList.size - 1]
        assertThat(testProductCategory.name).isEqualTo(UPDATED_NAME)
    }

    @Test
    @Transactional
    fun putNonExistingProductCategory() {
        val databaseSizeBeforeUpdate = productCategoryRepository.findAll().size
        productCategory.id = count.incrementAndGet()

        // Create the ProductCategory
        val productCategoryDTO = productCategoryMapper.toDto(productCategory)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductCategoryMockMvc.perform(
            put(ENTITY_API_URL_ID, productCategoryDTO.id).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productCategoryDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the ProductCategory in the database
        val productCategoryList = productCategoryRepository.findAll()
        assertThat(productCategoryList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithIdMismatchProductCategory() {
        val databaseSizeBeforeUpdate = productCategoryRepository.findAll().size
        productCategory.id = count.incrementAndGet()

        // Create the ProductCategory
        val productCategoryDTO = productCategoryMapper.toDto(productCategory)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductCategoryMockMvc.perform(
            put(ENTITY_API_URL_ID, count.incrementAndGet()).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productCategoryDTO))
        ).andExpect(status().isBadRequest)

        // Validate the ProductCategory in the database
        val productCategoryList = productCategoryRepository.findAll()
        assertThat(productCategoryList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithMissingIdPathParamProductCategory() {
        val databaseSizeBeforeUpdate = productCategoryRepository.findAll().size
        productCategory.id = count.incrementAndGet()

        // Create the ProductCategory
        val productCategoryDTO = productCategoryMapper.toDto(productCategory)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductCategoryMockMvc.perform(
            put(ENTITY_API_URL).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productCategoryDTO))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the ProductCategory in the database
        val productCategoryList = productCategoryRepository.findAll()
        assertThat(productCategoryList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun partialUpdateProductCategoryWithPatch() {

        // Initialize the database
        productCategoryRepository.saveAndFlush(productCategory)

        val databaseSizeBeforeUpdate = productCategoryRepository.findAll().size

// Update the productCategory using partial update
        val partialUpdatedProductCategory = ProductCategory().apply {
            id = productCategory.id

            name = UPDATED_NAME
        }

        restProductCategoryMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedProductCategory.id).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedProductCategory))
        )
            .andExpect(status().isOk)

// Validate the ProductCategory in the database
        val productCategoryList = productCategoryRepository.findAll()
        assertThat(productCategoryList).hasSize(databaseSizeBeforeUpdate)
        val testProductCategory = productCategoryList.last()
        assertThat(testProductCategory.name).isEqualTo(UPDATED_NAME)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun fullUpdateProductCategoryWithPatch() {

        // Initialize the database
        productCategoryRepository.saveAndFlush(productCategory)

        val databaseSizeBeforeUpdate = productCategoryRepository.findAll().size

// Update the productCategory using partial update
        val partialUpdatedProductCategory = ProductCategory().apply {
            id = productCategory.id

            name = UPDATED_NAME
        }

        restProductCategoryMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedProductCategory.id).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedProductCategory))
        )
            .andExpect(status().isOk)

// Validate the ProductCategory in the database
        val productCategoryList = productCategoryRepository.findAll()
        assertThat(productCategoryList).hasSize(databaseSizeBeforeUpdate)
        val testProductCategory = productCategoryList.last()
        assertThat(testProductCategory.name).isEqualTo(UPDATED_NAME)
    }

    @Throws(Exception::class)
    fun patchNonExistingProductCategory() {
        val databaseSizeBeforeUpdate = productCategoryRepository.findAll().size
        productCategory.id = count.incrementAndGet()

        // Create the ProductCategory
        val productCategoryDTO = productCategoryMapper.toDto(productCategory)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductCategoryMockMvc.perform(
            patch(ENTITY_API_URL_ID, productCategoryDTO.id).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(productCategoryDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the ProductCategory in the database
        val productCategoryList = productCategoryRepository.findAll()
        assertThat(productCategoryList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithIdMismatchProductCategory() {
        val databaseSizeBeforeUpdate = productCategoryRepository.findAll().size
        productCategory.id = count.incrementAndGet()

        // Create the ProductCategory
        val productCategoryDTO = productCategoryMapper.toDto(productCategory)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductCategoryMockMvc.perform(
            patch(ENTITY_API_URL_ID, count.incrementAndGet()).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(productCategoryDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the ProductCategory in the database
        val productCategoryList = productCategoryRepository.findAll()
        assertThat(productCategoryList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithMissingIdPathParamProductCategory() {
        val databaseSizeBeforeUpdate = productCategoryRepository.findAll().size
        productCategory.id = count.incrementAndGet()

        // Create the ProductCategory
        val productCategoryDTO = productCategoryMapper.toDto(productCategory)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductCategoryMockMvc.perform(
            patch(ENTITY_API_URL).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(productCategoryDTO))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the ProductCategory in the database
        val productCategoryList = productCategoryRepository.findAll()
        assertThat(productCategoryList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteProductCategory() {
        // Initialize the database
        productCategoryRepository.saveAndFlush(productCategory)

        val databaseSizeBeforeDelete = productCategoryRepository.findAll().size

        // Delete the productCategory
        restProductCategoryMockMvc.perform(
            delete(ENTITY_API_URL_ID, productCategory.id).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val productCategoryList = productCategoryRepository.findAll()
        assertThat(productCategoryList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_NAME = "AAAAAAAAAA"
        private const val UPDATED_NAME = "BBBBBBBBBB"

        private val ENTITY_API_URL: String = "/api/product-categories"
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
        fun createEntity(em: EntityManager): ProductCategory {
            val productCategory = ProductCategory(

                name = DEFAULT_NAME

            )

            return productCategory
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): ProductCategory {
            val productCategory = ProductCategory(

                name = UPDATED_NAME

            )

            return productCategory
        }
    }
}
