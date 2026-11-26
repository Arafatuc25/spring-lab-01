package kz.iitu.springab.member;

import java.time.LocalDate;

public record Member(long id, LocalDate validUntil) {}