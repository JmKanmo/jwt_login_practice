package com.practice.domain.token;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import javax.persistence.Id;

@Data
@Builder
@RedisHash("logoutAccessToken")
@NoArgsConstructor
@AllArgsConstructor
public class LogoutAccessToken {
    @Id
    private String id;

    private String username;

    @TimeToLive
    private Long expiration;

    public static LogoutAccessToken from(String username, String accessToken, Long expirationTime) {
        return LogoutAccessToken.builder()
                .id(accessToken)
                .username(username)
                .expiration(expirationTime / 1000)
                .build();
    }
}
