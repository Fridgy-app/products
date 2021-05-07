package me.rasztabiga.fridgy.products.web.rest

import me.rasztabiga.fridgy.products.IntegrationTest
import me.rasztabiga.fridgy.products.domain.Recipe
import me.rasztabiga.fridgy.products.repository.RecipeRepository
import me.rasztabiga.fridgy.products.service.mapper.RecipeMapper
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
 * Integration tests for the [RecipeResource] REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class RecipeResourceIT {
    @Autowired
    private lateinit var recipeRepository: RecipeRepository

    @Autowired
    private lateinit var recipeMapper: RecipeMapper

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
    private lateinit var restRecipeMockMvc: MockMvc

    private lateinit var recipe: Recipe

    @BeforeEach
    fun initTest() {
        recipe = createEntity(em)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createRecipe() {
        val databaseSizeBeforeCreate = recipeRepository.findAll().size

        // Create the Recipe
        val recipeDTO = recipeMapper.toDto(recipe)
        restRecipeMockMvc.perform(
            post(ENTITY_API_URL).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(recipeDTO))
        ).andExpect(status().isCreated)

        // Validate the Recipe in the database
        val recipeList = recipeRepository.findAll()
        assertThat(recipeList).hasSize(databaseSizeBeforeCreate + 1)
        val testRecipe = recipeList[recipeList.size - 1]

        assertThat(testRecipe.name).isEqualTo(DEFAULT_NAME)
        assertThat(testRecipe.instructionsBody).isEqualTo(DEFAULT_INSTRUCTIONS_BODY)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun createRecipeWithExistingId() {
        // Create the Recipe with an existing ID
        recipe.id = 1L
        val recipeDTO = recipeMapper.toDto(recipe)

        val databaseSizeBeforeCreate = recipeRepository.findAll().size

        // An entity with an existing ID cannot be created, so this API call must fail
        restRecipeMockMvc.perform(
            post(ENTITY_API_URL).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(recipeDTO))
        ).andExpect(status().isBadRequest)

        // Validate the Recipe in the database
        val recipeList = recipeRepository.findAll()
        assertThat(recipeList).hasSize(databaseSizeBeforeCreate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun checkNameIsRequired() {
        val databaseSizeBeforeTest = recipeRepository.findAll().size
        // set the field null
        recipe.name = null

        // Create the Recipe, which fails.
        val recipeDTO = recipeMapper.toDto(recipe)

        restRecipeMockMvc.perform(
            post(ENTITY_API_URL).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(recipeDTO))
        ).andExpect(status().isBadRequest)

        val recipeList = recipeRepository.findAll()
        assertThat(recipeList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun checkInstructionsBodyIsRequired() {
        val databaseSizeBeforeTest = recipeRepository.findAll().size
        // set the field null
        recipe.instructionsBody = null

        // Create the Recipe, which fails.
        val recipeDTO = recipeMapper.toDto(recipe)

        restRecipeMockMvc.perform(
            post(ENTITY_API_URL).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(recipeDTO))
        ).andExpect(status().isBadRequest)

        val recipeList = recipeRepository.findAll()
        assertThat(recipeList).hasSize(databaseSizeBeforeTest)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getAllRecipes() {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe)

        // Get all the recipeList
        restRecipeMockMvc.perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(recipe.id?.toInt())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].instructionsBody").value(hasItem(DEFAULT_INSTRUCTIONS_BODY)))
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun getRecipe() {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe)

        val id = recipe.id
        assertNotNull(id)

        // Get the recipe
        restRecipeMockMvc.perform(get(ENTITY_API_URL_ID, recipe.id))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(recipe.id?.toInt()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.instructionsBody").value(DEFAULT_INSTRUCTIONS_BODY))
    }
    @Test
    @Transactional
    @Throws(Exception::class)
    fun getNonExistingRecipe() {
        // Get the recipe
        restRecipeMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE))
            .andExpect(status().isNotFound)
    }
    @Test
    @Transactional
    fun putNewRecipe() {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe)

        val databaseSizeBeforeUpdate = recipeRepository.findAll().size

        // Update the recipe
        val updatedRecipe = recipeRepository.findById(recipe.id).get()
        // Disconnect from session so that the updates on updatedRecipe are not directly saved in db
        em.detach(updatedRecipe)
        updatedRecipe.name = UPDATED_NAME
        updatedRecipe.instructionsBody = UPDATED_INSTRUCTIONS_BODY
        val recipeDTO = recipeMapper.toDto(updatedRecipe)

        restRecipeMockMvc.perform(
            put(ENTITY_API_URL_ID, recipeDTO.id).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(recipeDTO))
        ).andExpect(status().isOk)

        // Validate the Recipe in the database
        val recipeList = recipeRepository.findAll()
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate)
        val testRecipe = recipeList[recipeList.size - 1]
        assertThat(testRecipe.name).isEqualTo(UPDATED_NAME)
        assertThat(testRecipe.instructionsBody).isEqualTo(UPDATED_INSTRUCTIONS_BODY)
    }

    @Test
    @Transactional
    fun putNonExistingRecipe() {
        val databaseSizeBeforeUpdate = recipeRepository.findAll().size
        recipe.id = count.incrementAndGet()

        // Create the Recipe
        val recipeDTO = recipeMapper.toDto(recipe)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRecipeMockMvc.perform(
            put(ENTITY_API_URL_ID, recipeDTO.id).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(recipeDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the Recipe in the database
        val recipeList = recipeRepository.findAll()
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithIdMismatchRecipe() {
        val databaseSizeBeforeUpdate = recipeRepository.findAll().size
        recipe.id = count.incrementAndGet()

        // Create the Recipe
        val recipeDTO = recipeMapper.toDto(recipe)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecipeMockMvc.perform(
            put(ENTITY_API_URL_ID, count.incrementAndGet()).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(recipeDTO))
        ).andExpect(status().isBadRequest)

        // Validate the Recipe in the database
        val recipeList = recipeRepository.findAll()
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun putWithMissingIdPathParamRecipe() {
        val databaseSizeBeforeUpdate = recipeRepository.findAll().size
        recipe.id = count.incrementAndGet()

        // Create the Recipe
        val recipeDTO = recipeMapper.toDto(recipe)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecipeMockMvc.perform(
            put(ENTITY_API_URL).with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonBytes(recipeDTO))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the Recipe in the database
        val recipeList = recipeRepository.findAll()
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun partialUpdateRecipeWithPatch() {

        // Initialize the database
        recipeRepository.saveAndFlush(recipe)

        val databaseSizeBeforeUpdate = recipeRepository.findAll().size

// Update the recipe using partial update
        val partialUpdatedRecipe = Recipe().apply {
            id = recipe.id

            name = UPDATED_NAME
            instructionsBody = UPDATED_INSTRUCTIONS_BODY
        }

        restRecipeMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedRecipe.id).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedRecipe))
        )
            .andExpect(status().isOk)

// Validate the Recipe in the database
        val recipeList = recipeRepository.findAll()
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate)
        val testRecipe = recipeList.last()
        assertThat(testRecipe.name).isEqualTo(UPDATED_NAME)
        assertThat(testRecipe.instructionsBody).isEqualTo(UPDATED_INSTRUCTIONS_BODY)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun fullUpdateRecipeWithPatch() {

        // Initialize the database
        recipeRepository.saveAndFlush(recipe)

        val databaseSizeBeforeUpdate = recipeRepository.findAll().size

// Update the recipe using partial update
        val partialUpdatedRecipe = Recipe().apply {
            id = recipe.id

            name = UPDATED_NAME
            instructionsBody = UPDATED_INSTRUCTIONS_BODY
        }

        restRecipeMockMvc.perform(
            patch(ENTITY_API_URL_ID, partialUpdatedRecipe.id).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(partialUpdatedRecipe))
        )
            .andExpect(status().isOk)

