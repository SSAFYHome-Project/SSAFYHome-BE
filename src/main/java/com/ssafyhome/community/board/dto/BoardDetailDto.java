package com.ssafyhome.community.board.dto;

import lombok.Getter;

import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
public class BoardDetailDto {
    private int boardIdx;
    private String boardTitle;
    private String boardContent;
    private int boardView;
    private String username;
    private String boardRegDate;
    private int boardRecommendCnt;
    private String userEmail;

    public BoardDetailDto(int boardIdx, String boardTitle, String boardContent,
                          int boardView, String username, Date boardRegDate, int boardRecommendCnt, String userEmail) {
        this.boardIdx = boardIdx;
        this.boardTitle = boardTitle;
        this.boardContent = boardContent;
        this.boardView = boardView;
        this.username = username;
        this.boardRegDate = formatDate(boardRegDate);
        this.boardRecommendCnt = boardRecommendCnt;
        this.userEmail = userEmail;
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
    }
}
