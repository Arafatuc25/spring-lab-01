package kz.iitu.springab.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/css/**")
                        .permitAll()

                        // REST API: создание книги и чтение по числовому ID
                        .requestMatchers(HttpMethod.POST, "/api/books")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/books/{id:[0-9]+}")
                        .hasAnyRole("USER", "ADMIN")

                        // Страницы каталога
                        .requestMatchers(HttpMethod.GET, "/books", "/books/**")
                        .hasAnyRole("USER", "ADMIN")
                        .requestMatchers(HttpMethod.POST, "/books/*/edit")
                        .hasAnyRole("USER", "ADMIN")
                        .requestMatchers("/books", "/books/**")
                        .hasRole("ADMIN")

                        .requestMatchers("/admin", "/admin/**")
                        .hasRole("ADMIN")
                        .anyRequest()
                        .denyAll()
                )
                .formLogin(form -> form
                        .defaultSuccessUrl("/books", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/?logout")
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}