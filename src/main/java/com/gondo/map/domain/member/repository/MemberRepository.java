package com.gondo.map.domain.member.repository;

import com.gondo.map.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("MemberRepository")
public interface MemberRepository extends JpaRepository<Member, String> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByNickName(String nick);

    Optional<Member> findMemberByRecoveryEmail(String email);
}
