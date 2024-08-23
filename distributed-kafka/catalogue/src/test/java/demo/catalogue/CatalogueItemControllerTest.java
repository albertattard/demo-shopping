package demo.catalogue;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = "new_catalogue_item", bootstrapServersProperty = "spring.kafka.bootstrap-servers")
class CatalogueItemControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Nested
    class GetCatalogueItemTest {

        @Test
        void returnTheCatalogueItemWhenGivenAnExistingId() {
            final ResponseEntity<CatalogueItemTo> response = getCatalogueItemWithId(1);
            assertThat(response.getStatusCode())
                    .isEqualTo(HttpStatus.OK);
            assertThat(response.getBody())
                    .isEqualTo(new CatalogueItemTo(1L, "Leather Sofa", "A very nice and comfortable sofa"));
        }

        @Test
        void returnNotFoundWhenGivenAnIdThatDoesNotExists() {
            final ResponseEntity<CatalogueItemTo> response = getCatalogueItemWithId(10);
            assertThat(response.getStatusCode())
                    .isEqualTo(HttpStatus.NOT_FOUND);
        }

        private ResponseEntity<CatalogueItemTo> getCatalogueItemWithId(final long id) {
            return restTemplate.getForEntity("/catalogue/item/%d".formatted(id), CatalogueItemTo.class);
        }
    }

    @Nested
    class AddCatalogueItemTest {

        @Autowired
        private EmbeddedKafkaBroker embeddedKafka;

        @Test
        void returnTheNewIdOfTheAddedCatalogueItem() {
            final NewCatalogueItemTo item = new NewCatalogueItemTo("Green Plant", "Put a little life in your living room!!");

            final ResponseEntity<CatalogueItemTo> response = restTemplate.postForEntity("/catalogue/item", item, CatalogueItemTo.class);
            assertThat(response.getStatusCode())
                    .isEqualTo(HttpStatus.CREATED);

            final long id = assertThatHeaderLocationHasCatalogueItemId(response);

            assertThat(response.getBody())
                    .isEqualTo(new CatalogueItemTo(id, item.caption(), item.description()));

            final ConsumerRecords<String, CatalogueItemTo> records = getConsumerRecords();

            assertThat(records)
                    .describedAs("The message should be sent to Kafka")
                    .isNotNull();

            assertThat(records.count())
                    .describedAs("The message should be sent to Kafka")
                    .isEqualTo(1)
            ;

            assertThat(records.iterator().next().value())
                    .describedAs("The message value/body should be equal to the newly created catalogue item")
                    .isEqualTo(new CatalogueItemTo(id, item.caption(), item.description()));
        }

        private ConsumerRecords<String, CatalogueItemTo> getConsumerRecords() {
            final Map<String, Object> consumerProperties = KafkaTestUtils.consumerProps("demo", "true", embeddedKafka);
            consumerProperties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
            consumerProperties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
            consumerProperties.put(JsonDeserializer.TRUSTED_PACKAGES, "demo.catalogue");
            consumerProperties.put(ConsumerConfig.GROUP_ID_CONFIG, "demo");
            consumerProperties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

            final ConsumerFactory<String, CatalogueItemTo> consumerFactory = new DefaultKafkaConsumerFactory<>(consumerProperties);

            final Consumer<String, CatalogueItemTo> consumer = consumerFactory.createConsumer();
            embeddedKafka.consumeFromAnEmbeddedTopic(consumer, "new_catalogue_item");

            return KafkaTestUtils.getRecords(consumer);
        }

        private static long assertThatHeaderLocationHasCatalogueItemId(final ResponseEntity<CatalogueItemTo> response) {
            assertThat(response.getHeaders())
                    .isNotNull();
            assertThat(response.getHeaders().getLocation())
                    .isNotNull();

            final String location = response.getHeaders().getLocation().getPath();
            assertThat(location)
                    .matches(Pattern.compile("/catalogue/item/\\d"));

            return Long.parseLong(location.substring(16));
        }
    }
}
