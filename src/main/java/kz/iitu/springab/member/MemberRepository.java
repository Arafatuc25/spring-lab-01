package kz.iitu.springab.member;

import java.util.Optional;

public interface MemberRepository {
    Optional<Member> findById(long id);
    Member save(Member member);
}