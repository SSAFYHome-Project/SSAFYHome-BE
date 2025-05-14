package com.ssafyhome.user.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class UserPatchRequest {
	private String name;
    private String password;
    private MultipartFile profile;
    private List<AddressDto> addresses;
}
