package demo.catalogue;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Entity(name = "external_catalogue_item")
public class CatalogueItemEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private long id;
    private String caption;

    protected CatalogueItemEntity() {}

    public CatalogueItemEntity(final long id, final String caption) {
        this.id = id;
        this.caption = caption;
    }

    public long id() {
        return id;
    }

    public String caption() {
        return caption;
    }

    @Override
    public boolean equals(final Object object) {
        return object instanceof final CatalogueItemEntity other
               && getClass() == other.getClass()
               && id == other.id
               && Objects.equals(caption, other.caption);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, caption);
    }

    @Override
    public String toString() {
        return "CatalogueItemEntity[id=%d, caption=%s]".formatted(id, caption);
    }
}
