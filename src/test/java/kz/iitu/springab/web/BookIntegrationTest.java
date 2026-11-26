package kz.iitu.springab.web;

import kz.iitu.springab.catalog.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureRestTestClient
class BookIntegrationTest {

    @Container
    @ServiceConnection
    static final PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:17-alpine");

    @LocalServerPort
    private int port;

    @Autowired
    private RestTestClient api;

    @Test
    void adminCreatesBookAndReadsItBackFromPostgres() throws Exception {
        // Пароль тестового admin, созданного миграцией в пустом контейнере.
        // Это НЕ пароль пользователя postgres и не пароль от pgAdmin.
        String password = System.getenv("LAB12_ADMIN_PASSWORD");
        assertThat(password)
                .as("Set LAB12_ADMIN_PASSWORD to the seeded application admin password")
                .isNotBlank();

        URI base = URI.create("http://localhost:" + port);
        CookieManager cookies = new CookieManager(null, CookiePolicy.ACCEPT_ALL);
        HttpClient browser = HttpClient.newBuilder()
                .cookieHandler(cookies)
                .followRedirects(HttpClient.Redirect.NEVER)
                .build();

        // 1. Открываем форму входа и получаем её CSRF-токен.
        var loginPage = browser.send(
                HttpRequest.newBuilder(base.resolve("/login")).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        assertThat(loginPage.statusCode()).isEqualTo(200);

        String form = "username=admin&password=" + encode(password)
                + "&_csrf=" + encode(csrfFrom(loginPage.body()));

        var login = browser.send(
                HttpRequest.newBuilder(base.resolve("/login"))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString(form))
                        .build(),
                HttpResponse.BodyHandlers.ofString());

        assertThat(login.statusCode()).isEqualTo(302);
        assertThat(login.headers().firstValue("Location").orElse(""))
                .endsWith("/books");

        // 2. После входа берём новый CSRF-токен и текущую сессию.
        var catalogue = browser.send(
                HttpRequest.newBuilder(base.resolve("/books")).GET().build(),
                HttpResponse.BodyHandlers.ofString());
        assertThat(catalogue.statusCode()).isEqualTo(200);

        String csrf = csrfFrom(catalogue.body());
        String session = cookies.getCookieStore().get(base).stream()
                .filter(cookie -> cookie.getName().equals("JSESSIONID"))
                .findFirst()
                .orElseThrow(() -> new AssertionError("No login session cookie"))
                .getValue();

        // 3. Создаём книгу через настоящий HTTP API.
        Book submitted = new Book(null, "Lab 12 Integration", "Arafat", 2026);

        Book saved = api.post().uri("/api/books")
                .header(HttpHeaders.COOKIE, "JSESSIONID=" + session)
                .header("X-CSRF-TOKEN", csrf)
                .contentType(MediaType.APPLICATION_JSON)
                .body(submitted)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Book.class)
                .returnResult()
                .getResponseBody();

        assertThat(saved).isNotNull();
        assertThat(saved.id()).isPositive();

        // 4. Читаем по ID, который выдала база, а не по заранее заданному ID.
        api.get().uri("/api/books/{id}", saved.id())
                .header(HttpHeaders.COOKIE, "JSESSIONID=" + session)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.title").isEqualTo("Lab 12 Integration")
                .jsonPath("$.author").isEqualTo("Arafat")
                .jsonPath("$.ownerUsername").isEqualTo("admin");
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static String csrfFrom(String html) {
        var matcher = Pattern.compile(
                "<input\\b(?=[^>]*\\bname=[\"']_csrf[\"'])"
                        + "[^>]*\\bvalue=[\"']([^\"']+)[\"']",
                Pattern.CASE_INSENSITIVE
        ).matcher(html);

        assertThat(matcher.find()).as("CSRF field in HTML form").isTrue();
        return matcher.group(1);
    }
}