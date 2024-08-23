package demo.catalogue;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.Optional;

@Configuration
public class CatalogueConfiguration {

    @Bean
    public RestTemplate catalogueRestTemplate(final RestTemplateBuilder builder) {
        final String catalogueRootUri = Optional.ofNullable(System.getenv("CATALOGUE_ROOT_URI"))
                .orElse("http://localhost:8081/");

        return builder
                .setConnectTimeout(Duration.ofSeconds(1))
                .setReadTimeout(Duration.ofSeconds(1))
                .rootUri(catalogueRootUri)
                .build();
    }
}
