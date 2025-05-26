package com.ssafyhome.community.board.dto;

import com.ssafyhome.user.dto.User;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Getter
@Setter
@NoArgsConstructor
public class Board {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_idx")
    private int boardIdx;

    @Column(name = "board_reg_date")
    private LocalDateTime boardRegDate = LocalDateTime.now();

    @Column(name = "board_title", length = 200)
    private String boardTitle;

    @Column(name = "board_content", columnDefinition = "LONGTEXT")
    private String boardContent;

    @Column(name = "board_view")
    private int boardView;

    @Column(name = "board_recommend_cnt")
    private int boardRecommendCnt;

    @Column(name = "board_category")
    private CategoryType boardCategory;

    @ManyToOne
    @JoinColumn(name="mno")
    private User user;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BoardImage> images = new ArrayList<>();

    public void addImage(BoardImage image) {
        image.setBoard(this);
        this.images.add(image);
    }

    public void removeImage(BoardImage image) {
        this.images.remove(image);
        image.setBoard(null);
    }
}
