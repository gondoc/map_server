package com.gondo.map.config.security;

import com.gondo.map.component.MailSender;
import com.gondo.map.domain.member.entity.Member;
import com.gondo.map.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SecurityUserDetailService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final MailSender mailSender;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Member> opDto = memberRepository.findByEmail(email);
        if (opDto.isEmpty()) {
            throw new UsernameNotFoundException("아이디 또는 비밀번호가 잘못되었습니다.");
        } else {
            Member memberEntity = opDto.get();
            return new SecurityUser(memberEntity.toSecurityMemberRecord());
        }
    }
}
