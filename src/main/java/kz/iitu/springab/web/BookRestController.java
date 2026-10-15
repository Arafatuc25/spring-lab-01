package kz.iitu.springab.web;

import kz.iitu.springab.catalog.Book;
import kz.iitu.springab.catalog.BookService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/books")
public class BookRestController {

    private final BookService service;

    public BookRestController(BookService service) {
        this.service = service;
    }

    @GetMapping
    public List<Book> list(
            @RequestParam(required = false) String author,
            @RequestParam(defaultValue = "10") int limit) {

        return service.findAll(author)
                .stream()
                .limit(limit)
                .toList();
    }

    @GetMapping("/stats")
    public Map<String, Object> stats() {

        List<Book> books = service.findAll(null);

        Integer earliestYear = books.isEmpty()
                ? null
                : books.stream()
                .mapToInt(Book::year)
                .min()
                .orElseThrow();

        Integer latestYear = books.isEmpty()
                ? null
                : books.stream()
                .mapToInt(Book::year)
                .max()
                .orElseThrow();

        Map<String, Object> result =
                new LinkedHashMap<>();

        result.put("count", books.size());
        result.put("earliestYear", earliestYear);
        result.put("latestYear", latestYear);

        return result;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> find(
            @PathVariable long id) {

        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Book> create(
            @RequestBody Book book) {

        Book saved = service.create(book);

        return ResponseEntity
                .created(URI.create(
                        "/api/books/" + saved.id()
                ))
                .body(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> replace(
            @PathVariable long id,
            @RequestBody Book book) {

        return service.replace(id, book)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable long id) {

        if (service.delete(id)) {
            return ResponseEntity
                    .noContent()
                    .build();
        }

        return ResponseEntity
                .notFound()
                .build();
    }
}