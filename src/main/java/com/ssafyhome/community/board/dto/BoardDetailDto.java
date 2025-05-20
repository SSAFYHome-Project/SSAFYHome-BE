package com.ssafyhome.community.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BoardDetailDto {
    private int boardIdx;
    private String boardTitle;
    private String boardContent;
    private int boardView;
    private String userName;
    private String boardImage;
    private String boardRegDate; // 필요 시 String으로 포맷팅
}
