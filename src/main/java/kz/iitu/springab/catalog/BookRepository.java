package kz.iitu.springab.catalog;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional(readOnly = true)
public class BookRepository {

    private final BookJpaRepository repository;

    public BookRepository(BookJpaRepository repository) {
        this.repository = repository;
    }

    public List<Book> findAll() {
        return repository.findAll(Sort.by("id"))
                .stream()
                .map(BookEntity::toBook)
                .toList();
    }

    public Optional<Book> findById(long id) {
        return repository.findById(id)
                .map(BookEntity::toBook);
    }

    public boolean isOwner(long id, String username) {
        if (username == null) {
            return false;
        }

        return findById(id)
                .map(book -> username.equals(book.ownerUsername()))
                .orElse(false);
    }

    @Transactional
    public Book save(Book book) {
        BookEntity entity;

        if (book.id() == null) {
            if (book.ownerUsername() == null) {
                throw new IllegalArgumentException(
                        "Book owner is required"
                );
            }

            entity = new BookEntity(book);
        } else {
            entity = repository.findById(book.id())
                    .orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Book not found: " + book.id()
                            )
                    );

            entity.update(book);
        }

        return repository.saveAndFlush(entity).toBook();
    }

    @Transactional
    public boolean deleteById(long id) {
        Optional<BookEntity> entity = repository.findById(id);

        if (entity.isEmpty()) {
            return false;
        }

        repository.delete(entity.get());
        return true;
    }
}