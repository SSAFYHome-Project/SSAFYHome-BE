package com.ssafyhome.community.reply.dto;

import com.ssafyhome.community.board.dto.Board;
import com.ssafyhome.user.dto.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Data
public class Reply {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_idx")
    private int replyIdx;

    @Column(name = "reply_content")
    private String replyContent;

    @ManyToOne
    @JoinColumn(name="board_idx")
    private Board board;

    @ManyToOne
    @JoinColumn(name="mno")
    private User user;

    @CreationTimestamp
    @Column(name = "reply_reg_date")
    private Date replyRegDate;
}
