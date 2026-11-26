package kz.iitu.springab.member;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @Mock
    private MemberRepository repository;

    private final Clock clock = Clock.fixed(
            Instant.parse("2026-11-26T12:00:00Z"), ZoneOffset.UTC);

    @Test
    void membershipIsInactiveWhenItExpiredYesterday() {
        MemberService service = new MemberService(repository, clock);

        assertFalse(service.isMembershipActive(LocalDate.of(2026, 11, 25)));
    }

    @Test
    void membershipIsActiveOnItsLastValidDay() {
        Member member = new Member(1L, LocalDate.of(2026, 11, 26));
        when(repository.findById(1L)).thenReturn(Optional.of(member));

        MemberService service = new MemberService(repository, clock);

        assertTrue(service.isMembershipActive(1L));
    }
    @Test
    void throwsWhenMemberIsNotFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());
        MemberService service = new MemberService(repository, clock);

        org.junit.jupiter.api.Assertions.assertThrows(
                java.util.NoSuchElementException.class,
                () -> service.isMembershipActive(99L));
    }
    @Test
    void registrationSavesMemberWithCorrectIdAndExpiryDate() {
        MemberService service = new MemberService(repository, clock);
        LocalDate expiry = LocalDate.of(2026, 12, 31);

        service.registerMember(7L, expiry);

        var captor = org.mockito.ArgumentCaptor.forClass(Member.class);
        org.mockito.Mockito.verify(repository).save(captor.capture());

        var saved = captor.getValue();
        org.junit.jupiter.api.Assertions.assertEquals(7L, saved.id());
        org.junit.jupiter.api.Assertions.assertEquals(expiry, saved.validUntil());
    }
}