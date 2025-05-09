package com.ssafyhome.user.dto;

import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterRequest {
    private String name;
    private String email;
    private String password;

    @JsonIgnore
    private MultipartFile profileImage;
}
