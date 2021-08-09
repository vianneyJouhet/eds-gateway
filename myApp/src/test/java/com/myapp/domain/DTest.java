package com.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class DTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(D.class);
        D d1 = new D();
        d1.setId(1L);
        D d2 = new D();
        d2.setId(d1.getId());
        assertThat(d1).isEqualTo(d2);
        d2.setId(2L);
        assertThat(d1).isNotEqualTo(d2);
        d1.setId(null);
        assertThat(d1).isNotEqualTo(d2);
    }
}
