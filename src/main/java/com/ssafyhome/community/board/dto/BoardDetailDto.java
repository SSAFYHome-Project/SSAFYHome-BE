package com.ssafyhome.community.board.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
public class BoardDetailDto {
    private int boardIdx;
    private String boardTitle;
    private String boardContent;
    private int boardView;
    private String userName;
    private String boardImage;
    private String boardRegDate;

    public BoardDetailDto(int boardIdx, String boardTitle, String boardContent,
                          int boardView, String userName, String boardImage, Date boardRegDate) {
        this.boardIdx = boardIdx;
        this.boardTitle = boardTitle;
        this.boardContent = boardContent;
        this.boardView = boardView;
        this.userName = userName;
        this.boardImage = boardImage;
        this.boardRegDate = formatDate(boardRegDate);
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
    }
}
