package demo.catalogue;

import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public record CatalogueItemTo(long id, String caption) {

    public static CatalogueItemTo of(final CatalogueItemEntity entity) {
        requireNonNull(entity);

        return new CatalogueItemTo(entity.id(), entity.caption());
    }

    public CatalogueItemEntity toEntity() {
        return new CatalogueItemEntity(id, caption);
    }

    public <T> T map(final Function<CatalogueItemTo, T> mapper) {
        requireNonNull(mapper);
        return mapper.apply(this);
    }
}