// Validate the Recipe in the database
        val recipeList = recipeRepository.findAll()
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate)
        val testRecipe = recipeList.last()
        assertThat(testRecipe.name).isEqualTo(UPDATED_NAME)
        assertThat(testRecipe.instructionsBody).isEqualTo(UPDATED_INSTRUCTIONS_BODY)
    }

    @Throws(Exception::class)
    fun patchNonExistingRecipe() {
        val databaseSizeBeforeUpdate = recipeRepository.findAll().size
        recipe.id = count.incrementAndGet()

        // Create the Recipe
        val recipeDTO = recipeMapper.toDto(recipe)

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restRecipeMockMvc.perform(
            patch(ENTITY_API_URL_ID, recipeDTO.id).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(recipeDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the Recipe in the database
        val recipeList = recipeRepository.findAll()
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithIdMismatchRecipe() {
        val databaseSizeBeforeUpdate = recipeRepository.findAll().size
        recipe.id = count.incrementAndGet()

        // Create the Recipe
        val recipeDTO = recipeMapper.toDto(recipe)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecipeMockMvc.perform(
            patch(ENTITY_API_URL_ID, count.incrementAndGet()).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(recipeDTO))
        )
            .andExpect(status().isBadRequest)

        // Validate the Recipe in the database
        val recipeList = recipeRepository.findAll()
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun patchWithMissingIdPathParamRecipe() {
        val databaseSizeBeforeUpdate = recipeRepository.findAll().size
        recipe.id = count.incrementAndGet()

        // Create the Recipe
        val recipeDTO = recipeMapper.toDto(recipe)

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restRecipeMockMvc.perform(
            patch(ENTITY_API_URL).with(csrf())
                .contentType("application/merge-patch+json")
                .content(convertObjectToJsonBytes(recipeDTO))
        )
            .andExpect(status().isMethodNotAllowed)

        // Validate the Recipe in the database
        val recipeList = recipeRepository.findAll()
        assertThat(recipeList).hasSize(databaseSizeBeforeUpdate)
    }

    @Test
    @Transactional
    @Throws(Exception::class)
    fun deleteRecipe() {
        // Initialize the database
        recipeRepository.saveAndFlush(recipe)

        val databaseSizeBeforeDelete = recipeRepository.findAll().size

        // Delete the recipe
        restRecipeMockMvc.perform(
            delete(ENTITY_API_URL_ID, recipe.id).with(csrf())
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent)

        // Validate the database contains one less item
        val recipeList = recipeRepository.findAll()
        assertThat(recipeList).hasSize(databaseSizeBeforeDelete - 1)
    }

    companion object {

        private const val DEFAULT_NAME = "AAAAAAAAAA"
        private const val UPDATED_NAME = "BBBBBBBBBB"

        private const val DEFAULT_INSTRUCTIONS_BODY = "AAAAAAAAAA"
        private const val UPDATED_INSTRUCTIONS_BODY = "BBBBBBBBBB"

        private val ENTITY_API_URL: String = "/api/recipes"
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
        fun createEntity(em: EntityManager): Recipe {
            val recipe = Recipe(

                name = DEFAULT_NAME,

                instructionsBody = DEFAULT_INSTRUCTIONS_BODY

            )

            return recipe
        }

        /**
         * Create an updated entity for this test.
         *
         * This is a static method, as tests for other entities might also need it,
         * if they test an entity which requires the current entity.
         */
        @JvmStatic
        fun createUpdatedEntity(em: EntityManager): Recipe {
            val recipe = Recipe(

                name = UPDATED_NAME,

                instructionsBody = UPDATED_INSTRUCTIONS_BODY

            )

            return recipe
        }
    }
}
