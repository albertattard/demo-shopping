package demo.catalogue;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CamelConfiguration extends RouteBuilder {

    @Override
    public void configure() {
        final String cartRootUri = Optional.ofNullable(System.getenv("CART_ROOT_URI"))
                .orElse("http://localhost:8082");

        from("""
                jpa:demo.catalogue.CatalogueItemEntity?
                namedQuery=new-catalogue-item&consumeDelete=false""")
                .bean(CatalogueItemTo.class, "of(${body})")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .setHeader(Exchange.CONTENT_TYPE, constant("application/json"))
                .marshal().json(JsonLibrary.Jackson)
                .to(cartRootUri.concat("/catalogue/item"))
                .log("New catalogue item processed by cart: ${body}");
    }
}
