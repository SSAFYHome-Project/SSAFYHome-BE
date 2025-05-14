package com.ssafyhome.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDto {
    private TitleType title;
    private String address;
    private String detailAddress;
    private String x;
    private String y;
}

