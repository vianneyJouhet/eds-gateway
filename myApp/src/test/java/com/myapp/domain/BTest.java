package com.myapp.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.myapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class BTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(B.class);
        B b1 = new B();
        b1.setId(1L);
        B b2 = new B();
        b2.setId(b1.getId());
        assertThat(b1).isEqualTo(b2);
        b2.setId(2L);
        assertThat(b1).isNotEqualTo(b2);
        b1.setId(null);
        assertThat(b1).isNotEqualTo(b2);
    }
}
