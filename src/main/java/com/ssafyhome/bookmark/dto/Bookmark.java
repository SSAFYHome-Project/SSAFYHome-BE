package com.ssafyhome.bookmark.dto;

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
    private int bookmarkIdx;

    @ManyToOne
    @JoinColumn(name="mno")
    private User user;

    private String aptNm;

    private String estateAgentAggNm;

    private String umdNm;

    private int dealAmount;

    @CreationTimestamp
    private Date createdAt;
}
