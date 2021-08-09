package com.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(C.class);
        C c1 = new C();
        c1.setId(1L);
        C c2 = new C();
        c2.setId(c1.getId());
        assertThat(c1).isEqualTo(c2);
        c2.setId(2L);
        assertThat(c1).isNotEqualTo(c2);
        c1.setId(null);
        assertThat(c1).isNotEqualTo(c2);
    }
}
