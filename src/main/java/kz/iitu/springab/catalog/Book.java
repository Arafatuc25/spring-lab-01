package kz.iitu.springab.catalog;

public record Book(
        Long id,
        String title,
        String author,
        int year,
        String ownerUsername
) {

    public Book(
            Long id,
            String title,
            String author,
            int year
    ) {
        this(id, title, author, year, null);
    }
}