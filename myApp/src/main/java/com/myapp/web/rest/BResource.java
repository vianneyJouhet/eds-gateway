package com.myapp.web.rest;

import com.myapp.domain.B;
import com.myapp.repository.BRepository;
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
 * REST controller for managing {@link com.myapp.domain.B}.
 */
@RestController
@RequestMapping("/api")
@Transactional
public class BResource {

    private final Logger log = LoggerFactory.getLogger(BResource.class);

    private static final String ENTITY_NAME = "b";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final BRepository bRepository;

    public BResource(BRepository bRepository) {
        this.bRepository = bRepository;
    }

    /**
     * {@code POST  /bs} : Create a new b.
     *
     * @param b the b to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new b, or with status {@code 400 (Bad Request)} if the b has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/bs")
    public Mono<ResponseEntity<B>> createB(@RequestBody B b) throws URISyntaxException {
        log.debug("REST request to save B : {}", b);
        if (b.getId() != null) {
            throw new BadRequestAlertException("A new b cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return bRepository
            .save(b)
            .map(
                result -> {
                    try {
                        return ResponseEntity
                            .created(new URI("/api/bs/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result);
                    } catch (URISyntaxException e) {
                        throw new RuntimeException(e);
                    }
                }
            );
    }

    /**
     * {@code PUT  /bs/:id} : Updates an existing b.
     *
     * @param id the id of the b to save.
     * @param b the b to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated b,
     * or with status {@code 400 (Bad Request)} if the b is not valid,
     * or with status {@code 500 (Internal Server Error)} if the b couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/bs/{id}")
    public Mono<ResponseEntity<B>> updateB(@PathVariable(value = "id", required = false) final Long id, @RequestBody B b)
        throws URISyntaxException {
        log.debug("REST request to update B : {}, {}", id, b);
        if (b.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, b.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return bRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    return bRepository
                        .save(b)
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
     * {@code PATCH  /bs/:id} : Partial updates given fields of an existing b, field will ignore if it is null
     *
     * @param id the id of the b to save.
     * @param b the b to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated b,
     * or with status {@code 400 (Bad Request)} if the b is not valid,
     * or with status {@code 404 (Not Found)} if the b is not found,
     * or with status {@code 500 (Internal Server Error)} if the b couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/bs/{id}", consumes = "application/merge-patch+json")
    public Mono<ResponseEntity<B>> partialUpdateB(@PathVariable(value = "id", required = false) final Long id, @RequestBody B b)
        throws URISyntaxException {
        log.debug("REST request to partial update B partially : {}, {}", id, b);
        if (b.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, b.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return bRepository
            .existsById(id)
            .flatMap(
                exists -> {
                    if (!exists) {
                        return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                    }

                    Mono<B> result = bRepository
                        .findById(b.getId())
                        .map(
                            existingB -> {
                                return existingB;
                            }
                        )
                        .flatMap(bRepository::save);

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
     * {@code GET  /bs} : get all the bS.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of bS in body.
     */
    @GetMapping("/bs")
    public Mono<List<B>> getAllBS() {
        log.debug("REST request to get all BS");
        return bRepository.findAll().collectList();
    }

    /**
     * {@code GET  /bs} : get all the bS as a stream.
     * @return the {@link Flux} of bS.
     */
    @GetMapping(value = "/bs", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<B> getAllBSAsStream() {
        log.debug("REST request to get all BS as a stream");
        return bRepository.findAll();
    }

    /**
     * {@code GET  /bs/:id} : get the "id" b.
     *
     * @param id the id of the b to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the b, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/bs/{id}")
    public Mono<ResponseEntity<B>> getB(@PathVariable Long id) {
        log.debug("REST request to get B : {}", id);
        Mono<B> b = bRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(b);
    }

    /**
     * {@code DELETE  /bs/:id} : delete the "id" b.
     *
     * @param id the id of the b to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/bs/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public Mono<ResponseEntity<Void>> deleteB(@PathVariable Long id) {
        log.debug("REST request to delete B : {}", id);
        return bRepository
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
