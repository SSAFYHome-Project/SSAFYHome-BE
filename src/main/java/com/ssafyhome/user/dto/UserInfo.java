package com.ssafyhome.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
	private String name;
    private String email;
    private String password;
    private String role;
    private List<AddressDto> address;
    private byte[] profile;
}
