package com.ssafyhome.community.board.dto;


import lombok.Getter;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

@Getter
public class AllBoardDto {

    private int boardIdx;
    private String boardTitle;
    private String username;
    private String boardRegDate;
    private CategoryType boardCategory;
    private byte[] profile;

    public AllBoardDto(int boardIdx, String boardTitle,
                          String username, LocalDateTime boardRegDate, CategoryType boardCategory, byte[] profile) {
        this.boardIdx = boardIdx;
        this.boardTitle = boardTitle;
        this.username = username;
        this.boardRegDate = boardRegDate.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.boardCategory = boardCategory;
        this.profile = profile;
    }

}
