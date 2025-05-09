package com.ssafyhome.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
	private String name;
    private String email;
    private String password;
    private String role;
    private byte[] profile;
}
