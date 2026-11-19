package kz.iitu.springab.catalog;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public List<Book> findAll(String author) {
        if (author == null || author.isBlank()) {
            return repository.findAll();
        }

        String requestedAuthor = author.toLowerCase(Locale.ROOT);

        return repository.findAll()
                .stream()
                .filter(book -> book.author()
                        .toLowerCase(Locale.ROOT)
                        .contains(requestedAuthor))
                .toList();
    }

    public Optional<Book> findById(long id) {
        return repository.findById(id);
    }

    @PreAuthorize(
            "hasRole('ADMIN') or "
                    + "@bookSecurity.isOwner(#p0, authentication.name)"
    )
    public Optional<Book> findForEdit(long id) {
        return repository.findById(id);
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public Book create(Book book) {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return repository.save(new Book(
                null,
                book.title(),
                book.author(),
                book.year(),
                username
        ));
    }

    @Transactional
    @PreAuthorize(
            "hasRole('ADMIN') or "
                    + "@bookSecurity.isOwner(#p0, authentication.name)"
    )
    public Optional<Book> replace(long id, Book book) {
        if (repository.findById(id).isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(repository.save(new Book(
                id,
                book.title(),
                book.author(),
                book.year()
        )));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public boolean delete(long id) {
        return repository.deleteById(id);
    }
}