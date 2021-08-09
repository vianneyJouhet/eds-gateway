package com.myapp.web.rest;

import com.myapp.domain.C;
import com.myapp.repository.CRepository;
import com.myapp.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.myapp.domain.C}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class CResource {

    private final Logger log = LoggerFactory.getLogger(CResource.class);

    private static final String ENTITY_NAME = "c";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final CRepository cRepository;

    public CResource(CRepository cRepository) {
        this.cRepository = cRepository;
    }

    /**
     * {@code POST  /cs} : Create a new c.
     *
     * @param c the c to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new c, or with status {@code 400 (Bad Request)} if the c has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/cs")
    public Mono<ResponseEntity<C>> createC(@RequestBody C c) throws URISyntaxException {
        log.debug("REST request to save C : {}", c);
        if (c.getId() != null) {
            throw new BadRequestAlertException("A new c cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return cRepository
            .save(c)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/cs/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /cs/:id} : Updates an existing c.
     *
     * @param id the id of the c to save.
     * @param c the c to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated c,
     * or with status {@code 400 (Bad Request)} if the c is not valid,
     * or with status {@code 500 (Internal Server Error)} if the c couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/cs/{id}")
    public Mono<ResponseEntity<C>> updateC(@PathVariable(value = "id", required = false) final Long id, @RequestBody C c)
        throws URISyntaxException {
        log.debug("REST request to update C : {}, {}", id, c);
        if (c.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, c.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return cRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return cRepository
                        .save(c)
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map(
                            result ->
                                ResponseEntity
                                    .ok()
                                    .headers(
                                        HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString())
                                    )
                                    .body(result)
                        );
                }
            );
    }

    /**
     * {@code PATCH  /cs/:id} : Partial updates given fields of an existing c, field will ignore if it is null
     *
     * @param id the id of the c to save.
     * @param c the c to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated c,
     * or with status {@code 400 (Bad Request)} if the c is not valid,
     * or with status {@code 404 (Not Found)} if the c is not found,
     * or with status {@code 500 (Internal Server Error)} if the c couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/cs/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<C>> partialUpdateC(@PathVariable(value = "id", required = false) final Long id, @RequestBody C c)
        throws URISyntaxException {
        log.debug("REST request to partial update C partially : {}, {}", id, c);
        if (c.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, c.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return cRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<C> result = cRepository
                        .findById(c.getId())
                        .map(
                            existingC -> {
                                return existingC;
                            }
                        )
                        .flatMap(cRepository::save);

                    return result
                        .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                        .map(
                            res ->
                                ResponseEntity
                                    .ok()
                                    .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                                    .body(res)
                        );
                }
            );
    }

    /**
     * {@code GET  /cs} : get all the cS.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of cS in body.
     */
    @GetMapping("/cs")
    public Mono<List<C>> getAllCS() {
        log.debug("REST request to get all CS");
        return cRepository.findAll().collectList();
    }

    /**
     * {@code GET  /cs} : get all the cS as a stream.
     * @return the {@link Flux} of cS.
     */
    @GetMapping(value = "/cs", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<C> getAllCSAsStream() {
        log.debug("REST request to get all CS as a stream");
        return cRepository.findAll();
    }

    /**
     * {@code GET  /cs/:id} : get the "id" c.
     *
     * @param id the id of the c to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the c, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/cs/{id}")
    public Mono<ResponseEntity<C>> getC(@PathVariable Long id) {
        log.debug("REST request to get C : {}", id);
        Mono<C> c = cRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(c);
    }

    /**
     * {@code DELETE  /cs/:id} : delete the "id" c.
     *
     * @param id the id of the c to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/cs/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteC(@PathVariable Long id) {
        log.debug("REST request to delete C : {}", id);
        return cRepository
            .deleteById(id)
            .map(
                result ->
                    ResponseEntity
                        .noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
            );
    }
}
