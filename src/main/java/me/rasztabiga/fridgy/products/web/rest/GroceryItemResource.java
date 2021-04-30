package me.rasztabiga.fridgy.products.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import me.rasztabiga.fridgy.products.domain.GroceryItem;
import me.rasztabiga.fridgy.products.repository.GroceryItemRepository;
import me.rasztabiga.fridgy.products.repository.UserRepository;
import me.rasztabiga.fridgy.products.service.GroceryItemService;
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

    private final UserRepository userRepository;

    public GroceryItemResource(
        GroceryItemService groceryItemService,
        GroceryItemRepository groceryItemRepository,
        UserRepository userRepository
    ) {
        this.groceryItemService = groceryItemService;
        this.groceryItemRepository = groceryItemRepository;
        this.userRepository = userRepository;
    }

    /**
     * {@code POST  /grocery-items} : Create a new groceryItem.
     *
     * @param groceryItem the groceryItem to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new groceryItem, or with status {@code 400 (Bad Request)} if the groceryItem has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/grocery-items")
    public ResponseEntity<GroceryItem> createGroceryItem(@RequestBody GroceryItem groceryItem) throws URISyntaxException {
        log.debug("REST request to save GroceryItem : {}", groceryItem);
        if (groceryItem.getId() != null) {
            throw new BadRequestAlertException("A new groceryItem cannot already have an ID", ENTITY_NAME, "idexists");
        }
        if (groceryItem.getUser() != null) {
            // Save user in case it's new and only exists in gateway
            userRepository.save(groceryItem.getUser());
        }
        GroceryItem result = groceryItemService.save(groceryItem);
        return ResponseEntity
            .created(new URI("/api/grocery-items/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * {@code PUT  /grocery-items/:id} : Updates an existing groceryItem.
     *
     * @param id the id of the groceryItem to save.
     * @param groceryItem the groceryItem to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated groceryItem,
     * or with status {@code 400 (Bad Request)} if the groceryItem is not valid,
     * or with status {@code 500 (Internal Server Error)} if the groceryItem couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/grocery-items/{id}")
    public ResponseEntity<GroceryItem> updateGroceryItem(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody GroceryItem groceryItem
    ) throws URISyntaxException {
        log.debug("REST request to update GroceryItem : {}, {}", id, groceryItem);
        if (groceryItem.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, groceryItem.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!groceryItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        if (groceryItem.getUser() != null) {
            // Save user in case it's new and only exists in gateway
            userRepository.save(groceryItem.getUser());
        }
        GroceryItem result = groceryItemService.save(groceryItem);
        return ResponseEntity
            .ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, groceryItem.getId().toString()))
            .body(result);
    }

    /**
     * {@code PATCH  /grocery-items/:id} : Partial updates given fields of an existing groceryItem, field will ignore if it is null
     *
     * @param id the id of the groceryItem to save.
     * @param groceryItem the groceryItem to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated groceryItem,
     * or with status {@code 400 (Bad Request)} if the groceryItem is not valid,
     * or with status {@code 404 (Not Found)} if the groceryItem is not found,
     * or with status {@code 500 (Internal Server Error)} if the groceryItem couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/grocery-items/{id}", consumes = "application/merge-patch+json")
    public ResponseEntity<GroceryItem> partialUpdateGroceryItem(
        @PathVariable(value = "id", required = false) final Long id,
        @RequestBody GroceryItem groceryItem
    ) throws URISyntaxException {
        log.debug("REST request to partial update GroceryItem partially : {}, {}", id, groceryItem);
        if (groceryItem.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, groceryItem.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!groceryItemRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        if (groceryItem.getUser() != null) {
            // Save user in case it's new and only exists in gateway
            userRepository.save(groceryItem.getUser());
        }

        Optional<GroceryItem> result = groceryItemService.partialUpdate(groceryItem);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, groceryItem.getId().toString())
        );
    }

    /**
     * {@code GET  /grocery-items} : get all the groceryItems.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of groceryItems in body.
     */
    @GetMapping("/grocery-items")
    public ResponseEntity<List<GroceryItem>> getAllGroceryItems(Pageable pageable) {
        log.debug("REST request to get a page of GroceryItems");
        Page<GroceryItem> page = groceryItemService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /grocery-items/:id} : get the "id" groceryItem.
     *
     * @param id the id of the groceryItem to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the groceryItem, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/grocery-items/{id}")
    public ResponseEntity<GroceryItem> getGroceryItem(@PathVariable Long id) {
        log.debug("REST request to get GroceryItem : {}", id);
        Optional<GroceryItem> groceryItem = groceryItemService.findOne(id);
        return ResponseUtil.wrapOrNotFound(groceryItem);
    }

    /**
     * {@code DELETE  /grocery-items/:id} : delete the "id" groceryItem.
     *
     * @param id the id of the groceryItem to delete.
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
