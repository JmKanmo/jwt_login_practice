package com.practice.service.member;

import com.practice.domain.Member;
import com.practice.domain.token.LogoutAccessToken;
import com.practice.domain.token.RefreshToken;
import com.practice.dto.JwtTokenDto;
import com.practice.dto.MemberDto;
import com.practice.exception.message.ExceptionMessage;
import com.practice.exception.model.TokenCheckFailException;
import com.practice.exception.model.UserAuthException;
import com.practice.model.LoginModel;
import com.practice.model.MemberModel;
import com.practice.repository.member.MemberRepository;
import com.practice.service.token.LogoutAccessTokenService;
import com.practice.service.token.RefreshTokenService;
import com.practice.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    private final JwtTokenUtil jwtTokenUtil;

    private final RefreshTokenService refreshTokenService;

    private final LogoutAccessTokenService logoutAccessTokenService;

    @Transactional(readOnly = true)
    @Override
    public JwtTokenDto login(LoginModel loginModel) {
        Member member = authenticate(loginModel);
        String token = this.jwtTokenUtil.generateToken(member.getUsername(), JwtTokenUtil.ACCESS_TOKEN_EXPIRE_TIME);
        RefreshToken refreshToken = refreshTokenService.saveRefreshToken(member.getUsername(), JwtTokenUtil.REFRESH_TOKEN_EXPIRE_TIME);
        jwtTokenUtil.setRefreshTokenAtCookie(refreshToken);
        return JwtTokenDto.from(token);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("회원 정보를 찾을 수 없습니다."));
    }


    @Transactional
    public Long register(MemberModel memberModel) {
        String username = memberModel.getUsername();

        if (this.memberRepository.existsByUsername(username)) {
            throw new UserAuthException("이미 존재하는 회원입니다.");
        }

        memberModel.setPassword(this.passwordEncoder.encode(memberModel.getPassword()));
        return memberRepository.save(Member.from(memberModel)).getId();
    }

    public Member authenticate(LoginModel loginModel) {
        String username = loginModel.getUsername();

        if (!this.memberRepository.existsByUsername(username)) {
            throw new UserAuthException("회원 정보를 찾을 수 없습니다");
        }

        Member member = this.memberRepository.findByUsername(username).get();

        if (!this.passwordEncoder.matches(loginModel.getPassword(), member.getPassword())) {
            throw new UserAuthException(ExceptionMessage.MISMATCH_PASSWORD);
        }
        return member;
    }

    public void logout(String accessToken) {
        String username = jwtTokenUtil.parseToken(jwtTokenUtil.resolveToken(accessToken));
        accessToken = jwtTokenUtil.resolveToken(accessToken);
        long remainTime = jwtTokenUtil.getRemainTime(accessToken);
        refreshTokenService.deleteRefreshTokenById(username);
        logoutAccessTokenService.saveLogoutAccessToken(LogoutAccessToken.from(username, accessToken, remainTime));
    }

    @Override
    public JwtTokenDto reissue(String refreshToken, Principal principal) {
        if (principal == null || principal.getName() == null) {
            throw new UserAuthException(ExceptionMessage.NOT_AUTHORIZED_ACCESS);
        }

        String curUserName = principal.getName();
        RefreshToken redisRefreshToken = refreshTokenService.findRefreshTokenById(curUserName);

        if (refreshToken == null || !refreshToken.equals(redisRefreshToken.getRefreshToken())) {
            throw new TokenCheckFailException(ExceptionMessage.MISMATCH_TOKEN);
        }
        return createRefreshToken(refreshToken, curUserName);
    }

    @Transactional(readOnly = true)
    @Override
    public MemberDto findMemberById(String username) {
        Member member = memberRepository.findByUsername(username).orElseThrow(() -> new UserAuthException(ExceptionMessage.USER_NOT_FOUND));
        return MemberDto.from(member);
    }

    private JwtTokenDto createRefreshToken(String refreshToken, String username) {
        if (lessThanReissueExpirationTimesLeft(refreshToken)) {
            String accessToken = jwtTokenUtil.generateToken(username, JwtTokenUtil.ACCESS_TOKEN_EXPIRE_TIME);
            RefreshToken newRedisToken = refreshTokenService.saveRefreshToken(username, JwtTokenUtil.REFRESH_TOKEN_EXPIRE_TIME);
            jwtTokenUtil.setRefreshTokenAtCookie(newRedisToken);
            return JwtTokenDto.from(accessToken);
        }
        return JwtTokenDto.from(jwtTokenUtil.generateToken(username, JwtTokenUtil.ACCESS_TOKEN_EXPIRE_TIME));
    }

    private boolean lessThanReissueExpirationTimesLeft(String refreshToken) {
        return jwtTokenUtil.getRemainTime(refreshToken) < JwtTokenUtil.REISSUE_EXPIRE_TIME;
    }
}
