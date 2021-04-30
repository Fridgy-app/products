package me.rasztabiga.fridgy.products.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import me.rasztabiga.fridgy.products.domain.RecipeIngredient;
import me.rasztabiga.fridgy.products.repository.RecipeIngredientRepository;
import me.rasztabiga.fridgy.products.service.RecipeIngredientService;
import me.rasztabiga.fridgy.products.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link me.rasztabiga.fridgy.products.domain.RecipeIngredient}.
 */
@RestController
@RequestMapping("/api")
public class RecipeIngredientResource {

    private final Logger log = LoggerFactory.getLogger(RecipeIngredientResource.class);

    private static final String ENTITY_NAME = "productsRecipeIngredient";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final RecipeIngredientService recipeIngredientService;

    private final RecipeIngredientRepository recipeIngredientRepository;

    public RecipeIngredientResource(
        RecipeIngredientService recipeIngredientService,
        RecipeIngredientRepository recipeIngredientRepository
    ) {
        this.recipeIngredientService = recipeIngredientService;
        this.recipeIngredientRepository = recipeIngredientRepository;
    }

    /**
     * {@code POST  /recipe-ingredients} : Create a new recipeIngredient.
     *
     * @param recipeIngredient the recipeIngredient to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new recipeIngredient, or with status {@code 400 (Bad Request)} if the recipeIngredient has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/recipe-ingredients")
    public ResponseEntity<RecipeIngredient> createRecipeIngredient(@RequestBody RecipeIngredient recipeIngredient)
        throws URISyntaxException {
        log.debug("REST request to save RecipeIngredient : {}", recipeIngredient);
        if (recipeIngredient.getId() != null) {
            throw new BadRequestAlertException("A new recipeIngredient cannot already have an ID", ENTITY_NAME, "idexists");
        }
        RecipeIngredient result = recipeIngredientService.save(recipeIngredient);
        return ResponseEntity
            .created(new URI("/api/recipe-ingredients/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /recipe-ingredients/:id} : Updates an existing recipeIngredient.
     *
     * @param id the id of the recipeIngredient to save.
     * @param recipeIngredient the recipeIngredient to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated recipeIngredient,
     * or with status {@code 400 (Bad Request)} if the recipeIngredient is not valid,
     * or with status {@code 500 (Internal Server Error)} if the recipeIngredient couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/recipe-ingredients/{id}")
    public ResponseEntity<RecipeIngredient> updateRecipeIngredient(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody RecipeIngredient recipeIngredient
    ) throws URISyntaxException {
        log.debug("REST request to update RecipeIngredient : {}, {}", id, recipeIngredient);
        if (recipeIngredient.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, recipeIngredient.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!recipeIngredientRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        RecipeIngredient result = recipeIngredientService.save(recipeIngredient);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, recipeIngredient.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /recipe-ingredients/:id} : Partial updates given fields of an existing recipeIngredient, field will ignore if it is null
     *
     * @param id the id of the recipeIngredient to save.
     * @param recipeIngredient the recipeIngredient to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated recipeIngredient,
     * or with status {@code 400 (Bad Request)} if the recipeIngredient is not valid,
     * or with status {@code 404 (Not Found)} if the recipeIngredient is not found,
     * or with status {@code 500 (Internal Server Error)} if the recipeIngredient couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/recipe-ingredients/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<RecipeIngredient> partialUpdateRecipeIngredient(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody RecipeIngredient recipeIngredient
    ) throws URISyntaxException {
        log.debug("REST request to partial update RecipeIngredient partially : {}, {}", id, recipeIngredient);
        if (recipeIngredient.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, recipeIngredient.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!recipeIngredientRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<RecipeIngredient> result = recipeIngredientService.partialUpdate(recipeIngredient);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, recipeIngredient.getId().toString())
        );
    }

    /**
     * {@code GET  /recipe-ingredients} : get all the recipeIngredients.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of recipeIngredients in body.
     */
    @GetMapping("/recipe-ingredients")
    public List<RecipeIngredient> getAllRecipeIngredients() {
        log.debug("REST request to get all RecipeIngredients");
        return recipeIngredientService.findAll();
    }

    /**
     * {@code GET  /recipe-ingredients/:id} : get the "id" recipeIngredient.
     *
     * @param id the id of the recipeIngredient to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the recipeIngredient, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/recipe-ingredients/{id}")
    public ResponseEntity<RecipeIngredient> getRecipeIngredient(@PathVariable Long id) {
        log.debug("REST request to get RecipeIngredient : {}", id);
        Optional<RecipeIngredient> recipeIngredient = recipeIngredientService.findOne(id);
        return ResponseUtil.wrapOrNotFound(recipeIngredient);
    }

    /**
     * {@code DELETE  /recipe-ingredients/:id} : delete the "id" recipeIngredient.
     *
     * @param id the id of the recipeIngredient to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/recipe-ingredients/{id}")
    public ResponseEntity<Void> deleteRecipeIngredient(@PathVariable Long id) {
        log.debug("REST request to delete RecipeIngredient : {}", id);
        recipeIngredientService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
