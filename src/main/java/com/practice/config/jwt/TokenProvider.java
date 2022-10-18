package com.practice.config.jwt;

import com.practice.exception.message.ExceptionMessage;
import com.practice.exception.model.TokenCheckFailException;
import com.practice.repository.MemberRepository;
import com.practice.service.MemberService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;

@Component
@RequiredArgsConstructor
public class TokenProvider {
    @Value("${spring.jwt.expire_time}")
    private long TOKEN_EXPIRE_TIME;

    @Value("${spring.jwt.secret}")
    private String secretKey;

    private static final String KEY_ROLES = "roles";

    private final MemberService memberService;

    /**
     * 토큰 생성(발급)
     *
     * @param username
     * @param roles
     * @return
     */
    public String generateToken(String username, List<String> roles) {
        // 다음 정보들을 포함한 claims 생성
        //      - username
        //      - roles
        //      - 생성 시간
        //      - 만료 시간
        //      - signature
        Claims claims = Jwts.claims().setSubject(username);
        claims.put(KEY_ROLES, roles);

        var now = new Date();
        var expireDate = new Date(now.getTime() + TOKEN_EXPIRE_TIME);

        // jwt 발급
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, this.secretKey).compact();
    }

    public Authentication getAuthentication(String jwt) {
        String tokenUserName = this.getUsername(jwt);

        if (tokenUserName == null) {
            throw new TokenCheckFailException(ExceptionMessage.MISMATCH_USERNAME_TOKEN);
        }

        UserDetails userDetails = memberService.loadUserByUsername(tokenUserName);

        if (!userDetails.getUsername().equals(tokenUserName)) {
            throw new TokenCheckFailException(ExceptionMessage.MISMATCH_USERNAME_TOKEN);
        }
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String getUsername(String token) {
        return this.parseClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        // TODO 추후에 로그아웃 관련 추가 검사 진행
        if (!StringUtils.hasText(token)) {
            throw new TokenCheckFailException(ExceptionMessage.FAIL_TOKEN_CHECK);
        } else if (this.parseClaims(token).getExpiration().before(new Date())) {
            throw new TokenCheckFailException(ExceptionMessage.TOKEN_VALID_TIME_EXPIRED);
        }
        return true;
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(this.secretKey).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            throw new TokenCheckFailException(ExceptionMessage.TOKEN_VALID_TIME_EXPIRED);
        } catch (Exception e) {
            throw new TokenCheckFailException(ExceptionMessage.FAIL_TOKEN_CHECK);
        }
    }
}
