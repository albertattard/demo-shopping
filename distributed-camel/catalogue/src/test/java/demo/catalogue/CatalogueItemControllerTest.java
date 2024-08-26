package demo.catalogue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.mockserver.model.HttpRequest;
import org.mockserver.verify.VerificationTimes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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

        private static ClientAndServer catalogueServer;

        @BeforeAll
        static void startServer() {
            catalogueServer = startClientAndServer(8082);

            catalogueServer.when(createNewCatalogueItemRequest())
                    .respond(response()
                            .withStatusCode(201)
                            .withHeaders(new Header("Content-Type", "application/json; charset=utf-8"))
                            .withBody("""
                                    {
                                      "id":6,
                                      "caption":"Green Plant"
                                    }"""));
        }

        @Test
        void returnTheNewIdOfTheAddedCatalogueItem() {
            final NewCatalogueItemTo item = new NewCatalogueItemTo("Green Plant", "Put a little life in your living room!!");

            final ResponseEntity<CatalogueItemTo> response = restTemplate.postForEntity("/catalogue/item", item, CatalogueItemTo.class);
            assertThat(response.getStatusCode())
                    .isEqualTo(HttpStatus.CREATED);

            final long id = assertThatHeaderLocationHasCatalogueItemId(response);

            assertThat(response.getBody())
                    .isEqualTo(new CatalogueItemTo(id, item.caption(), item.description()));

            waitForCamelToProcessRequest();

            catalogueServer.verify(createNewCatalogueItemRequest(), VerificationTimes.once());
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

        private static void waitForCamelToProcessRequest() {
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Interrupted while waiting for the item to be processed", e);
            }
        }

        private static HttpRequest createNewCatalogueItemRequest() {
            return request()
                    .withMethod("POST")
                    .withPath("/catalogue/item");
        }

        @AfterAll
        static void stopServer() {
            catalogueServer.stop();
        }
    }
}
