package kz.iitu.springab.catalog;

public record Book(
        Long id,
        String title,
        String author,
        int year
) {
}