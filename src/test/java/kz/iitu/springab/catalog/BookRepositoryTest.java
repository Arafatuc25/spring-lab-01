package kz.iitu.springab.catalog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = {
        "spring.flyway.enabled=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class BookRepositoryTest {

    @Autowired
    private TestEntityManager em;

    @Autowired
    private BookJpaRepository repository;

    @Test
    void findsOnlyBooksByRequestedAuthorIgnoringCase() {
        persistBook("Effective Java", "Joshua Bloch", 2018);
        persistBook("Clean Code", "Robert Martin", 2008);
        em.flush();
        em.clear();

        var result = repository.findByAuthorIgnoreCase("joshua bloch");

        assertThat(result)
                .extracting(entity -> entity.toBook().title())
                .containsExactly("Effective Java");
    }

    @Test
    void returnsEmptyListWhenAuthorDoesNotMatch() {
        persistBook("Effective Java", "Joshua Bloch", 2018);
        em.flush();
        em.clear();

        var result = repository.findByAuthorIgnoreCase("Unknown Author");

        assertThat(result).isEmpty();
    }

    private void persistBook(String title, String author, int year) {
        em.persist(new BookEntity(new Book(null, title, author, year, "admin")));
    }
}