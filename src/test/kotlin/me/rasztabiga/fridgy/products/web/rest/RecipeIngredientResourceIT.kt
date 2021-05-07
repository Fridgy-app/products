package me.rasztabiga.fridgy.products.web.rest

import me.rasztabiga.fridgy.products.IntegrationTest
import me.rasztabiga.fridgy.products.domain.RecipeIngredient
import me.rasztabiga.fridgy.products.repository.RecipeIngredientRepository
import me.rasztabiga.fridgy.products.service.mapper.RecipeIngredientMapper
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
 * Integration tests for the [RecipeIngredientResource] REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RecipeIngredientResourceIT {
    @Autowired
    private lateinit var recipeIngredientRepository: RecipeIngredientRepository

    @Autowired
    private lateinit var recipeIngredientMapper: RecipeIngredientMapper

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
    private lateinit var restRecipeIngredientMockMvc: MockMvc

    private lateinit var recipeIngredient: RecipeIngredient

    @BeforeEach
    fun initTest() {
        recipeIngredient = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createRecipeIngredient() {
        val databaseSizeBeforeCreate = recipeIngredientRepository.findAll().size

        // Create the RecipeIngredient
        val recipeIngredientDTO = recipeIngredientMapper.toDto(recipeIngredient)
        restRecipeIngredientMockMvc.perform(
            post(ENTITY_API_URL).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(recipeIngredientDTO))
        ).andExpect(status().isCreated)

        // Validate the RecipeIngredient in the database
        val recipeIngredientList = recipeIngredientRepository.findAll()
        assertThat(recipeIngredientList).hasSize(databaseSizeBeforeCreate + 1)
        val testRecipeIngredient = recipeIngredientList[recipeIngredientList.size - 1]

        assertThat(testRecipeIngredient.quantity).isEqualTo(DEFAULT_QUANTITY)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createRecipeIngredientWithExistingId() {
        // Create the RecipeIngredient with an existing ID
        recipeIngredient.id = 1L
        val recipeIngredientDTO = recipeIngredientMapper.toDto(recipeIngredient)

        val databaseSizeBeforeCreate = recipeIngredientRepository.findAll().size

        // An entity with an existing ID cannot be created, so this API call must fail
        restRecipeIngredientMockMvc.perform(
            post(ENTITY_API_URL).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(recipeIngredientDTO))
        ).andExpect(status().isBadRequest)

        // Validate the RecipeIngredient in the database
        val recipeIngredientList = recipeIngredientRepository.findAll()
        assertThat(recipeIngredientList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllRecipeIngredients() {
        // Initialize the database
        recipeIngredientRepository.saveAndFlush(recipeIngredient)

        // Get all the recipeIngredientList
        restRecipeIngredientMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recipeIngredient.id?.toInt())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY.toDouble())))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getRecipeIngredient() {
        // Initialize the database
        recipeIngredientRepository.saveAndFlush(recipeIngredient)

        val id = recipeIngredient.id
        assertNotNull(id)

        // Get the recipeIngredient
        restRecipeIngredientMockMvc.perform(get(ENTITY_API_URL_ID, recipeIngredient.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(recipeIngredient.id?.toInt()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY.toDouble()))
    }
    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingRecipeIngredient() {
        // Get the recipeIngredient
        restRecipeIngredientMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun putNewRecipeIngredient() {
        // Initialize the database
        recipeIngredientRepository.saveAndFlush(recipeIngredient)

        val databaseSizeBeforeUpdate = recipeIngredientRepository.findAll().size

        // Update the recipeIngredient
        val updatedRecipeIngredient = recipeIngredientRepository.findById(recipeIngredient.id).get()
        // Disconnect from session so that the updates on updatedRecipeIngredient are not directly saved in db
        em.detach(updatedRecipeIngredient)
        updatedRecipeIngredient.quantity = UPDATED_QUANTITY
        val recipeIngredientDTO = recipeIngredientMapper.toDto(updatedRecipeIngredient)

        restRecipeIngredientMockMvc.perform(
            put(ENTITY_API_URL_ID, recipeIngredientDTO.id).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(recipeIngredientDTO))
        ).andExpect(status().isOk)

        // Validate the RecipeIngredient in the database
        val recipeIngredientList = recipeIngredientRepository.findAll()
        assertThat(recipeIngredientList).hasSize(databaseSizeBeforeUpdate)
        val testRecipeIngredient = recipeIngredientList[recipeIngredientList.size - 1]
        assertThat(testRecipeIngredient.quantity).isEqualTo(UPDATED_QUANTITY)
    }

    @Test
    @Transactional
    fun putNonExistingRecipeIngredient() {
        val databaseSizeBeforeUpdate = recipeIngredientRepository.findAll().size
        recipeIngredient.id = count.incrementAndGet()

        // Create the RecipeIngredient
        val recipeIngredientDTO = recipeIngredientMapper.toDto(recipeIngredient)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRecipeIngredientMockMvc.perform(
            put(ENTITY_API_URL_ID, recipeIngredientDTO.id).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(recipeIngredientDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the RecipeIngredient in the database
        val recipeIngredientList = recipeIngredientRepository.findAll()
        assertThat(recipeIngredientList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithIdMismatchRecipeIngredient() {
        val databaseSizeBeforeUpdate = recipeIngredientRepository.findAll().size
        recipeIngredient.id = count.incrementAndGet()

        // Create the RecipeIngredient
        val recipeIngredientDTO = recipeIngredientMapper.toDto(recipeIngredient)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecipeIngredientMockMvc.perform(
            put(ENTITY_API_URL_ID, count.incrementAndGet()).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(recipeIngredientDTO))
        ).andExpect(status().isBadRequest)

        // Validate the RecipeIngredient in the database
        val recipeIngredientList = recipeIngredientRepository.findAll()
        assertThat(recipeIngredientList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithMissingIdPathParamRecipeIngredient() {
        val databaseSizeBeforeUpdate = recipeIngredientRepository.findAll().size
        recipeIngredient.id = count.incrementAndGet()

        // Create the RecipeIngredient
        val recipeIngredientDTO = recipeIngredientMapper.toDto(recipeIngredient)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecipeIngredientMockMvc.perform(
            put(ENTITY_API_URL).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(recipeIngredientDTO))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the RecipeIngredient in the database
        val recipeIngredientList = recipeIngredientRepository.findAll()
        assertThat(recipeIngredientList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun partialUpdateRecipeIngredientWithPatch() {

        // Initialize the database
        recipeIngredientRepository.saveAndFlush(recipeIngredient)

        val databaseSizeBeforeUpdate = recipeIngredientRepository.findAll().size

// Update the recipeIngredient using partial update
        val partialUpdatedRecipeIngredient = RecipeIngredient().apply {
            id = recipeIngredient.id

            quantity = UPDATED_QUANTITY
        }

        restRecipeIngredientMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedRecipeIngredient.id).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedRecipeIngredient))
        )
            .andExpect(status().isOk)

// Validate the RecipeIngredient in the database
        val recipeIngredientList = recipeIngredientRepository.findAll()
        assertThat(recipeIngredientList).hasSize(databaseSizeBeforeUpdate)
        val testRecipeIngredient = recipeIngredientList.last()
        assertThat(testRecipeIngredient.quantity).isEqualTo(UPDATED_QUANTITY)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun fullUpdateRecipeIngredientWithPatch() {

        // Initialize the database
        recipeIngredientRepository.saveAndFlush(recipeIngredient)

        val databaseSizeBeforeUpdate = recipeIngredientRepository.findAll().size

// Update the recipeIngredient using partial update
        val partialUpdatedRecipeIngredient = RecipeIngredient().apply {
            id = recipeIngredient.id

            quantity = UPDATED_QUANTITY
        }

        restRecipeIngredientMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedRecipeIngredient.id).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedRecipeIngredient))
        )
            .andExpect(status().isOk)

// Validate the RecipeIngredient in the database
        val recipeIngredientList = recipeIngredientRepository.findAll()
        assertThat(recipeIngredientList).hasSize(databaseSizeBeforeUpdate)
        val testRecipeIngredient = recipeIngredientList.last()
        assertThat(testRecipeIngredient.quantity).isEqualTo(UPDATED_QUANTITY)
    }

    @Throws(Exception::class)
    fun patchNonExistingRecipeIngredient() {
        val databaseSizeBeforeUpdate = recipeIngredientRepository.findAll().size
        recipeIngredient.id = count.incrementAndGet()

        // Create the RecipeIngredient
        val recipeIngredientDTO = recipeIngredientMapper.toDto(recipeIngredient)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRecipeIngredientMockMvc.perform(
            patch(ENTITY_API_URL_ID, recipeIngredientDTO.id).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(recipeIngredientDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the RecipeIngredient in the database
        val recipeIngredientList = recipeIngredientRepository.findAll()
        assertThat(recipeIngredientList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithIdMismatchRecipeIngredient() {
        val databaseSizeBeforeUpdate = recipeIngredientRepository.findAll().size
        recipeIngredient.id = count.incrementAndGet()

        // Create the RecipeIngredient
        val recipeIngredientDTO = recipeIngredientMapper.toDto(recipeIngredient)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecipeIngredientMockMvc.perform(
            patch(ENTITY_API_URL_ID, count.incrementAndGet()).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(recipeIngredientDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the RecipeIngredient in the database
        val recipeIngredientList = recipeIngredientRepository.findAll()
        assertThat(recipeIngredientList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithMissingIdPathParamRecipeIngredient() {
        val databaseSizeBeforeUpdate = recipeIngredientRepository.findAll().size
        recipeIngredient.id = count.incrementAndGet()

        // Create the RecipeIngredient
        val recipeIngredientDTO = recipeIngredientMapper.toDto(recipeIngredient)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecipeIngredientMockMvc.perform(
            patch(ENTITY_API_URL).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(recipeIngredientDTO))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the RecipeIngredient in the database
        val recipeIngredientList = recipeIngredientRepository.findAll()
        assertThat(recipeIngredientList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteRecipeIngredient() {
        // Initialize the database
        recipeIngredientRepository.saveAndFlush(recipeIngredient)

        val databaseSizeBeforeDelete = recipeIngredientRepository.findAll().size

        // Delete the recipeIngredient
        restRecipeIngredientMockMvc.perform(
            delete(ENTITY_API_URL_ID, recipeIngredient.id).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val recipeIngredientList = recipeIngredientRepository.findAll()
        assertThat(recipeIngredientList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_QUANTITY: Double = 1.0
        private const val UPDATED_QUANTITY: Double = 2.0

        private val ENTITY_API_URL: String = "/api/recipe-ingredients"
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
        fun createEntity(em: EntityManager): RecipeIngredient {
            val recipeIngredient = RecipeIngredient(

                quantity = DEFAULT_QUANTITY

            )

            return recipeIngredient
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): RecipeIngredient {
            val recipeIngredient = RecipeIngredient(

                quantity = UPDATED_QUANTITY

            )

            return recipeIngredient
        }
    }
}
