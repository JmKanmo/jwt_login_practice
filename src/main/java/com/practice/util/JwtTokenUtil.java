package com.practice.util;

import com.practice.domain.token.RefreshToken;
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
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

@Component
@Data
public class JwtTokenUtil {
    // Token
    public static final Long ACCESS_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 1; // ACCESS 토근 만료 시간: 1시간
    public static final Long REFRESH_TOKEN_EXPIRE_TIME = 1000L * 60 * 60 * 6; // Refresh 토큰 만료 시간 : 6시간
    public static final Long REISSUE_EXPIRE_TIME = 1000L * 60 * 60 * 3; // Reissue 만료 시간 : 3시간

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

    public void setRefreshTokenAtCookie(RefreshToken refreshToken) {
        Cookie cookie = new Cookie("RefreshToken", refreshToken.getRefreshToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(refreshToken.getExpiration().intValue());
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes()).getResponse();
        response.addCookie(cookie);
    }
}
