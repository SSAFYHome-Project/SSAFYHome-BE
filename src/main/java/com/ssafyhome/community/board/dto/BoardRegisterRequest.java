package com.ssafyhome.community.board.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardRegisterRequest {
    private String title;
    private String content;
    private String image;
}

