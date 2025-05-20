package com.ssafyhome.community.board.dto;

import com.ssafyhome.user.dto.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Data
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int boardIdx;

    @CreationTimestamp
    private Date boardRegDate;

    private String boardTitle;

    private String boardContent;

    private int boardView;

    private int boardRecommendCnt;

    @Column(columnDefinition = "TEXT")
    private String boardImage;

    @ManyToOne
    @JoinColumn(name="mno")
    private User user;
}
