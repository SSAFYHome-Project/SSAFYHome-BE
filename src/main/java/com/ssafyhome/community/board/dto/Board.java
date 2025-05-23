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
    private int boardIdx;

    @Column
    private LocalDateTime boardRegDate = LocalDateTime.now();

    private String boardTitle;

    private String boardContent;

    private int boardView;

    private int boardRecommendCnt;

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
