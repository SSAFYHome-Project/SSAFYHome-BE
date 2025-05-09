package com.ssafyhome.user.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class PatchRequest {
	private String name;
    private String email;
    private String password;
    private MultipartFile profile;
}
