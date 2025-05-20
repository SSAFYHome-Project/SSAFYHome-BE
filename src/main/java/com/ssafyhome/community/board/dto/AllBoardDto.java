package com.ssafyhome.community.board.dto;

import com.ssafyhome.user.dto.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Getter
@AllArgsConstructor
public class AllBoardDto {

    private int boardIdx;
    private String title;
    private String name;
    private int boardView;
    private Date createTime;
}
