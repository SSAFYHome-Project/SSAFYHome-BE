package com.ssafyhome.bookmark.dto;

import com.ssafyhome.deal.dto.Deal;
import com.ssafyhome.user.dto.User;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Data
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bookmark_idx")
    private int bookmarkIdx;

    @ManyToOne
    @JoinColumn(name="mno")
    private User user;

    @ManyToOne
    @JoinColumn(name="dealId")
    private Deal deal;


    @CreationTimestamp
    @Column(name = "reg_date")
    private Date regDate;
}
