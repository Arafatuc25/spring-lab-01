package kz.iitu.springab.web;

import kz.iitu.springab.catalog.Book;
import kz.iitu.springab.catalog.BookService;
import kz.iitu.springab.config.SecurityConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookRestController.class)
@Import(SecurityConfiguration.class)
class BookControllerTest {

    private static final String VALID_BOOK_JSON = """
            {
              "title": "Effective Java",
              "author": "Joshua Bloch",
              "year": 2018
            }
            """;

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private BookService service;

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void returnsBookAsJsonWhenIdExists() throws Exception {
        Book book = new Book(1L, "Effective Java", "Joshua Bloch", 2018);
        when(service.findById(1L)).thenReturn(Optional.of(book));

        mvc.perform(get("/api/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Effective Java"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void returnsProblemDetailWhenBookIsMissing() throws Exception {
        when(service.findById(999L)).thenReturn(Optional.empty());

        mvc.perform(get("/api/books/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.title").isNotEmpty());
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void rejectsBookWithBlankTitle() throws Exception {
        mvc.perform(post("/api/books")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "title": "   ",
                                  "author": "Joshua Bloch",
                                  "year": 2018
                                }
                                """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void userCannotCreateBookEvenWithValidCsrf() throws Exception {
        mvc.perform(post("/api/books")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BOOK_JSON))
                .andExpect(status().isForbidden());

        verifyNoInteractions(service);
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void adminCanCreateBookWithValidCsrf() throws Exception {
        Book submitted = new Book(null, "Effective Java", "Joshua Bloch", 2018);
        Book saved = new Book(7L, "Effective Java", "Joshua Bloch", 2018, "admin");
        when(service.create(submitted)).thenReturn(saved);

        mvc.perform(post("/api/books")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_BOOK_JSON))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/books/7"))
                .andExpect(jsonPath("$.id").value(7));
    }
    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void rejectsBookWhenTitleIsMissing() throws Exception {
        mvc.perform(post("/api/books")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {
                              "author": "Joshua Bloch",
                              "year": 2018
                            }
                            """))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(service);
    }
}