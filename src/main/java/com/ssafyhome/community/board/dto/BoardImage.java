package com.ssafyhome.community.board.dto;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "board_image")
@Getter
@Setter
@NoArgsConstructor
public class BoardImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int imageIdx;

    @Column(nullable = false)
    private String imageUrl;

    @Column(nullable = false)
    private String s3Key;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_idx")
    private Board board;

    public BoardImage(String imageUrl, String s3Key) {
        this.imageUrl = imageUrl;
        this.s3Key = s3Key;
    }
}
