package me.rasztabiga.fridgy.products.web.rest

import me.rasztabiga.fridgy.products.IntegrationTest
import me.rasztabiga.fridgy.products.domain.ProductUnit
import me.rasztabiga.fridgy.products.repository.ProductUnitRepository
import me.rasztabiga.fridgy.products.service.mapper.ProductUnitMapper
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
 * Integration tests for the [ProductUnitResource] REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ProductUnitResourceIT {
    @Autowired
    private lateinit var productUnitRepository: ProductUnitRepository

    @Autowired
    private lateinit var productUnitMapper: ProductUnitMapper

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
    private lateinit var restProductUnitMockMvc: MockMvc

    private lateinit var productUnit: ProductUnit

    @BeforeEach
    fun initTest() {
        productUnit = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createProductUnit() {
        val databaseSizeBeforeCreate = productUnitRepository.findAll().size

        // Create the ProductUnit
        val productUnitDTO = productUnitMapper.toDto(productUnit)
        restProductUnitMockMvc.perform(
            post(ENTITY_API_URL).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productUnitDTO))
        ).andExpect(status().isCreated)

        // Validate the ProductUnit in the database
        val productUnitList = productUnitRepository.findAll()
        assertThat(productUnitList).hasSize(databaseSizeBeforeCreate + 1)
        val testProductUnit = productUnitList[productUnitList.size - 1]

        assertThat(testProductUnit.name).isEqualTo(DEFAULT_NAME)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createProductUnitWithExistingId() {
        // Create the ProductUnit with an existing ID
        productUnit.id = 1L
        val productUnitDTO = productUnitMapper.toDto(productUnit)

        val databaseSizeBeforeCreate = productUnitRepository.findAll().size

        // An entity with an existing ID cannot be created, so this API call must fail
        restProductUnitMockMvc.perform(
            post(ENTITY_API_URL).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productUnitDTO))
        ).andExpect(status().isBadRequest)

        // Validate the ProductUnit in the database
        val productUnitList = productUnitRepository.findAll()
        assertThat(productUnitList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = productUnitRepository.findAll().size
        // set the field null
        productUnit.name = null

        // Create the ProductUnit, which fails.
        val productUnitDTO = productUnitMapper.toDto(productUnit)

        restProductUnitMockMvc.perform(
            post(ENTITY_API_URL).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productUnitDTO))
        ).andExpect(status().isBadRequest)

        val productUnitList = productUnitRepository.findAll()
        assertThat(productUnitList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllProductUnits() {
        // Initialize the database
        productUnitRepository.saveAndFlush(productUnit)

        // Get all the productUnitList
        restProductUnitMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(productUnit.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getProductUnit() {
        // Initialize the database
        productUnitRepository.saveAndFlush(productUnit)

        val id = productUnit.id
        assertNotNull(id)

        // Get the productUnit
        restProductUnitMockMvc.perform(get(ENTITY_API_URL_ID, productUnit.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(productUnit.id?.toInt()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
    }
    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingProductUnit() {
        // Get the productUnit
        restProductUnitMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun putNewProductUnit() {
        // Initialize the database
        productUnitRepository.saveAndFlush(productUnit)

        val databaseSizeBeforeUpdate = productUnitRepository.findAll().size

        // Update the productUnit
        val updatedProductUnit = productUnitRepository.findById(productUnit.id).get()
        // Disconnect from session so that the updates on updatedProductUnit are not directly saved in db
        em.detach(updatedProductUnit)
        updatedProductUnit.name = UPDATED_NAME
        val productUnitDTO = productUnitMapper.toDto(updatedProductUnit)

        restProductUnitMockMvc.perform(
            put(ENTITY_API_URL_ID, productUnitDTO.id).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productUnitDTO))
        ).andExpect(status().isOk)

        // Validate the ProductUnit in the database
        val productUnitList = productUnitRepository.findAll()
        assertThat(productUnitList).hasSize(databaseSizeBeforeUpdate)
        val testProductUnit = productUnitList[productUnitList.size - 1]
        assertThat(testProductUnit.name).isEqualTo(UPDATED_NAME)
    }

    @Test
    @Transactional
    fun putNonExistingProductUnit() {
        val databaseSizeBeforeUpdate = productUnitRepository.findAll().size
        productUnit.id = count.incrementAndGet()

        // Create the ProductUnit
        val productUnitDTO = productUnitMapper.toDto(productUnit)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductUnitMockMvc.perform(
            put(ENTITY_API_URL_ID, productUnitDTO.id).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productUnitDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the ProductUnit in the database
        val productUnitList = productUnitRepository.findAll()
        assertThat(productUnitList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithIdMismatchProductUnit() {
        val databaseSizeBeforeUpdate = productUnitRepository.findAll().size
        productUnit.id = count.incrementAndGet()

        // Create the ProductUnit
        val productUnitDTO = productUnitMapper.toDto(productUnit)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductUnitMockMvc.perform(
            put(ENTITY_API_URL_ID, count.incrementAndGet()).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productUnitDTO))
        ).andExpect(status().isBadRequest)

        // Validate the ProductUnit in the database
        val productUnitList = productUnitRepository.findAll()
        assertThat(productUnitList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithMissingIdPathParamProductUnit() {
        val databaseSizeBeforeUpdate = productUnitRepository.findAll().size
        productUnit.id = count.incrementAndGet()

        // Create the ProductUnit
        val productUnitDTO = productUnitMapper.toDto(productUnit)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductUnitMockMvc.perform(
            put(ENTITY_API_URL).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(productUnitDTO))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the ProductUnit in the database
        val productUnitList = productUnitRepository.findAll()
        assertThat(productUnitList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun partialUpdateProductUnitWithPatch() {

        // Initialize the database
        productUnitRepository.saveAndFlush(productUnit)

        val databaseSizeBeforeUpdate = productUnitRepository.findAll().size

// Update the productUnit using partial update
        val partialUpdatedProductUnit = ProductUnit().apply {
            id = productUnit.id

            name = UPDATED_NAME
        }

        restProductUnitMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedProductUnit.id).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedProductUnit))
        )
            .andExpect(status().isOk)

// Validate the ProductUnit in the database
        val productUnitList = productUnitRepository.findAll()
        assertThat(productUnitList).hasSize(databaseSizeBeforeUpdate)
        val testProductUnit = productUnitList.last()
        assertThat(testProductUnit.name).isEqualTo(UPDATED_NAME)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun fullUpdateProductUnitWithPatch() {

        // Initialize the database
        productUnitRepository.saveAndFlush(productUnit)

        val databaseSizeBeforeUpdate = productUnitRepository.findAll().size

// Update the productUnit using partial update
        val partialUpdatedProductUnit = ProductUnit().apply {
            id = productUnit.id

            name = UPDATED_NAME
        }

        restProductUnitMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedProductUnit.id).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedProductUnit))
        )
            .andExpect(status().isOk)

// Validate the ProductUnit in the database
        val productUnitList = productUnitRepository.findAll()
        assertThat(productUnitList).hasSize(databaseSizeBeforeUpdate)
        val testProductUnit = productUnitList.last()
        assertThat(testProductUnit.name).isEqualTo(UPDATED_NAME)
    }

    @Throws(Exception::class)
    fun patchNonExistingProductUnit() {
        val databaseSizeBeforeUpdate = productUnitRepository.findAll().size
        productUnit.id = count.incrementAndGet()

        // Create the ProductUnit
        val productUnitDTO = productUnitMapper.toDto(productUnit)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restProductUnitMockMvc.perform(
            patch(ENTITY_API_URL_ID, productUnitDTO.id).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(productUnitDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the ProductUnit in the database
        val productUnitList = productUnitRepository.findAll()
        assertThat(productUnitList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithIdMismatchProductUnit() {
        val databaseSizeBeforeUpdate = productUnitRepository.findAll().size
        productUnit.id = count.incrementAndGet()

        // Create the ProductUnit
        val productUnitDTO = productUnitMapper.toDto(productUnit)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductUnitMockMvc.perform(
            patch(ENTITY_API_URL_ID, count.incrementAndGet()).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(productUnitDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the ProductUnit in the database
        val productUnitList = productUnitRepository.findAll()
        assertThat(productUnitList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithMissingIdPathParamProductUnit() {
        val databaseSizeBeforeUpdate = productUnitRepository.findAll().size
        productUnit.id = count.incrementAndGet()

        // Create the ProductUnit
        val productUnitDTO = productUnitMapper.toDto(productUnit)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restProductUnitMockMvc.perform(
            patch(ENTITY_API_URL).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(productUnitDTO))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the ProductUnit in the database
        val productUnitList = productUnitRepository.findAll()
        assertThat(productUnitList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteProductUnit() {
        // Initialize the database
        productUnitRepository.saveAndFlush(productUnit)

        val databaseSizeBeforeDelete = productUnitRepository.findAll().size

        // Delete the productUnit
        restProductUnitMockMvc.perform(
            delete(ENTITY_API_URL_ID, productUnit.id).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val productUnitList = productUnitRepository.findAll()
        assertThat(productUnitList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_NAME = "AAAAAAAAAA"
        private const val UPDATED_NAME = "BBBBBBBBBB"

        private val ENTITY_API_URL: String = "/api/product-units"
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
        fun createEntity(em: EntityManager): ProductUnit {
            val productUnit = ProductUnit(

                name = DEFAULT_NAME

            )

            return productUnit
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): ProductUnit {
            val productUnit = ProductUnit(

                name = UPDATED_NAME

            )

            return productUnit
        }
    }
}
