package com.example.afyabora.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterUserDto {
    private String email;
    private String password;
    private String username;
    private String role; // "Patient" or "Doctor"
    private String specialty; // Required if role is "Doctor"
    private String location;  // Required if role is "Doctor"
}
