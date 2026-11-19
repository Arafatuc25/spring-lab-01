package kz.iitu.springab.web;

import kz.iitu.springab.catalog.Book;
import kz.iitu.springab.catalog.BookService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/books")
public class BookPageController {

    private final BookService service;

    public BookPageController(BookService service) {
        this.service = service;
    }

    @GetMapping
    public String list(
            @RequestParam(required = false) String author,
            Model model
    ) {
        model.addAttribute("books", service.findAll(author));
        model.addAttribute("author", author);
        model.addAttribute("title", "Book Catalogue");

        return "books/list";
    }

    @PostMapping
    public String create(
            @RequestParam String title,
            @RequestParam String author,
            @RequestParam int year,
            RedirectAttributes redirect
    ) {
        Book saved = service.create(
                new Book(null, title, author, year)
        );

        redirect.addFlashAttribute(
                "message",
                "Book \"" + saved.title() + "\" has been added"
        );

        return "redirect:/books";
    }

    @GetMapping("/{id}/edit")
    public String edit(
            @PathVariable long id,
            Model model
    ) {
        Book book = service.findForEdit(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Book not found"
                        )
                );

        model.addAttribute("book", book);

        return "books/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(
            @PathVariable long id,
            @RequestParam String title,
            @RequestParam String author,
            @RequestParam int year,
            RedirectAttributes redirect
    ) {
        Book updated = service.replace(
                id,
                new Book(id, title, author, year)
        ).orElseThrow(() ->
                new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Book not found"
                )
        );

        redirect.addFlashAttribute(
                "message",
                "Book \"" + updated.title() + "\" has been updated"
        );

        return "redirect:/books";
    }
}