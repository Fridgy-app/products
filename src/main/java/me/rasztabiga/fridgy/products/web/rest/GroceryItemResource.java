package me.rasztabiga.fridgy.products.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import me.rasztabiga.fridgy.products.repository.GroceryItemRepository;
import me.rasztabiga.fridgy.products.service.GroceryItemService;
import me.rasztabiga.fridgy.products.service.dto.GroceryItemDTO;
import me.rasztabiga.fridgy.products.web.rest.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link me.rasztabiga.fridgy.products.domain.GroceryItem}.
 */
@RestController
@RequestMapping("/api")
public class GroceryItemResource {

    private final Logger log = LoggerFactory.getLogger(GroceryItemResource.class);

    private static final String ENTITY_NAME = "productsGroceryItem";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final GroceryItemService groceryItemService;

    private final GroceryItemRepository groceryItemRepository;

    public GroceryItemResource(GroceryItemService groceryItemService, GroceryItemRepository groceryItemRepository) {
        this.groceryItemService = groceryItemService;
        this.groceryItemRepository = groceryItemRepository;
    }

    /**
     * {@code POST  /grocery-items} : Create a new groceryItem.
     *
     * @param groceryItemDTO the groceryItemDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new groceryItemDTO, or with status {@code 400 (Bad Request)} if the groceryItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/grocery-items")
    public ResponseEntity<GroceryItemDTO> createGroceryItem(@RequestBody GroceryItemDTO groceryItemDTO) throws URISyntaxException {
        log.debug("REST request to save GroceryItem : {}", groceryItemDTO);
        if (groceryItemDTO.getId() != null) {
            throw new BadRequestAlertException("A new groceryItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        GroceryItemDTO result = groceryItemService.save(groceryItemDTO);
        return ResponseEntity
            .created(new URI("/api/grocery-items/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /grocery-items/:id} : Updates an existing groceryItem.
     *
     * @param id the id of the groceryItemDTO to save.
     * @param groceryItemDTO the groceryItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated groceryItemDTO,
     * or with status {@code 400 (Bad Request)} if the groceryItemDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the groceryItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/grocery-items/{id}")
    public ResponseEntity<GroceryItemDTO> updateGroceryItem(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody GroceryItemDTO groceryItemDTO
    ) throws URISyntaxException {
        log.debug("REST request to update GroceryItem : {}, {}", id, groceryItemDTO);
        if (groceryItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, groceryItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!groceryItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        GroceryItemDTO result = groceryItemService.save(groceryItemDTO);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, groceryItemDTO.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /grocery-items/:id} : Partial updates given fields of an existing groceryItem, field will ignore if it is null
     *
     * @param id the id of the groceryItemDTO to save.
     * @param groceryItemDTO the groceryItemDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated groceryItemDTO,
     * or with status {@code 400 (Bad Request)} if the groceryItemDTO is not valid,
     * or with status {@code 404 (Not Found)} if the groceryItemDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the groceryItemDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/grocery-items/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<GroceryItemDTO> partialUpdateGroceryItem(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody GroceryItemDTO groceryItemDTO
    ) throws URISyntaxException {
        log.debug("REST request to partial update GroceryItem partially : {}, {}", id, groceryItemDTO);
        if (groceryItemDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, groceryItemDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!groceryItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<GroceryItemDTO> result = groceryItemService.partialUpdate(groceryItemDTO);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, groceryItemDTO.getId().toString())
        );
    }

    /**
     * {@code GET  /grocery-items} : get all the groceryItems.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of groceryItems in body.
     */
    @GetMapping("/grocery-items")
    public ResponseEntity<List<GroceryItemDTO>> getAllGroceryItems(Pageable pageable) {
        log.debug("REST request to get a page of GroceryItems");
        Page<GroceryItemDTO> page = groceryItemService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /grocery-items/:id} : get the "id" groceryItem.
     *
     * @param id the id of the groceryItemDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the groceryItemDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/grocery-items/{id}")
    public ResponseEntity<GroceryItemDTO> getGroceryItem(@PathVariable Long id) {
        log.debug("REST request to get GroceryItem : {}", id);
        Optional<GroceryItemDTO> groceryItemDTO = groceryItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(groceryItemDTO);
    }

    /**
     * {@code DELETE  /grocery-items/:id} : delete the "id" groceryItem.
     *
     * @param id the id of the groceryItemDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/grocery-items/{id}")
    public ResponseEntity<Void> deleteGroceryItem(@PathVariable Long id) {
        log.debug("REST request to delete GroceryItem : {}", id);
        groceryItemService.delete(id);
        return ResponseEntity
            .noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
