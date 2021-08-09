package com.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;

import com.myapp.IntegrationTest;
import com.myapp.domain.B;
import com.myapp.repository.BRepository;
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
 * Integration tests for the {@link BResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient
@WithMockUser
class BResourceIT {

    private static final String ENTITY_API_URL = "/api/bs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private BRepository bRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private B b;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static B createEntity(EntityManager em) {
        B b = new B();
        return b;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static B createUpdatedEntity(EntityManager em) {
        B b = new B();
        return b;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(B.class).block();
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
        b = createEntity(em);
    }

    @Test
    void createB() throws Exception {
        int databaseSizeBeforeCreate = bRepository.findAll().collectList().block().size();
        // Create the B
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(b))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the B in the database
        List<B> bList = bRepository.findAll().collectList().block();
        assertThat(bList).hasSize(databaseSizeBeforeCreate + 1);
        B testB = bList.get(bList.size() - 1);
    }

    @Test
    void createBWithExistingId() throws Exception {
        // Create the B with an existing ID
        b.setId(1L);

        int databaseSizeBeforeCreate = bRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(b))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the B in the database
        List<B> bList = bRepository.findAll().collectList().block();
        assertThat(bList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllBSAsStream() {
        // Initialize the database
        bRepository.save(b).block();

        List<B> bList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(B.class)
            .getResponseBody()
            .filter(b::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(bList).isNotNull();
        assertThat(bList).hasSize(1);
        B testB = bList.get(0);
    }

    @Test
    void getAllBS() {
        // Initialize the database
        bRepository.save(b).block();

        // Get all the bList
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
            .value(hasItem(b.getId().intValue()));
    }

    @Test
    void getB() {
        // Initialize the database
        bRepository.save(b).block();

        // Get the b
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, b.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(b.getId().intValue()));
    }

    @Test
    void getNonExistingB() {
        // Get the b
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putNewB() throws Exception {
        // Initialize the database
        bRepository.save(b).block();

        int databaseSizeBeforeUpdate = bRepository.findAll().collectList().block().size();

        // Update the b
        B updatedB = bRepository.findById(b.getId()).block();

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedB.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedB))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the B in the database
        List<B> bList = bRepository.findAll().collectList().block();
        assertThat(bList).hasSize(databaseSizeBeforeUpdate);
        B testB = bList.get(bList.size() - 1);
    }

    @Test
    void putNonExistingB() throws Exception {
        int databaseSizeBeforeUpdate = bRepository.findAll().collectList().block().size();
        b.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, b.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(b))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the B in the database
        List<B> bList = bRepository.findAll().collectList().block();
        assertThat(bList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchB() throws Exception {
        int databaseSizeBeforeUpdate = bRepository.findAll().collectList().block().size();
        b.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(b))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the B in the database
        List<B> bList = bRepository.findAll().collectList().block();
        assertThat(bList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamB() throws Exception {
        int databaseSizeBeforeUpdate = bRepository.findAll().collectList().block().size();
        b.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(b))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the B in the database
        List<B> bList = bRepository.findAll().collectList().block();
        assertThat(bList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateBWithPatch() throws Exception {
        // Initialize the database
        bRepository.save(b).block();

        int databaseSizeBeforeUpdate = bRepository.findAll().collectList().block().size();

        // Update the b using partial update
        B partialUpdatedB = new B();
        partialUpdatedB.setId(b.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedB.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedB))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the B in the database
        List<B> bList = bRepository.findAll().collectList().block();
        assertThat(bList).hasSize(databaseSizeBeforeUpdate);
        B testB = bList.get(bList.size() - 1);
    }

    @Test
    void fullUpdateBWithPatch() throws Exception {
        // Initialize the database
        bRepository.save(b).block();

        int databaseSizeBeforeUpdate = bRepository.findAll().collectList().block().size();

        // Update the b using partial update
        B partialUpdatedB = new B();
        partialUpdatedB.setId(b.getId());

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedB.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedB))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the B in the database
        List<B> bList = bRepository.findAll().collectList().block();
        assertThat(bList).hasSize(databaseSizeBeforeUpdate);
        B testB = bList.get(bList.size() - 1);
    }

    @Test
    void patchNonExistingB() throws Exception {
        int databaseSizeBeforeUpdate = bRepository.findAll().collectList().block().size();
        b.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, b.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(b))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the B in the database
        List<B> bList = bRepository.findAll().collectList().block();
        assertThat(bList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchB() throws Exception {
        int databaseSizeBeforeUpdate = bRepository.findAll().collectList().block().size();
        b.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, count.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(b))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the B in the database
        List<B> bList = bRepository.findAll().collectList().block();
        assertThat(bList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamB() throws Exception {
        int databaseSizeBeforeUpdate = bRepository.findAll().collectList().block().size();
        b.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(b))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the B in the database
        List<B> bList = bRepository.findAll().collectList().block();
        assertThat(bList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteB() {
        // Initialize the database
        bRepository.save(b).block();

        int databaseSizeBeforeDelete = bRepository.findAll().collectList().block().size();

        // Delete the b
        webTestClient.delete().uri(ENTITY_API_URL_ID, b.getId()).accept(MediaType.APPLICATION_JSON).exchange().expectStatus().isNoContent();

        // Validate the database contains one less item
        List<B> bList = bRepository.findAll().collectList().block();
        assertThat(bList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
