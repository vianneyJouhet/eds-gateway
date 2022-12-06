package fr.chu.bordeaux.eds.web.rest;

import fr.chu.bordeaux.eds.domain.EDSApplication;
import fr.chu.bordeaux.eds.repository.EDSApplicationRepository;
import fr.chu.bordeaux.eds.web.rest.errors.BadRequestAlertException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link fr.chu.bordeaux.eds.domain.EDSApplication}.
 */
@RestController
@RequestMapping("/api")
public class EDSApplicationResource {

    private final Logger log = LoggerFactory.getLogger(EDSApplicationResource.class);

    private static final String ENTITY_NAME = "eDSApplication";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EDSApplicationRepository eDSApplicationRepository;

    public EDSApplicationResource(EDSApplicationRepository eDSApplicationRepository) {
        this.eDSApplicationRepository = eDSApplicationRepository;
    }

    /**
     * {@code POST  /eds-applications} : Create a new eDSApplication.
     *
     * @param eDSApplication the eDSApplication to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new eDSApplication, or with status {@code 400 (Bad Request)} if the eDSApplication has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/eds-applications")
    public Mono<ResponseEntity<EDSApplication>> createEDSApplication(@Valid @RequestBody EDSApplication eDSApplication)
        throws URISyntaxException {
        log.debug("REST request to save EDSApplication : {}", eDSApplication);
        if (eDSApplication.getId() != null) {
            throw new BadRequestAlertException("A new eDSApplication cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return eDSApplicationRepository
            .save(eDSApplication)
            .map(result -> {
                try {
                    return ResponseEntity
                        .created(new URI("/api/eds-applications/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /eds-applications/:id} : Updates an existing eDSApplication.
     *
     * @param id the id of the eDSApplication to save.
     * @param eDSApplication the eDSApplication to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated eDSApplication,
     * or with status {@code 400 (Bad Request)} if the eDSApplication is not valid,
     * or with status {@code 500 (Internal Server Error)} if the eDSApplication couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/eds-applications/{id}")
    public Mono<ResponseEntity<EDSApplication>> updateEDSApplication(
        @PathVariable(value = "id", required = false) final String id,
        @Valid @RequestBody EDSApplication eDSApplication
    ) throws URISyntaxException {
        log.debug("REST request to update EDSApplication : {}, {}", id, eDSApplication);
        if (eDSApplication.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, eDSApplication.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return eDSApplicationRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return eDSApplicationRepository
                    .save(eDSApplication)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /eds-applications/:id} : Partial updates given fields of an existing eDSApplication, field will ignore if it is null
     *
     * @param id the id of the eDSApplication to save.
     * @param eDSApplication the eDSApplication to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated eDSApplication,
     * or with status {@code 400 (Bad Request)} if the eDSApplication is not valid,
     * or with status {@code 404 (Not Found)} if the eDSApplication is not found,
     * or with status {@code 500 (Internal Server Error)} if the eDSApplication couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/eds-applications/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<EDSApplication>> partialUpdateEDSApplication(
        @PathVariable(value = "id", required = false) final String id,
        @NotNull @RequestBody EDSApplication eDSApplication
    ) throws URISyntaxException {
        log.debug("REST request to partial update EDSApplication partially : {}, {}", id, eDSApplication);
        if (eDSApplication.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, eDSApplication.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return eDSApplicationRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<EDSApplication> result = eDSApplicationRepository
                    .findById(eDSApplication.getId())
                    .map(existingEDSApplication -> {
                        if (eDSApplication.getName() != null) {
                            existingEDSApplication.setName(eDSApplication.getName());
                        }
                        if (eDSApplication.getLogo() != null) {
                            existingEDSApplication.setLogo(eDSApplication.getLogo());
                        }
                        if (eDSApplication.getLogoContentType() != null) {
                            existingEDSApplication.setLogoContentType(eDSApplication.getLogoContentType());
                        }
                        if (eDSApplication.getLink() != null) {
                            existingEDSApplication.setLink(eDSApplication.getLink());
                        }
                        if (eDSApplication.getDescription() != null) {
                            existingEDSApplication.setDescription(eDSApplication.getDescription());
                        }
                        if (eDSApplication.getCategory() != null) {
                            existingEDSApplication.setCategory(eDSApplication.getCategory());
                        }
                        if (eDSApplication.getAuthorizedRole() != null) {
                            existingEDSApplication.setAuthorizedRole(eDSApplication.getAuthorizedRole());
                        }
                        if (eDSApplication.getNeedAuth() != null) {
                            existingEDSApplication.setNeedAuth(eDSApplication.getNeedAuth());
                        }
                        if (eDSApplication.getDefaultHidden() != null) {
                            existingEDSApplication.setDefaultHidden(eDSApplication.getDefaultHidden());
                        }

                        return existingEDSApplication;
                    })
                    .flatMap(eDSApplicationRepository::save);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity
                            .ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /eds-applications} : get all the eDSApplications.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of eDSApplications in body.
     */
    @GetMapping("/eds-applications")
    public Mono<List<EDSApplication>> getAllEDSApplications() {
        log.debug("REST request to get all EDSApplications");
        return eDSApplicationRepository.findAll().collectList();
    }

    /**
     * {@code GET  /eds-applications} : get all the eDSApplications as a stream.
     * @return the {@link Flux} of eDSApplications.
     */
    @GetMapping(value = "/eds-applications", produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<EDSApplication> getAllEDSApplicationsAsStream() {
        log.debug("REST request to get all EDSApplications as a stream");
        return eDSApplicationRepository.findAll();
    }

    /**
     * {@code GET  /eds-applications/:id} : get the "id" eDSApplication.
     *
     * @param id the id of the eDSApplication to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the eDSApplication, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/eds-applications/{id}")
    public Mono<ResponseEntity<EDSApplication>> getEDSApplication(@PathVariable String id) {
        log.debug("REST request to get EDSApplication : {}", id);
        Mono<EDSApplication> eDSApplication = eDSApplicationRepository.findById(id);
        return ResponseUtil.wrapOrNotFound(eDSApplication);
    }

    /**
     * {@code DELETE  /eds-applications/:id} : delete the "id" eDSApplication.
     *
     * @param id the id of the eDSApplication to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/eds-applications/{id}")
    public Mono<ResponseEntity<Void>> deleteEDSApplication(@PathVariable String id) {
        log.debug("REST request to delete EDSApplication : {}", id);
        return eDSApplicationRepository
            .deleteById(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent().headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id)).build()
                )
            );
    }
}
