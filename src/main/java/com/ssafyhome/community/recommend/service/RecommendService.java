package com.ssafyhome.community.recommend.service;

import com.ssafyhome.bookmark.dto.Bookmark;
import com.ssafyhome.community.recommend.dao.RecommendRepository;
import com.ssafyhome.deal.dao.DealRepository;
import com.ssafyhome.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendService {
    private final RecommendRepository bookmarkRepository;
    private final DealRepository dealRepository;

    public List<Bookmark> getBookmark(CustomUserDetails userDetails) {
        return null;
    }


}
