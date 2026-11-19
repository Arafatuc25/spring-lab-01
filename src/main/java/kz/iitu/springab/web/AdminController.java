package kz.iitu.springab.web;

import kz.iitu.springab.security.AppUserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AdminController {

    private final AppUserRepository users;

    public AdminController(AppUserRepository users) {
        this.users = users;
    }

    public record UserRow(
            Long id,
            String username,
            boolean enabled,
            String roles
    ) {
    }

    @GetMapping("/admin")
    public String admin(Model model, Authentication authentication) {
        List<UserRow> rows = users.findAll(Sort.by("username"))
                .stream()
                .map(user -> new UserRow(
                        user.getId(),
                        user.getUsername(),
                        user.isEnabled(),
                        user.getRoles()
                                .stream()
                                .sorted()
                                .collect(Collectors.joining(", "))
                ))
                .toList();

        model.addAttribute("users", rows);
        model.addAttribute(
                "currentUsername",
                authentication.getName()
        );

        return "admin/users";
    }
}