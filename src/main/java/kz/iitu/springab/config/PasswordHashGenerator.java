package kz.iitu.springab.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String userHash1 = encoder.encode("user123");
        String userHash2 = encoder.encode("user123");
        String adminHash = encoder.encode("admin123");

        System.out.println("USER_HASH_1=" + userHash1);
        System.out.println("USER_HASH_2=" + userHash2);
        System.out.println("ADMIN_HASH=" + adminHash);

        System.out.println(
                "HASHES_DIFFER=" + !userHash1.equals(userHash2)
        );

        System.out.println(
                "USER_PASSWORD_MATCHES="
                        + encoder.matches("user123", userHash1)
        );
    }
}