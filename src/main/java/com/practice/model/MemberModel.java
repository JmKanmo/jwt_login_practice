package com.practice.model;

import lombok.Builder;
import lombok.Data;

@Data
public class MemberModel {
    private String username;
    private String password;
    private String intro;
}
