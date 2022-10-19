package com.practice.util;

import com.practice.exception.message.ExceptionMessage;
import com.practice.exception.model.TokenCheckFailException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Date;
import java.util.List;

@Component
@Data
public class JwtTokenUtil {
    // Token
    public static final Long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 30; // ACCESS 토근 만료 시간: 30분
    public static final Long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 7; // Refresh 토큰 만료 시간 : 7일

    public static final Long REISSUE_EXPIRE_TIME = 1000L * 60 * 60 * 24 * 7; // Reissue 만료 시간 : 3일

    // header
    public static final String TOKEN_HEADER = "Authorization";
    public static final String TOKEN_PREFIX = "Bearer-";

    private static final String KEY_ROLES = "roles";

    @Value("${spring.jwt.secret}")
    private String secretKey;

    public String generateToken(String username, long expirationTime) {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("username", username);
        // claims.put(KEY_ROLES, roles);

        var now = new Date();
        var expireDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expireDate)
                .signWith(SignatureAlgorithm.HS512, secretKey).compact();
    }

    public long getRemainTime(String token) {
        Date expiration = parseClaims(token).getExpiration();
        Date now = new Date();
        return expiration.getTime() - now.getTime();
    }

    public String parseToken(String token) {
        return parseClaims(token).getSubject();
    }

    public String resolveToken(String token) {
        if (!ObjectUtils.isEmpty(token) && token.startsWith(JwtTokenUtil.TOKEN_PREFIX)) {
            return token.substring(JwtTokenUtil.TOKEN_PREFIX.length());
        }
        return null;
    }

    public Claims parseClaims(String token) {
        try {
            return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            throw new TokenCheckFailException(ExceptionMessage.TOKEN_VALID_TIME_EXPIRED);
        } catch (Exception e) {
            throw new TokenCheckFailException(ExceptionMessage.FAIL_TOKEN_CHECK);
        }
    }
}
