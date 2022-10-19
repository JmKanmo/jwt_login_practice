package com.practice.service.token;

import com.practice.domain.token.RefreshToken;
import com.practice.exception.message.ExceptionMessage;
import com.practice.exception.model.TokenNotFoundException;
import com.practice.repository.token.RefreshTokenRepository;
import com.practice.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenUtil jwtTokenUtil;

    public RefreshToken saveRefreshToken(String username, long expirationTime) {
        return refreshTokenRepository.save(RefreshToken.from(
                username, jwtTokenUtil.generateToken(username, expirationTime), expirationTime)
        );
    }

    public RefreshToken findRefreshTokenById(String username) {
        return refreshTokenRepository.findById(username).orElseThrow(() -> new TokenNotFoundException(ExceptionMessage.TOKEN_NOT_FOUND));
    }

    public void deleteRefreshTokenById(String username) {
        refreshTokenRepository.deleteById(username);
    }
}
