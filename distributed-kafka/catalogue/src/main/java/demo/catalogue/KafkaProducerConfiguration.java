package demo.catalogue;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaProducerConfiguration {

    @Bean
    public NewTopic newCatalogueItemTopic() {
        return TopicBuilder.name("new_catalogue_item")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
