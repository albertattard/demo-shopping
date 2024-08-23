package demo.service;

import demo.dao.CatalogueItemEntity;
import demo.dao.CatalogueItemRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.util.Objects.requireNonNull;

@Service
public class CatalogueItemService {

    private final CatalogueItemRepository repository;

    public CatalogueItemService(final CatalogueItemRepository repository) {
        this.repository = requireNonNull(repository, "Repository cannot be null");
    }

    public Optional<CatalogueItemEntity> findById(final long id) {
        return repository.findById(id);
    }

    public CatalogueItemEntity add(final CatalogueItemEntity entity) {
        requireNonNull(entity, "Entity cannot be null");
        return repository.save(entity);
    }
}
