package com.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.myapp.IntegrationTest;
import com.myapp.domain.D;
import com.myapp.repository.DRepository;
import com.myapp.service.EntityManager;
import java.time.Duration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link DResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class DResourceIT {

    private static final String ENTITY_API_URL = "/api/ds";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private DRepository dRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private D d;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static D createEntity(EntityManager em) {
        D d = new D();
        return d;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static D createUpdatedEntity(EntityManager em) {
        D d = new D();
        return d;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(D.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @AfterEach
    public void cleanup() {
        deleteEntities(em);
    }

    @BeforeEach
    public void initTest() {
        deleteEntities(em);
        d = createEntity(em);
    }

    @Test
    void createD() throws Exception {
        int databaseSizeBeforeCreate = dRepository.findAll().collectList().block().size();
        // Create the D
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(d))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the D in the database
        List<D> dList = dRepository.findAll().collectList().block();
        assertThat(dList).hasSize(databaseSizeBeforeCreate + 1);
        D testD = dList.get(dList.size() - 1);
    }

    @Test
    void createDWithExistingId() throws Exception {
        // Create the D with an existing ID
        d.setId(1L);

        int databaseSizeBeforeCreate = dRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(d))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the D in the database
        List<D> dList = dRepository.findAll().collectList().block();
        assertThat(dList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllDSAsStream() {
        // Initialize the database
        dRepository.save(d).block();

        List<D> dList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(D.class)
            .getResponseBody()
            .filter(d::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(dList).isNotNull();
        assertThat(dList).hasSize(1);
        D testD = dList.get(0);
    }

    @Test
    void getAllDS() {
        // Initialize the database
        dRepository.save(d).block();

        // Get all the dList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(d.getId().intValue()));
    }

    @Test
    void getD() {
        // Initialize the database
        dRepository.save(d).block();

        // Get the d
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, d.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(d.getId().intValue()));
    }

    @Test
    void getNonExistingD() {
        // Get the d
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewD() throws Exception {
        // Initialize the database
        dRepository.save(d).block();

        int databaseSizeBeforeUpdate = dRepository.findAll().collectList().block().size();

        // Update the d
        D updatedD = dRepository.findById(d.getId()).block();

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedD.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedD))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the D in the database
        List<D> dList = dRepository.findAll().collectList().block();
        assertThat(dList).hasSize(databaseSizeBeforeUpdate);
        D testD = dList.get(dList.size() - 1);
    }

    @Test
    void putNonExistingD() throws Exception {
        int databaseSizeBeforeUpdate = dRepository.findAll().collectList().block().size();
        d.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, d.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(d))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the D in the database
        List<D> dList = dRepository.findAll().collectList().block();
        assertThat(dList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchD() throws Exception {
        int databaseSizeBeforeUpdate = dRepository.findAll().collectList().block().size();
        d.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(d))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the D in the database
        List<D> dList = dRepository.findAll().collectList().block();
        assertThat(dList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamD() throws Exception {
        int databaseSizeBeforeUpdate = dRepository.findAll().collectList().block().size();
        d.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(d))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the D in the database
        List<D> dList = dRepository.findAll().collectList().block();
        assertThat(dList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateDWithPatch() throws Exception {
        // Initialize the database
        dRepository.save(d).block();

        int databaseSizeBeforeUpdate = dRepository.findAll().collectList().block().size();

        // Update the d using partial update
        D partialUpdatedD = new D();
        partialUpdatedD.setId(d.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedD.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedD))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the D in the database
        List<D> dList = dRepository.findAll().collectList().block();
        assertThat(dList).hasSize(databaseSizeBeforeUpdate);
        D testD = dList.get(dList.size() - 1);
    }

    @Test
    void fullUpdateDWithPatch() throws Exception {
        // Initialize the database
        dRepository.save(d).block();

        int databaseSizeBeforeUpdate = dRepository.findAll().collectList().block().size();

        // Update the d using partial update
        D partialUpdatedD = new D();
        partialUpdatedD.setId(d.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedD.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedD))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the D in the database
        List<D> dList = dRepository.findAll().collectList().block();
        assertThat(dList).hasSize(databaseSizeBeforeUpdate);
        D testD = dList.get(dList.size() - 1);
    }

    @Test
    void patchNonExistingD() throws Exception {
        int databaseSizeBeforeUpdate = dRepository.findAll().collectList().block().size();
        d.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, d.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(d))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the D in the database
        List<D> dList = dRepository.findAll().collectList().block();
        assertThat(dList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchD() throws Exception {
        int databaseSizeBeforeUpdate = dRepository.findAll().collectList().block().size();
        d.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(d))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the D in the database
        List<D> dList = dRepository.findAll().collectList().block();
        assertThat(dList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamD() throws Exception {
        int databaseSizeBeforeUpdate = dRepository.findAll().collectList().block().size();
        d.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(d))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the D in the database
        List<D> dList = dRepository.findAll().collectList().block();
        assertThat(dList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteD() {
        // Initialize the database
        dRepository.save(d).block();

        int databaseSizeBeforeDelete = dRepository.findAll().collectList().block().size();

        // Delete the d
        webTestClient.delete().uri(ENTITY_API_URL_ID, d.getId()).accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isNoContent();

        // Validate the database contains one less item
        List<D> dList = dRepository.findAll().collectList().block();
        assertThat(dList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
