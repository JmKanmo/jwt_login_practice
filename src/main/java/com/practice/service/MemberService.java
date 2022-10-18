package com.practice.service;

import com.practice.domain.Member;
import com.practice.model.LoginModel;
import com.practice.model.MemberModel;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface MemberService extends UserDetailsService {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
    Long register(MemberModel memberModel);
    Member authenticate(LoginModel loginModel);
}
