package kz.iitu.springab.catalog;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public List<Book> findAll(String author) {
        if (author == null || author.isBlank()) {
            return repository.findAll();
        }

        String requestedAuthor =
                author.toLowerCase(Locale.ROOT);

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

    public Book create(Book book) {
        return repository.save(new Book(
                null,
                book.title(),
                book.author(),
                book.year()
        ));
    }

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

    public boolean delete(long id) {
        return repository.deleteById(id);
    }
}