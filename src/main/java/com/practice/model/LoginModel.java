package com.practice.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginModel {
    private final String username;
    private final String password;
}
