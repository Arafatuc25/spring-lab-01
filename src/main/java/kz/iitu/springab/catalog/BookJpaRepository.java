package kz.iitu.springab.catalog;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookJpaRepository
        extends JpaRepository<BookEntity, Long> {

    List<BookEntity> findByAuthorIgnoreCase(String author);
}