package demo.catalogue;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import static java.util.Objects.requireNonNull;

@Service
public class CatalogueItemMessageHandler {

    private final CatalogueItemRepository repository;

    public CatalogueItemMessageHandler(final CatalogueItemRepository repository) {
        this.repository = requireNonNull(repository, "Catalogue item repository cannot be null");
    }

    @KafkaListener(topics = "new_catalogue_item")
    public void newCatalogueItem(@Payload final CatalogueItemTo item) {
        requireNonNull(item, "Catalogue item cannot be null");
        repository.save(item.toEntity());
    }
}
