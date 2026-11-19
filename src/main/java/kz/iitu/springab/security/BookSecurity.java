package kz.iitu.springab.security;

import kz.iitu.springab.catalog.BookRepository;
import org.springframework.stereotype.Component;

@Component("bookSecurity")
public class BookSecurity {

    private final BookRepository books;

    public BookSecurity(BookRepository books) {
        this.books = books;
    }

    public boolean isOwner(long id, String username) {
        return books.isOwner(id, username);
    }
}