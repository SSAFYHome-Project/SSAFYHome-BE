package com.ssafyhome.community.board.dto;


import lombok.Getter;
import java.text.SimpleDateFormat;
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
                          String username, Date boardRegDate, CategoryType boardCategory, byte[] profile) {
        this.boardIdx = boardIdx;
        this.boardTitle = boardTitle;
        this.username = username;
        this.boardRegDate = formatDate(boardRegDate);
        this.boardCategory = boardCategory;
        this.profile = profile;
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
    }

}
