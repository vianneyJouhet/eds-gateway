package com.myapp.web.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.myapp.IntegrationTest;
import com.myapp.domain.C;
import com.myapp.repository.CRepository;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link CResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class CResourceIT {

    private static final String ENTITY_API_URL = "/api/cs";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private CRepository cRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restCMockMvc;

    private C c;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static C createEntity(EntityManager em) {
        C c = new C();
        return c;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static C createUpdatedEntity(EntityManager em) {
        C c = new C();
        return c;
    }

    @BeforeEach
    public void initTest() {
        c = createEntity(em);
    }

    @Test
    @Transactional
    void createC() throws Exception {
        int databaseSizeBeforeCreate = cRepository.findAll().size();
        // Create the C
        restCMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(c)))
            .andExpect(status().isCreated());

        // Validate the C in the database
        List<C> cList = cRepository.findAll();
        assertThat(cList).hasSize(databaseSizeBeforeCreate + 1);
        C testC = cList.get(cList.size() - 1);
    }

    @Test
    @Transactional
    void createCWithExistingId() throws Exception {
        // Create the C with an existing ID
        c.setId(1L);

        int databaseSizeBeforeCreate = cRepository.findAll().size();

        // An entity with an existing ID cannot be created, so this API call must fail
        restCMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(c)))
            .andExpect(status().isBadRequest());

        // Validate the C in the database
        List<C> cList = cRepository.findAll();
        assertThat(cList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllCS() throws Exception {
        // Initialize the database
        cRepository.saveAndFlush(c);

        // Get all the cList
        restCMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(c.getId().intValue())));
    }

    @Test
    @Transactional
    void getC() throws Exception {
        // Initialize the database
        cRepository.saveAndFlush(c);

        // Get the c
        restCMockMvc
            .perform(get(ENTITY_API_URL_ID, c.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(c.getId().intValue()));
    }

    @Test
    @Transactional
    void getNonExistingC() throws Exception {
        // Get the c
        restCMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putNewC() throws Exception {
        // Initialize the database
        cRepository.saveAndFlush(c);

        int databaseSizeBeforeUpdate = cRepository.findAll().size();

        // Update the c
        C updatedC = cRepository.findById(c.getId()).get();
        // Disconnect from session so that the updates on updatedC are not directly saved in db
        em.detach(updatedC);

        restCMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedC.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(updatedC))
            )
            .andExpect(status().isOk());

        // Validate the C in the database
        List<C> cList = cRepository.findAll();
        assertThat(cList).hasSize(databaseSizeBeforeUpdate);
        C testC = cList.get(cList.size() - 1);
    }

    @Test
    @Transactional
    void putNonExistingC() throws Exception {
        int databaseSizeBeforeUpdate = cRepository.findAll().size();
        c.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCMockMvc
            .perform(
                put(ENTITY_API_URL_ID, c.getId()).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(c))
            )
            .andExpect(status().isBadRequest());

        // Validate the C in the database
        List<C> cList = cRepository.findAll();
        assertThat(cList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchC() throws Exception {
        int databaseSizeBeforeUpdate = cRepository.findAll().size();
        c.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCMockMvc
            .perform(
                put(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(TestUtil.convertObjectToJsonBytes(c))
            )
            .andExpect(status().isBadRequest());

        // Validate the C in the database
        List<C> cList = cRepository.findAll();
        assertThat(cList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamC() throws Exception {
        int databaseSizeBeforeUpdate = cRepository.findAll().size();
        c.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(TestUtil.convertObjectToJsonBytes(c)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the C in the database
        List<C> cList = cRepository.findAll();
        assertThat(cList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateCWithPatch() throws Exception {
        // Initialize the database
        cRepository.saveAndFlush(c);

        int databaseSizeBeforeUpdate = cRepository.findAll().size();

        // Update the c using partial update
        C partialUpdatedC = new C();
        partialUpdatedC.setId(c.getId());

        restCMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedC.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedC))
            )
            .andExpect(status().isOk());

        // Validate the C in the database
        List<C> cList = cRepository.findAll();
        assertThat(cList).hasSize(databaseSizeBeforeUpdate);
        C testC = cList.get(cList.size() - 1);
    }

    @Test
    @Transactional
    void fullUpdateCWithPatch() throws Exception {
        // Initialize the database
        cRepository.saveAndFlush(c);

        int databaseSizeBeforeUpdate = cRepository.findAll().size();

        // Update the c using partial update
        C partialUpdatedC = new C();
        partialUpdatedC.setId(c.getId());

        restCMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedC.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(partialUpdatedC))
            )
            .andExpect(status().isOk());

        // Validate the C in the database
        List<C> cList = cRepository.findAll();
        assertThat(cList).hasSize(databaseSizeBeforeUpdate);
        C testC = cList.get(cList.size() - 1);
    }

    @Test
    @Transactional
    void patchNonExistingC() throws Exception {
        int databaseSizeBeforeUpdate = cRepository.findAll().size();
        c.setId(count.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restCMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, c.getId())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(c))
            )
            .andExpect(status().isBadRequest());

        // Validate the C in the database
        List<C> cList = cRepository.findAll();
        assertThat(cList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchC() throws Exception {
        int databaseSizeBeforeUpdate = cRepository.findAll().size();
        c.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, count.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(TestUtil.convertObjectToJsonBytes(c))
            )
            .andExpect(status().isBadRequest());

        // Validate the C in the database
        List<C> cList = cRepository.findAll();
        assertThat(cList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamC() throws Exception {
        int databaseSizeBeforeUpdate = cRepository.findAll().size();
        c.setId(count.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restCMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(TestUtil.convertObjectToJsonBytes(c)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the C in the database
        List<C> cList = cRepository.findAll();
        assertThat(cList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteC() throws Exception {
        // Initialize the database
        cRepository.saveAndFlush(c);

        int databaseSizeBeforeDelete = cRepository.findAll().size();

        // Delete the c
        restCMockMvc.perform(delete(ENTITY_API_URL_ID, c.getId()).accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<C> cList = cRepository.findAll();
        assertThat(cList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
