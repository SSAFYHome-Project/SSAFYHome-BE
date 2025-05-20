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
    private int replyIdx;

    private String replyContent;

    @ManyToOne
    @JoinColumn(name="boardIdx")
    private Board board;

    @ManyToOne
    @JoinColumn(name="mno")
    private User user;

    @CreationTimestamp
    private Date replyRegDate;
}
