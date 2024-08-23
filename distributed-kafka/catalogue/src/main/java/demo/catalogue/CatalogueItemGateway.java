package demo.catalogue;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static java.util.Objects.requireNonNull;

@Service
public class CatalogueItemGateway {

    private final KafkaTemplate<String, CatalogueItemTo> kafkaTemplate;

    public CatalogueItemGateway(final KafkaTemplate<String, CatalogueItemTo> kafkaTemplate) {
        this.kafkaTemplate = requireNonNull(kafkaTemplate, "The kafka template cannot be null");
    }

    public void newCatalogueItem(final CatalogueItemEntity item) {
        requireNonNull(item, "The catalogue item entry cannot be null");

        kafkaTemplate.send("new_catalogue_item", CatalogueItemTo.of(item));
    }
}
