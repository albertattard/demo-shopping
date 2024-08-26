package demo.catalogue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CatalogueItemControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Nested
    class AddCatalogueItemTest {

        @Test
        void returnTheNewIdOfTheAddedCatalogueItem() {
            final NewCatalogueItemTo item = new NewCatalogueItemTo(6, "Green Plant", "Put a little life in your living room!!");

            final ResponseEntity<CatalogueItemTo> response = restTemplate.postForEntity("/catalogue/item", item, CatalogueItemTo.class);
            assertThat(response.getStatusCode())
                    .isEqualTo(HttpStatus.CREATED);

            final long id = assertThatHeaderLocationHasCatalogueItemId(response);

            assertThat(response.getBody())
                    .isEqualTo(new CatalogueItemTo(id, item.caption()));
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

        record NewCatalogueItemTo(long id, String caption, String description) {}
    }
}