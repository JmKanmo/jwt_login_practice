package com.practice.domain.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import javax.persistence.Id;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("refreshToken")
@Builder
public class RefreshToken {
    @Id
    private String id;
    private String refreshToken;

    @TimeToLive
    private Long expiration;

    public static RefreshToken from(String username, String refreshToken, Long expirationTime) {
        return RefreshToken.builder()
                .id(username)
                .refreshToken(refreshToken)
                .expiration(expirationTime / 1000)
                .build();
    }
}
