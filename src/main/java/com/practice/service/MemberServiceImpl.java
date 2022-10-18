package com.practice.service;

import com.practice.domain.Member;
import com.practice.exception.message.ExceptionMessage;
import com.practice.exception.model.UserAuthException;
import com.practice.model.LoginModel;
import com.practice.model.MemberModel;
import com.practice.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("couldn't find user -> " + username));
    }

    @Transactional
    public Long register(MemberModel memberModel) {
        String username = memberModel.getUsername();

        if (this.memberRepository.existsByUsername(username)) {
            throw new UserAuthException("이미 존재하는 회원입니다. -> " + username);
        }

        memberModel.setPassword(this.passwordEncoder.encode(memberModel.getPassword()));
        return memberRepository.save(Member.from(memberModel)).getId();
    }

    @Transactional(readOnly = true)
    public Member authenticate(LoginModel loginModel) {
        String username = loginModel.getUsername();

        if (!this.memberRepository.existsByUsername(username)) {
            throw new UserAuthException("회원 정보를 찾을 수 없습니다. -> " + username);
        }

        Member member = this.memberRepository.findByUsername(username).get();

        if (!this.passwordEncoder.matches(loginModel.getPassword(), member.getPassword())) {
            throw new UserAuthException(ExceptionMessage.MISMATCH_PASSWORD);
        }
        return member;
    }
}
