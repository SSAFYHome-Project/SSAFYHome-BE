package com.ssafyhome.user.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UserRegisterRequest {
	private String name;
    private String email;
    private String password;
    private MultipartFile profileImage;
}
