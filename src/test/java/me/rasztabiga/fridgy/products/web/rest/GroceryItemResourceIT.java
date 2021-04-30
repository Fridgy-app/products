package me.rasztabiga.fridgy.products.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import me.rasztabiga.fridgy.products.IntegrationTest;
import me.rasztabiga.fridgy.products.domain.GroceryItem;
import me.rasztabiga.fridgy.products.repository.GroceryItemRepository;
import me.rasztabiga.fridgy.products.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link GroceryItemResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class GroceryItemResourceIT {

    private static final Double DEFAULT_QUANTITY = 1D;
    private static final Double UPDATED_QUANTITY = 2D;

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/grocery-items";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private GroceryItemRepository groceryItemRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restGroceryItemMockMvc;

    private GroceryItem groceryItem;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GroceryItem createEntity(EntityManager em) {
        GroceryItem groceryItem = new GroceryItem().quantity(DEFAULT_QUANTITY).description(DEFAULT_DESCRIPTION);
        return groceryItem;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static GroceryItem createUpdatedEntity(EntityManager em) {
        GroceryItem groceryItem = new GroceryItem().quantity(UPDATED_QUANTITY).description(UPDATED_DESCRIPTION);
        return groceryItem;
    }

    @BeforeEach
    public void initTest() {
        groceryItem = createEntity(em);
    }

    @Test
    @Transactional
    void createGroceryItem() throws Exception {
        int databaseSizeBeforeCreate = groceryItemRepository.findAll().size();
        // Create the GroceryItem
        restGroceryItemMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(groceryItem))
            )
            .andExpect(status().isCreated());

        // Validate the GroceryItem in the database
        List<GroceryItem> groceryItemList = groceryItemRepository.findAll();
        assertThat(groceryItemList).hasSize(databaseSizeBeforeCreate + 1);
        GroceryItem testGroceryItem = groceryItemList.get(groceryItemList.size() - 1);
        assertThat(testGroceryItem.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
        assertThat(testGroceryItem.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void createGroceryItemWithExistingId() throws Exception {
        // Create the GroceryItem with an existing ID
        groceryItem.setId(1L);

        int databaseSizeBeforeCreate = groceryItemRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restGroceryItemMockMvc
            .perform(
                post(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(groceryItem))
            )
            .andExpect(status().isBadRequest());

        // Validate the GroceryItem in the database
        List<GroceryItem> groceryItemList = groceryItemRepository.findAll();
        assertThat(groceryItemList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllGroceryItems() throws Exception {
        // Initialize the database
        groceryItemRepository.saveAndFlush(groceryItem);

        // Get all the groceryItemList
        restGroceryItemMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(groceryItem.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY.doubleValue())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)));
    }

    @Test
    @Transactional
    void getGroceryItem() throws Exception {
        // Initialize the database
        groceryItemRepository.saveAndFlush(groceryItem);

        // Get the groceryItem
        restGroceryItemMockMvc
            .perform(get(ENTITY_API_URL_ID, groceryItem.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(groceryItem.getId().intValue()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY.doubleValue()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION));
    }

    @Test
    @Transactional
    void getNonExistingGroceryItem() throws Exception {
        // Get the groceryItem
        restGroceryItemMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewGroceryItem() throws Exception {
        // Initialize the database
        groceryItemRepository.saveAndFlush(groceryItem);

        int databaseSizeBeforeUpdate = groceryItemRepository.findAll().size();

        // Update the groceryItem
        GroceryItem updatedGroceryItem = groceryItemRepository.findById(groceryItem.getId()).get();
        // Disconnect from session so that the updates on updatedGroceryItem are not directly saved in db
        em.detach(updatedGroceryItem);
        updatedGroceryItem.quantity(UPDATED_QUANTITY).description(UPDATED_DESCRIPTION);

        restGroceryItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedGroceryItem.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedGroceryItem))
            )
            .andExpect(status().isOk());

        // Validate the GroceryItem in the database
        List<GroceryItem> groceryItemList = groceryItemRepository.findAll();
        assertThat(groceryItemList).hasSize(databaseSizeBeforeUpdate);
        GroceryItem testGroceryItem = groceryItemList.get(groceryItemList.size() - 1);
        assertThat(testGroceryItem.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testGroceryItem.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void putNonExistingGroceryItem() throws Exception {
        int databaseSizeBeforeUpdate = groceryItemRepository.findAll().size();
        groceryItem.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGroceryItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, groceryItem.getId())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(groceryItem))
            )
            .andExpect(status().isBadRequest());

        // Validate the GroceryItem in the database
        List<GroceryItem> groceryItemList = groceryItemRepository.findAll();
        assertThat(groceryItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchGroceryItem() throws Exception {
        int databaseSizeBeforeUpdate = groceryItemRepository.findAll().size();
        groceryItem.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGroceryItemMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(groceryItem))
            )
            .andExpect(status().isBadRequest());

        // Validate the GroceryItem in the database
        List<GroceryItem> groceryItemList = groceryItemRepository.findAll();
        assertThat(groceryItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamGroceryItem() throws Exception {
        int databaseSizeBeforeUpdate = groceryItemRepository.findAll().size();
        groceryItem.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGroceryItemMockMvc
            .perform(
                put(ENTITY_API_URL)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(groceryItem))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the GroceryItem in the database
        List<GroceryItem> groceryItemList = groceryItemRepository.findAll();
        assertThat(groceryItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateGroceryItemWithPatch() throws Exception {
        // Initialize the database
        groceryItemRepository.saveAndFlush(groceryItem);

        int databaseSizeBeforeUpdate = groceryItemRepository.findAll().size();

        // Update the groceryItem using partial update
        GroceryItem partialUpdatedGroceryItem = new GroceryItem();
        partialUpdatedGroceryItem.setId(groceryItem.getId());

        partialUpdatedGroceryItem.quantity(UPDATED_QUANTITY);

        restGroceryItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGroceryItem.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGroceryItem))
            )
            .andExpect(status().isOk());

        // Validate the GroceryItem in the database
        List<GroceryItem> groceryItemList = groceryItemRepository.findAll();
        assertThat(groceryItemList).hasSize(databaseSizeBeforeUpdate);
        GroceryItem testGroceryItem = groceryItemList.get(groceryItemList.size() - 1);
        assertThat(testGroceryItem.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testGroceryItem.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    }

    @Test
    @Transactional
    void fullUpdateGroceryItemWithPatch() throws Exception {
        // Initialize the database
        groceryItemRepository.saveAndFlush(groceryItem);

        int databaseSizeBeforeUpdate = groceryItemRepository.findAll().size();

        // Update the groceryItem using partial update
        GroceryItem partialUpdatedGroceryItem = new GroceryItem();
        partialUpdatedGroceryItem.setId(groceryItem.getId());

        partialUpdatedGroceryItem.quantity(UPDATED_QUANTITY).description(UPDATED_DESCRIPTION);

        restGroceryItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedGroceryItem.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedGroceryItem))
            )
            .andExpect(status().isOk());

        // Validate the GroceryItem in the database
        List<GroceryItem> groceryItemList = groceryItemRepository.findAll();
        assertThat(groceryItemList).hasSize(databaseSizeBeforeUpdate);
        GroceryItem testGroceryItem = groceryItemList.get(groceryItemList.size() - 1);
        assertThat(testGroceryItem.getQuantity()).isEqualTo(UPDATED_QUANTITY);
        assertThat(testGroceryItem.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    }

    @Test
    @Transactional
    void patchNonExistingGroceryItem() throws Exception {
        int databaseSizeBeforeUpdate = groceryItemRepository.findAll().size();
        groceryItem.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restGroceryItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, groceryItem.getId())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(groceryItem))
            )
            .andExpect(status().isBadRequest());

        // Validate the GroceryItem in the database
        List<GroceryItem> groceryItemList = groceryItemRepository.findAll();
        assertThat(groceryItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchGroceryItem() throws Exception {
        int databaseSizeBeforeUpdate = groceryItemRepository.findAll().size();
        groceryItem.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGroceryItemMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(groceryItem))
            )
            .andExpect(status().isBadRequest());

        // Validate the GroceryItem in the database
        List<GroceryItem> groceryItemList = groceryItemRepository.findAll();
        assertThat(groceryItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamGroceryItem() throws Exception {
        int databaseSizeBeforeUpdate = groceryItemRepository.findAll().size();
        groceryItem.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restGroceryItemMockMvc
            .perform(
                patch(ENTITY_API_URL)
                    .with(csrf())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(groceryItem))
            )
            .andExpect(status().isMethodNotAllowed());

        // Validate the GroceryItem in the database
        List<GroceryItem> groceryItemList = groceryItemRepository.findAll();
        assertThat(groceryItemList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteGroceryItem() throws Exception {
        // Initialize the database
        groceryItemRepository.saveAndFlush(groceryItem);

        int databaseSizeBeforeDelete = groceryItemRepository.findAll().size();

        // Delete the groceryItem
        restGroceryItemMockMvc
            .perform(delete(ENTITY_API_URL_ID, groceryItem.getId()).with(csrf()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<GroceryItem> groceryItemList = groceryItemRepository.findAll();
        assertThat(groceryItemList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
