package com.myapp.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A B.
 */
@Table("b")
public class B implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @JsonIgnoreProperties(value = { "bs" }, allowSetters = true)
    @Transient
    private A a;

    @Column("aa_id")
    private Long aId;

    // jhipster-needle-entity-add-field - JHipster will add fields here
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public B id(Long id) {
        this.id = id;
        return this;
    }

    public A getA() {
        return this.a;
    }

    public B a(A a) {
        this.setA(a);
        this.aId = a != null ? a.getId() : null;
        return this;
    }

    public void setA(A a) {
        this.a = a;
        this.aId = a != null ? a.getId() : null;
    }

    public Long getAId() {
        return this.aId;
    }

    public void setAId(Long a) {
        this.aId = a;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof B)) {
            return false;
        }
        return id != null && id.equals(((B) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "B{" +
            "id=" + getId() +
            "}";
    }
}
