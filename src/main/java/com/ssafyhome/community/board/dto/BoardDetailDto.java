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
    private CategoryType boardCategory;
    private byte[] profile;

    public BoardDetailDto(int boardIdx, String boardTitle, String boardContent,
                          int boardView, String username, Date boardRegDate, int boardRecommendCnt, String userEmail,
                          CategoryType boardCategory, byte[] profile) {
        this.boardIdx = boardIdx;
        this.boardTitle = boardTitle;
        this.boardContent = boardContent;
        this.boardView = boardView;
        this.username = username;
        this.boardRegDate = formatDate(boardRegDate);
        this.boardRecommendCnt = boardRecommendCnt;
        this.userEmail = userEmail;
        this.boardCategory = boardCategory;
        this.profile = profile;
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
    }
}
