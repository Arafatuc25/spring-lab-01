package kz.iitu.springab.member;

import java.time.Clock;
import java.time.LocalDate;

public class MemberService {
    private final MemberRepository repository;
    private final Clock clock;

    public MemberService(MemberRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public boolean isMembershipActive(LocalDate validUntil) {
        return !validUntil.isBefore(LocalDate.now(clock));
    }

    public boolean isMembershipActive(long memberId) {
        Member member = repository.findById(memberId).orElseThrow();
        return isMembershipActive(member.validUntil());
    }
    public void registerMember(long id, LocalDate validUntil) {
        repository.save(new Member(id, validUntil));
    }
}