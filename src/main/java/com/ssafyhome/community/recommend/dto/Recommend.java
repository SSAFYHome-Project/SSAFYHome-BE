package com.ssafyhome.community.recommend.dto;

import com.ssafyhome.community.board.dto.Board;
import com.ssafyhome.user.dto.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Recommend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int bookmarkIdx;

    @ManyToOne
    @JoinColumn(name="mno")
    private User user;

    @ManyToOne
    @JoinColumn(name="boardIdx")
    private Board board;

}
