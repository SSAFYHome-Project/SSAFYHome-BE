package com.ssafyhome.community.board.dto;

import com.ssafyhome.user.dto.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.text.SimpleDateFormat;
import java.util.Date;

@Getter
public class AllBoardDto {

    private int boardIdx;
    private String boardTitle;
    private String username;
    private String boardRegDate;

    public AllBoardDto(int boardIdx, String boardTitle,
                          String username, Date boardRegDate) {
        this.boardIdx = boardIdx;
        this.boardTitle = boardTitle;
        this.username = username;
        this.boardRegDate = formatDate(boardRegDate);
    }

    private String formatDate(Date date) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(date);
    }

}
