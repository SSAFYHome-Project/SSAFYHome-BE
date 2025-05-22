package com.ssafyhome.community.board.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BoardPatchRequest {
    private String title;
    private String content;
    private CategoryType category;
}

