package fr.chu.bordeaux.eds.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import fr.chu.bordeaux.eds.IntegrationTest;
import fr.chu.bordeaux.eds.domain.EDSApplication;
import fr.chu.bordeaux.eds.repository.EDSApplicationRepository;
import java.time.Duration;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.Base64Utils;

/**
 * Integration tests for the {@link EDSApplicationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class EDSApplicationResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final byte[] DEFAULT_LOGO = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_LOGO = TestUtil.createByteArray(1, "1");
    private static final String DEFAULT_LOGO_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_LOGO_CONTENT_TYPE = "image/png";

    private static final String DEFAULT_LINK = "AAAAAAAAAA";
    private static final String UPDATED_LINK = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_CATEGORY = "AAAAAAAAAA";
    private static final String UPDATED_CATEGORY = "BBBBBBBBBB";

    private static final String DEFAULT_AUTHORIZED_ROLE = "AAAAAAAAAA";
    private static final String UPDATED_AUTHORIZED_ROLE = "BBBBBBBBBB";

    private static final Boolean DEFAULT_NEED_AUTH = false;
    private static final Boolean UPDATED_NEED_AUTH = true;

    private static final Boolean DEFAULT_DEFAULT_HIDDEN = false;
    private static final Boolean UPDATED_DEFAULT_HIDDEN = true;

    private static final String ENTITY_API_URL = "/api/eds-applications";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private EDSApplicationRepository eDSApplicationRepository;

    @Autowired
    private WebTestClient webTestClient;

    private EDSApplication eDSApplication;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EDSApplication createEntity() {
        EDSApplication eDSApplication = new EDSApplication()
            .name(DEFAULT_NAME)
            .logo(DEFAULT_LOGO)
            .logoContentType(DEFAULT_LOGO_CONTENT_TYPE)
            .link(DEFAULT_LINK)
            .description(DEFAULT_DESCRIPTION)
            .category(DEFAULT_CATEGORY)
            .authorizedRole(DEFAULT_AUTHORIZED_ROLE)
            .needAuth(DEFAULT_NEED_AUTH)
            .defaultHidden(DEFAULT_DEFAULT_HIDDEN);
        return eDSApplication;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EDSApplication createUpdatedEntity() {
        EDSApplication eDSApplication = new EDSApplication()
            .name(UPDATED_NAME)
            .logo(UPDATED_LOGO)
            .logoContentType(UPDATED_LOGO_CONTENT_TYPE)
            .link(UPDATED_LINK)
            .description(UPDATED_DESCRIPTION)
            .category(UPDATED_CATEGORY)
            .authorizedRole(UPDATED_AUTHORIZED_ROLE)
            .needAuth(UPDATED_NEED_AUTH)
            .defaultHidden(UPDATED_DEFAULT_HIDDEN);
        return eDSApplication;
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        eDSApplicationRepository.deleteAll().block();
        eDSApplication = createEntity();
    }

    @Test
    void createEDSApplication() throws Exception {
        int databaseSizeBeforeCreate = eDSApplicationRepository.findAll().collectList().block().size();
        // Create the EDSApplication
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(eDSApplication))
            .exchange()
            .expectStatus()
            .isCreated();

        // Validate the EDSApplication in the database
        List<EDSApplication> eDSApplicationList = eDSApplicationRepository.findAll().collectList().block();
        assertThat(eDSApplicationList).hasSize(databaseSizeBeforeCreate + 1);
        EDSApplication testEDSApplication = eDSApplicationList.get(eDSApplicationList.size() - 1);
        assertThat(testEDSApplication.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testEDSApplication.getLogo()).isEqualTo(DEFAULT_LOGO);
        assertThat(testEDSApplication.getLogoContentType()).isEqualTo(DEFAULT_LOGO_CONTENT_TYPE);
        assertThat(testEDSApplication.getLink()).isEqualTo(DEFAULT_LINK);
        assertThat(testEDSApplication.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testEDSApplication.getCategory()).isEqualTo(DEFAULT_CATEGORY);
        assertThat(testEDSApplication.getAuthorizedRole()).isEqualTo(DEFAULT_AUTHORIZED_ROLE);
        assertThat(testEDSApplication.getNeedAuth()).isEqualTo(DEFAULT_NEED_AUTH);
        assertThat(testEDSApplication.getDefaultHidden()).isEqualTo(DEFAULT_DEFAULT_HIDDEN);
    }

    @Test
    void createEDSApplicationWithExistingId() throws Exception {
        // Create the EDSApplication with an existing ID
        eDSApplication.setId("existing_id");

        int databaseSizeBeforeCreate = eDSApplicationRepository.findAll().collectList().block().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(eDSApplication))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EDSApplication in the database
        List<EDSApplication> eDSApplicationList = eDSApplicationRepository.findAll().collectList().block();
        assertThat(eDSApplicationList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void getAllEDSApplicationsAsStream() {
        // Initialize the database
        eDSApplicationRepository.save(eDSApplication).block();

        List<EDSApplication> eDSApplicationList = webTestClient
            .get()
            .uri(ENTITY_API_URL)
            .accept(MediaType.APPLICATION_NDJSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentTypeCompatibleWith(MediaType.APPLICATION_NDJSON)
            .returnResult(EDSApplication.class)
            .getResponseBody()
            .filter(eDSApplication::equals)
            .collectList()
            .block(Duration.ofSeconds(5));

        assertThat(eDSApplicationList).isNotNull();
        assertThat(eDSApplicationList).hasSize(1);
        EDSApplication testEDSApplication = eDSApplicationList.get(0);
        assertThat(testEDSApplication.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testEDSApplication.getLogo()).isEqualTo(DEFAULT_LOGO);
        assertThat(testEDSApplication.getLogoContentType()).isEqualTo(DEFAULT_LOGO_CONTENT_TYPE);
        assertThat(testEDSApplication.getLink()).isEqualTo(DEFAULT_LINK);
        assertThat(testEDSApplication.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testEDSApplication.getCategory()).isEqualTo(DEFAULT_CATEGORY);
        assertThat(testEDSApplication.getAuthorizedRole()).isEqualTo(DEFAULT_AUTHORIZED_ROLE);
        assertThat(testEDSApplication.getNeedAuth()).isEqualTo(DEFAULT_NEED_AUTH);
        assertThat(testEDSApplication.getDefaultHidden()).isEqualTo(DEFAULT_DEFAULT_HIDDEN);
    }

    @Test
    void getAllEDSApplications() {
        // Initialize the database
        eDSApplicationRepository.save(eDSApplication).block();

        // Get all the eDSApplicationList
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
            .value(hasItem(eDSApplication.getId()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].logoContentType")
            .value(hasItem(DEFAULT_LOGO_CONTENT_TYPE))
            .jsonPath("$.[*].logo")
            .value(hasItem(Base64Utils.encodeToString(DEFAULT_LOGO)))
            .jsonPath("$.[*].link")
            .value(hasItem(DEFAULT_LINK))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.[*].category")
            .value(hasItem(DEFAULT_CATEGORY))
            .jsonPath("$.[*].authorizedRole")
            .value(hasItem(DEFAULT_AUTHORIZED_ROLE))
            .jsonPath("$.[*].needAuth")
            .value(hasItem(DEFAULT_NEED_AUTH.booleanValue()))
            .jsonPath("$.[*].defaultHidden")
            .value(hasItem(DEFAULT_DEFAULT_HIDDEN.booleanValue()));
    }

    @Test
    void getEDSApplication() {
        // Initialize the database
        eDSApplicationRepository.save(eDSApplication).block();

        // Get the eDSApplication
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, eDSApplication.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(eDSApplication.getId()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.logoContentType")
            .value(is(DEFAULT_LOGO_CONTENT_TYPE))
            .jsonPath("$.logo")
            .value(is(Base64Utils.encodeToString(DEFAULT_LOGO)))
            .jsonPath("$.link")
            .value(is(DEFAULT_LINK))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION.toString()))
            .jsonPath("$.category")
            .value(is(DEFAULT_CATEGORY))
            .jsonPath("$.authorizedRole")
            .value(is(DEFAULT_AUTHORIZED_ROLE))
            .jsonPath("$.needAuth")
            .value(is(DEFAULT_NEED_AUTH.booleanValue()))
            .jsonPath("$.defaultHidden")
            .value(is(DEFAULT_DEFAULT_HIDDEN.booleanValue()));
    }

    @Test
    void getNonExistingEDSApplication() {
        // Get the eDSApplication
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingEDSApplication() throws Exception {
        // Initialize the database
        eDSApplicationRepository.save(eDSApplication).block();

        int databaseSizeBeforeUpdate = eDSApplicationRepository.findAll().collectList().block().size();

        // Update the eDSApplication
        EDSApplication updatedEDSApplication = eDSApplicationRepository.findById(eDSApplication.getId()).block();
        updatedEDSApplication
            .name(UPDATED_NAME)
            .logo(UPDATED_LOGO)
            .logoContentType(UPDATED_LOGO_CONTENT_TYPE)
            .link(UPDATED_LINK)
            .description(UPDATED_DESCRIPTION)
            .category(UPDATED_CATEGORY)
            .authorizedRole(UPDATED_AUTHORIZED_ROLE)
            .needAuth(UPDATED_NEED_AUTH)
            .defaultHidden(UPDATED_DEFAULT_HIDDEN);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, updatedEDSApplication.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(updatedEDSApplication))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EDSApplication in the database
        List<EDSApplication> eDSApplicationList = eDSApplicationRepository.findAll().collectList().block();
        assertThat(eDSApplicationList).hasSize(databaseSizeBeforeUpdate);
        EDSApplication testEDSApplication = eDSApplicationList.get(eDSApplicationList.size() - 1);
        assertThat(testEDSApplication.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEDSApplication.getLogo()).isEqualTo(UPDATED_LOGO);
        assertThat(testEDSApplication.getLogoContentType()).isEqualTo(UPDATED_LOGO_CONTENT_TYPE);
        assertThat(testEDSApplication.getLink()).isEqualTo(UPDATED_LINK);
        assertThat(testEDSApplication.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testEDSApplication.getCategory()).isEqualTo(UPDATED_CATEGORY);
        assertThat(testEDSApplication.getAuthorizedRole()).isEqualTo(UPDATED_AUTHORIZED_ROLE);
        assertThat(testEDSApplication.getNeedAuth()).isEqualTo(UPDATED_NEED_AUTH);
        assertThat(testEDSApplication.getDefaultHidden()).isEqualTo(UPDATED_DEFAULT_HIDDEN);
    }

    @Test
    void putNonExistingEDSApplication() throws Exception {
        int databaseSizeBeforeUpdate = eDSApplicationRepository.findAll().collectList().block().size();
        eDSApplication.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, eDSApplication.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(eDSApplication))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EDSApplication in the database
        List<EDSApplication> eDSApplicationList = eDSApplicationRepository.findAll().collectList().block();
        assertThat(eDSApplicationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchEDSApplication() throws Exception {
        int databaseSizeBeforeUpdate = eDSApplicationRepository.findAll().collectList().block().size();
        eDSApplication.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(eDSApplication))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EDSApplication in the database
        List<EDSApplication> eDSApplicationList = eDSApplicationRepository.findAll().collectList().block();
        assertThat(eDSApplicationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamEDSApplication() throws Exception {
        int databaseSizeBeforeUpdate = eDSApplicationRepository.findAll().collectList().block().size();
        eDSApplication.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TestUtil.convertObjectToJsonBytes(eDSApplication))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the EDSApplication in the database
        List<EDSApplication> eDSApplicationList = eDSApplicationRepository.findAll().collectList().block();
        assertThat(eDSApplicationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateEDSApplicationWithPatch() throws Exception {
        // Initialize the database
        eDSApplicationRepository.save(eDSApplication).block();

        int databaseSizeBeforeUpdate = eDSApplicationRepository.findAll().collectList().block().size();

        // Update the eDSApplication using partial update
        EDSApplication partialUpdatedEDSApplication = new EDSApplication();
        partialUpdatedEDSApplication.setId(eDSApplication.getId());

        partialUpdatedEDSApplication
            .logo(UPDATED_LOGO)
            .logoContentType(UPDATED_LOGO_CONTENT_TYPE)
            .link(UPDATED_LINK)
            .description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEDSApplication.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEDSApplication))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EDSApplication in the database
        List<EDSApplication> eDSApplicationList = eDSApplicationRepository.findAll().collectList().block();
        assertThat(eDSApplicationList).hasSize(databaseSizeBeforeUpdate);
        EDSApplication testEDSApplication = eDSApplicationList.get(eDSApplicationList.size() - 1);
        assertThat(testEDSApplication.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testEDSApplication.getLogo()).isEqualTo(UPDATED_LOGO);
        assertThat(testEDSApplication.getLogoContentType()).isEqualTo(UPDATED_LOGO_CONTENT_TYPE);
        assertThat(testEDSApplication.getLink()).isEqualTo(UPDATED_LINK);
        assertThat(testEDSApplication.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testEDSApplication.getCategory()).isEqualTo(DEFAULT_CATEGORY);
        assertThat(testEDSApplication.getAuthorizedRole()).isEqualTo(DEFAULT_AUTHORIZED_ROLE);
        assertThat(testEDSApplication.getNeedAuth()).isEqualTo(DEFAULT_NEED_AUTH);
        assertThat(testEDSApplication.getDefaultHidden()).isEqualTo(DEFAULT_DEFAULT_HIDDEN);
    }

    @Test
    void fullUpdateEDSApplicationWithPatch() throws Exception {
        // Initialize the database
        eDSApplicationRepository.save(eDSApplication).block();

        int databaseSizeBeforeUpdate = eDSApplicationRepository.findAll().collectList().block().size();

        // Update the eDSApplication using partial update
        EDSApplication partialUpdatedEDSApplication = new EDSApplication();
        partialUpdatedEDSApplication.setId(eDSApplication.getId());

        partialUpdatedEDSApplication
            .name(UPDATED_NAME)
            .logo(UPDATED_LOGO)
            .logoContentType(UPDATED_LOGO_CONTENT_TYPE)
            .link(UPDATED_LINK)
            .description(UPDATED_DESCRIPTION)
            .category(UPDATED_CATEGORY)
            .authorizedRole(UPDATED_AUTHORIZED_ROLE)
            .needAuth(UPDATED_NEED_AUTH)
            .defaultHidden(UPDATED_DEFAULT_HIDDEN);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEDSApplication.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(partialUpdatedEDSApplication))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EDSApplication in the database
        List<EDSApplication> eDSApplicationList = eDSApplicationRepository.findAll().collectList().block();
        assertThat(eDSApplicationList).hasSize(databaseSizeBeforeUpdate);
        EDSApplication testEDSApplication = eDSApplicationList.get(eDSApplicationList.size() - 1);
        assertThat(testEDSApplication.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testEDSApplication.getLogo()).isEqualTo(UPDATED_LOGO);
        assertThat(testEDSApplication.getLogoContentType()).isEqualTo(UPDATED_LOGO_CONTENT_TYPE);
        assertThat(testEDSApplication.getLink()).isEqualTo(UPDATED_LINK);
        assertThat(testEDSApplication.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testEDSApplication.getCategory()).isEqualTo(UPDATED_CATEGORY);
        assertThat(testEDSApplication.getAuthorizedRole()).isEqualTo(UPDATED_AUTHORIZED_ROLE);
        assertThat(testEDSApplication.getNeedAuth()).isEqualTo(UPDATED_NEED_AUTH);
        assertThat(testEDSApplication.getDefaultHidden()).isEqualTo(UPDATED_DEFAULT_HIDDEN);
    }

    @Test
    void patchNonExistingEDSApplication() throws Exception {
        int databaseSizeBeforeUpdate = eDSApplicationRepository.findAll().collectList().block().size();
        eDSApplication.setId(UUID.randomUUID().toString());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, eDSApplication.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(eDSApplication))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EDSApplication in the database
        List<EDSApplication> eDSApplicationList = eDSApplicationRepository.findAll().collectList().block();
        assertThat(eDSApplicationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchEDSApplication() throws Exception {
        int databaseSizeBeforeUpdate = eDSApplicationRepository.findAll().collectList().block().size();
        eDSApplication.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(eDSApplication))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EDSApplication in the database
        List<EDSApplication> eDSApplicationList = eDSApplicationRepository.findAll().collectList().block();
        assertThat(eDSApplicationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamEDSApplication() throws Exception {
        int databaseSizeBeforeUpdate = eDSApplicationRepository.findAll().collectList().block().size();
        eDSApplication.setId(UUID.randomUUID().toString());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(TestUtil.convertObjectToJsonBytes(eDSApplication))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the EDSApplication in the database
        List<EDSApplication> eDSApplicationList = eDSApplicationRepository.findAll().collectList().block();
        assertThat(eDSApplicationList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteEDSApplication() {
        // Initialize the database
        eDSApplicationRepository.save(eDSApplication).block();

        int databaseSizeBeforeDelete = eDSApplicationRepository.findAll().collectList().block().size();

        // Delete the eDSApplication
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, eDSApplication.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        List<EDSApplication> eDSApplicationList = eDSApplicationRepository.findAll().collectList().block();
        assertThat(eDSApplicationList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
