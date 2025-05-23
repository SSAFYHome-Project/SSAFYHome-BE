package com.ssafyhome.community.board.dto;

import lombok.Getter;
import lombok.Setter;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class BoardDetailDto {
    private int boardIdx;
    private String boardTitle;
    private String boardContent;
    private int boardView;
    private String username;
    private LocalDateTime boardRegDate;
    private int boardRecommendCnt;
    private String userEmail;
    private CategoryType boardCategory;
    private byte[] profile;
    private List<String> imageUrls;

    public BoardDetailDto(int boardIdx, String boardTitle, String boardContent,
                          int boardView, String username, LocalDateTime boardRegDate, int boardRecommendCnt, String userEmail,
                          CategoryType boardCategory, byte[] profile) {
        this.boardIdx = boardIdx;
        this.boardTitle = boardTitle;
        this.boardContent = boardContent;
        this.boardView = boardView;
        this.username = username;
        this.boardRegDate = boardRegDate;
        this.boardRecommendCnt = boardRecommendCnt;
        this.userEmail = userEmail;
        this.boardCategory = boardCategory;
        this.profile = profile;
    }
}
