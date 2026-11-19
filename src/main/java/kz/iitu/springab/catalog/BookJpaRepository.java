package kz.iitu.springab.catalog;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BookJpaRepository
        extends JpaRepository<BookEntity, Long> {
}