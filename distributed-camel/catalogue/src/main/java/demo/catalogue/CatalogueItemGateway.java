package demo.catalogue;

import org.springframework.stereotype.Service;

import static java.util.Objects.requireNonNull;

@Service
public class CatalogueItemGateway {

    public void newCatalogueItem(final CatalogueItemEntity item) {
        requireNonNull(item, "The catalogue item entry cannot be null");
    }
}
