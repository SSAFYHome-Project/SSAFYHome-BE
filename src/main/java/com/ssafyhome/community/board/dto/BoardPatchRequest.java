package com.ssafyhome.community.board.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class BoardPatchRequest {
    private String title;
    private String content;
    private CategoryType category;
    private List<MultipartFile> newImages;
    private List<String> imagesToDelete;
}

