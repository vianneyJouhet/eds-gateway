package fr.chu.bordeaux.eds.domain;

import java.io.Serializable;
import javax.validation.constraints.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * A EDSApplication.
 */
@Document(collection = "eds_application")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EDSApplication implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("logo")
    private byte[] logo;

    @NotNull
    @Field("logo_content_type")
    private String logoContentType;

    @Field("link")
    private String link;

    @Field("description")
    private String description;

    @Field("category")
    private String category;

    @Field("authorized_role")
    private String authorizedRole;

    @Field("need_auth")
    private Boolean needAuth;

    @Field("default_hidden")
    private Boolean defaultHidden;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public EDSApplication id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public EDSApplication name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getLogo() {
        return this.logo;
    }

    public EDSApplication logo(byte[] logo) {
        this.setLogo(logo);
        return this;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

    public String getLogoContentType() {
        return this.logoContentType;
    }

    public EDSApplication logoContentType(String logoContentType) {
        this.logoContentType = logoContentType;
        return this;
    }

    public void setLogoContentType(String logoContentType) {
        this.logoContentType = logoContentType;
    }

    public String getLink() {
        return this.link;
    }

    public EDSApplication link(String link) {
        this.setLink(link);
        return this;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getDescription() {
        return this.description;
    }

    public EDSApplication description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return this.category;
    }

    public EDSApplication category(String category) {
        this.setCategory(category);
        return this;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAuthorizedRole() {
        return this.authorizedRole;
    }

    public EDSApplication authorizedRole(String authorizedRole) {
        this.setAuthorizedRole(authorizedRole);
        return this;
    }

    public void setAuthorizedRole(String authorizedRole) {
        this.authorizedRole = authorizedRole;
    }

    public Boolean getNeedAuth() {
        return this.needAuth;
    }

    public EDSApplication needAuth(Boolean needAuth) {
        this.setNeedAuth(needAuth);
        return this;
    }

    public void setNeedAuth(Boolean needAuth) {
        this.needAuth = needAuth;
    }

    public Boolean getDefaultHidden() {
        return this.defaultHidden;
    }

    public EDSApplication defaultHidden(Boolean defaultHidden) {
        this.setDefaultHidden(defaultHidden);
        return this;
    }

    public void setDefaultHidden(Boolean defaultHidden) {
        this.defaultHidden = defaultHidden;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EDSApplication)) {
            return false;
        }
        return id != null && id.equals(((EDSApplication) o).id);
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EDSApplication{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", logo='" + getLogo() + "'" +
            ", logoContentType='" + getLogoContentType() + "'" +
            ", link='" + getLink() + "'" +
            ", description='" + getDescription() + "'" +
            ", category='" + getCategory() + "'" +
            ", authorizedRole='" + getAuthorizedRole() + "'" +
            ", needAuth='" + getNeedAuth() + "'" +
            ", defaultHidden='" + getDefaultHidden() + "'" +
            "}";
    }
}
