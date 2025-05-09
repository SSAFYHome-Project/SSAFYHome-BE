package com.ssafyhome.user.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Blob;

@Getter
@Setter
public class RegisterRequest {
	private String name;
    private String email;
    private String password;
    private MultipartFile profileImage;
}
