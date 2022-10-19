package com.practice.dto;

import com.practice.util.JwtTokenUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenDto {
    private String grantType;
    private String accessToken;
    private String refreshToken;

    public static TokenDto from(String accessToken, String refreshToken) {
        return TokenDto.builder()
                .grantType(JwtTokenUtil.TOKEN_HEADER + ":" + JwtTokenUtil.TOKEN_PREFIX)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
