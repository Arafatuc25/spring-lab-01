package kz.iitu.springab.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "books")
public class BookEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(name = "publication_year", nullable = false)
    private int year;

    @Column(name = "owner_username", nullable = false, length = 60)
    private String ownerUsername;

    protected BookEntity() {
    }

    public BookEntity(Book book) {
        this.ownerUsername = book.ownerUsername();
        update(book);
    }

    public void update(Book book) {
        this.title = book.title();
        this.author = book.author();
        this.year = book.year();
    }

    public Book toBook() {
        return new Book(
                id,
                title,
                author,
                year,
                ownerUsername
        );
    }
}