package fr.chu.bordeaux.eds.domain;

import static org.assertj.core.api.Assertions.assertThat;

import fr.chu.bordeaux.eds.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EDSApplicationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EDSApplication.class);
        EDSApplication eDSApplication1 = new EDSApplication();
        eDSApplication1.setId("id1");
        EDSApplication eDSApplication2 = new EDSApplication();
        eDSApplication2.setId(eDSApplication1.getId());
        assertThat(eDSApplication1).isEqualTo(eDSApplication2);
        eDSApplication2.setId("id2");
        assertThat(eDSApplication1).isNotEqualTo(eDSApplication2);
        eDSApplication1.setId(null);
        assertThat(eDSApplication1).isNotEqualTo(eDSApplication2);
    }
}
