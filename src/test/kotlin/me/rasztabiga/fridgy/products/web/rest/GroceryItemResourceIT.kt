package me.rasztabiga.fridgy.products.web.rest

import me.rasztabiga.fridgy.products.IntegrationTest
import me.rasztabiga.fridgy.products.domain.GroceryItem
import me.rasztabiga.fridgy.products.repository.GroceryItemRepository
import me.rasztabiga.fridgy.products.service.mapper.GroceryItemMapper
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
 * Integration tests for the [GroceryItemResource] REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class GroceryItemResourceIT {
    @Autowired
    private lateinit var groceryItemRepository: GroceryItemRepository

    @Autowired
    private lateinit var groceryItemMapper: GroceryItemMapper

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
    private lateinit var restGroceryItemMockMvc: MockMvc

    private lateinit var groceryItem: GroceryItem

    @BeforeEach
    fun initTest() {
        groceryItem = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createGroceryItem() {
        val databaseSizeBeforeCreate = groceryItemRepository.findAll().size

        // Create the GroceryItem
        val groceryItemDTO = groceryItemMapper.toDto(groceryItem)
        restGroceryItemMockMvc.perform(
            post(ENTITY_API_URL).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(groceryItemDTO))
        ).andExpect(status().isCreated)

        // Validate the GroceryItem in the database
        val groceryItemList = groceryItemRepository.findAll()
        assertThat(groceryItemList).hasSize(databaseSizeBeforeCreate + 1)
        val testGroceryItem = groceryItemList[groceryItemList.size - 1]

        assertThat(testGroceryItem.quantity).isEqualTo(DEFAULT_QUANTITY)
        assertThat(testGroceryItem.description).isEqualTo(DEFAULT_DESCRIPTION)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createGroceryItemWithExistingId() {
        // Create the GroceryItem with an existing ID
        groceryItem.id = 1L
        val groceryItemDTO = groceryItemMapper.toDto(groceryItem)

        val databaseSizeBeforeCreate = groceryItemRepository.findAll().size

        // An entity with an existing ID cannot be created, so this API call must fail
        restGroceryItemMockMvc.perform(
            post(ENTITY_API_URL).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(groceryItemDTO))
        ).andExpect(status().isBadRequest)

        // Validate the GroceryItem in the database
        val groceryItemList = groceryItemRepository.findAll()
        assertThat(groceryItemList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllGroceryItems() {
        // Initialize the database
        groceryItemRepository.saveAndFlush(groceryItem)

        // Get all the groceryItemList
        restGroceryItemMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(groceryItem.id?.toInt())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY.toDouble())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getGroceryItem() {
        // Initialize the database
        groceryItemRepository.saveAndFlush(groceryItem)

        val id = groceryItem.id
        assertNotNull(id)

        // Get the groceryItem
        restGroceryItemMockMvc.perform(get(ENTITY_API_URL_ID, groceryItem.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(groceryItem.id?.toInt()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY.toDouble()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
    }
    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingGroceryItem() {
        // Get the groceryItem
        restGroceryItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun putNewGroceryItem() {
        // Initialize the database
        groceryItemRepository.saveAndFlush(groceryItem)

        val databaseSizeBeforeUpdate = groceryItemRepository.findAll().size

        // Update the groceryItem
        val updatedGroceryItem = groceryItemRepository.findById(groceryItem.id).get()
        // Disconnect from session so that the updates on updatedGroceryItem are not directly saved in db
        em.detach(updatedGroceryItem)
        updatedGroceryItem.quantity = UPDATED_QUANTITY
        updatedGroceryItem.description = UPDATED_DESCRIPTION
        val groceryItemDTO = groceryItemMapper.toDto(updatedGroceryItem)

        restGroceryItemMockMvc.perform(
            put(ENTITY_API_URL_ID, groceryItemDTO.id).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(groceryItemDTO))
        ).andExpect(status().isOk)

        // Validate the GroceryItem in the database
        val groceryItemList = groceryItemRepository.findAll()
        assertThat(groceryItemList).hasSize(databaseSizeBeforeUpdate)
        val testGroceryItem = groceryItemList[groceryItemList.size - 1]
        assertThat(testGroceryItem.quantity).isEqualTo(UPDATED_QUANTITY)
        assertThat(testGroceryItem.description).isEqualTo(UPDATED_DESCRIPTION)
    }

    @Test
    @Transactional
    fun putNonExistingGroceryItem() {
        val databaseSizeBeforeUpdate = groceryItemRepository.findAll().size
        groceryItem.id = count.incrementAndGet()

        // Create the GroceryItem
        val groceryItemDTO = groceryItemMapper.toDto(groceryItem)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGroceryItemMockMvc.perform(
            put(ENTITY_API_URL_ID, groceryItemDTO.id).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(groceryItemDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the GroceryItem in the database
        val groceryItemList = groceryItemRepository.findAll()
        assertThat(groceryItemList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithIdMismatchGroceryItem() {
        val databaseSizeBeforeUpdate = groceryItemRepository.findAll().size
        groceryItem.id = count.incrementAndGet()

        // Create the GroceryItem
        val groceryItemDTO = groceryItemMapper.toDto(groceryItem)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGroceryItemMockMvc.perform(
            put(ENTITY_API_URL_ID, count.incrementAndGet()).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(groceryItemDTO))
        ).andExpect(status().isBadRequest)

        // Validate the GroceryItem in the database
        val groceryItemList = groceryItemRepository.findAll()
        assertThat(groceryItemList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithMissingIdPathParamGroceryItem() {
        val databaseSizeBeforeUpdate = groceryItemRepository.findAll().size
        groceryItem.id = count.incrementAndGet()

        // Create the GroceryItem
        val groceryItemDTO = groceryItemMapper.toDto(groceryItem)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGroceryItemMockMvc.perform(
            put(ENTITY_API_URL).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(groceryItemDTO))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the GroceryItem in the database
        val groceryItemList = groceryItemRepository.findAll()
        assertThat(groceryItemList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun partialUpdateGroceryItemWithPatch() {

        // Initialize the database
        groceryItemRepository.saveAndFlush(groceryItem)

        val databaseSizeBeforeUpdate = groceryItemRepository.findAll().size

// Update the groceryItem using partial update
        val partialUpdatedGroceryItem = GroceryItem().apply {
            id = groceryItem.id
        }

        restGroceryItemMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedGroceryItem.id).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedGroceryItem))
        )
            .andExpect(status().isOk)

// Validate the GroceryItem in the database
        val groceryItemList = groceryItemRepository.findAll()
        assertThat(groceryItemList).hasSize(databaseSizeBeforeUpdate)
        val testGroceryItem = groceryItemList.last()
        assertThat(testGroceryItem.quantity).isEqualTo(DEFAULT_QUANTITY)
        assertThat(testGroceryItem.description).isEqualTo(DEFAULT_DESCRIPTION)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun fullUpdateGroceryItemWithPatch() {

        // Initialize the database
        groceryItemRepository.saveAndFlush(groceryItem)

        val databaseSizeBeforeUpdate = groceryItemRepository.findAll().size

// Update the groceryItem using partial update
        val partialUpdatedGroceryItem = GroceryItem().apply {
            id = groceryItem.id

            quantity = UPDATED_QUANTITY
            description = UPDATED_DESCRIPTION
        }

        restGroceryItemMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedGroceryItem.id).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedGroceryItem))
        )
            .andExpect(status().isOk)

// Validate the GroceryItem in the database
        val groceryItemList = groceryItemRepository.findAll()
        assertThat(groceryItemList).hasSize(databaseSizeBeforeUpdate)
        val testGroceryItem = groceryItemList.last()
        assertThat(testGroceryItem.quantity).isEqualTo(UPDATED_QUANTITY)
        assertThat(testGroceryItem.description).isEqualTo(UPDATED_DESCRIPTION)
    }

    @Throws(Exception::class)
    fun patchNonExistingGroceryItem() {
        val databaseSizeBeforeUpdate = groceryItemRepository.findAll().size
        groceryItem.id = count.incrementAndGet()

        // Create the GroceryItem
        val groceryItemDTO = groceryItemMapper.toDto(groceryItem)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGroceryItemMockMvc.perform(
            patch(ENTITY_API_URL_ID, groceryItemDTO.id).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(groceryItemDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the GroceryItem in the database
        val groceryItemList = groceryItemRepository.findAll()
        assertThat(groceryItemList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithIdMismatchGroceryItem() {
        val databaseSizeBeforeUpdate = groceryItemRepository.findAll().size
        groceryItem.id = count.incrementAndGet()

        // Create the GroceryItem
        val groceryItemDTO = groceryItemMapper.toDto(groceryItem)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGroceryItemMockMvc.perform(
            patch(ENTITY_API_URL_ID, count.incrementAndGet()).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(groceryItemDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the GroceryItem in the database
        val groceryItemList = groceryItemRepository.findAll()
        assertThat(groceryItemList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithMissingIdPathParamGroceryItem() {
        val databaseSizeBeforeUpdate = groceryItemRepository.findAll().size
        groceryItem.id = count.incrementAndGet()

        // Create the GroceryItem
        val groceryItemDTO = groceryItemMapper.toDto(groceryItem)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGroceryItemMockMvc.perform(
            patch(ENTITY_API_URL).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(groceryItemDTO))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the GroceryItem in the database
        val groceryItemList = groceryItemRepository.findAll()
        assertThat(groceryItemList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteGroceryItem() {
        // Initialize the database
        groceryItemRepository.saveAndFlush(groceryItem)

        val databaseSizeBeforeDelete = groceryItemRepository.findAll().size

        // Delete the groceryItem
        restGroceryItemMockMvc.perform(
            delete(ENTITY_API_URL_ID, groceryItem.id).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val groceryItemList = groceryItemRepository.findAll()
        assertThat(groceryItemList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_QUANTITY: Double = 1.0
        private const val UPDATED_QUANTITY: Double = 2.0

        private const val DEFAULT_DESCRIPTION = "AAAAAAAAAA"
        private const val UPDATED_DESCRIPTION = "BBBBBBBBBB"

        private val ENTITY_API_URL: String = "/api/grocery-items"
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
        fun createEntity(em: EntityManager): GroceryItem {
            val groceryItem = GroceryItem(

                quantity = DEFAULT_QUANTITY,

                description = DEFAULT_DESCRIPTION

            )

            return groceryItem
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): GroceryItem {
            val groceryItem = GroceryItem(

                quantity = UPDATED_QUANTITY,

                description = UPDATED_DESCRIPTION

            )

            return groceryItem
        }
    }
}
