package com.practice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.practice.domain.Member;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MemberDto {
    private Long id;

    private String username;
    @JsonIgnore
    private String password;

    private String intro;

    public static MemberDto from(Member member) {
        return MemberDto.builder()
                .id(member.getId())
                .username(member.getUsername())
                .password(member.getPassword())
                .intro(member.getIntro())
                .build();
    }
}
