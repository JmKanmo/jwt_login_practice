package com.practice.service.member;

import com.practice.domain.Member;
import com.practice.dto.JwtTokenDto;
import com.practice.dto.MemberDto;
import com.practice.model.LoginModel;
import com.practice.model.MemberModel;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.security.Principal;

public interface MemberService extends UserDetailsService {
    JwtTokenDto login(LoginModel loginModel);

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    Long register(MemberModel memberModel);

    Member authenticate(LoginModel loginModel);

    void logout(String accessToken);

    JwtTokenDto reissue(String refreshToken, Principal principal);

    MemberDto findMemberById(String username);
}
