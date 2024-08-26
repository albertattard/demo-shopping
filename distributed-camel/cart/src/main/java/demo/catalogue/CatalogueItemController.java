package demo.catalogue;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static java.util.Objects.requireNonNull;

@RestController
@RequestMapping("/catalogue/item")
public class CatalogueItemController {

    private final CatalogueItemRepository repository;

    public CatalogueItemController(final CatalogueItemRepository repository) {
        this.repository = requireNonNull(repository, "Repository cannot be null");
    }

    @PostMapping()
    public ResponseEntity<CatalogueItemTo> add(@RequestBody final CatalogueItemTo item) {
        return item.toEntity()
                .map(repository::save)
                .map(CatalogueItemTo::of)
                .map(e -> ResponseEntity.created(location(e)).body(e));
    }

    private static URI location(final CatalogueItemTo item) {
        requireNonNull(item, "Catalogue item cannot be null");
        return URI.create("/catalogue/item/%d".formatted(item.id()));
    }
}
