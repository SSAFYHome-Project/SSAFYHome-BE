package com.ssafyhome.bookmark.service;

import com.ssafyhome.bookmark.dao.BookmarkRepository;
import com.ssafyhome.bookmark.dto.Bookmark;
import com.ssafyhome.bookmark.dto.BookmarkInfo;
import com.ssafyhome.security.dto.CustomUserDetails;
import com.ssafyhome.user.dto.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;

    public List<Bookmark> getBookmark(CustomUserDetails userDetails) {
        if (userDetails == null) {
            throw new IllegalArgumentException("인증 정보가 유효하지 않습니다.");
        }

        User user = userDetails.getUser();

        if (user == null) {
            throw new EntityNotFoundException("사용자 정보를 찾을 수 없습니다.");
        }

        return bookmarkRepository.findByUser(userDetails.getUser());
    }

    public void saveBookmark(CustomUserDetails userDetails, BookmarkInfo bookmarkInfo) {
        if (userDetails == null) {
            throw new IllegalArgumentException("인증 정보가 유효하지 않습니다.");
        }

        User user = userDetails.getUser();

        if (user == null) {
            throw new EntityNotFoundException("사용자 정보를 찾을 수 없습니다.");
        }

        Bookmark bookmark = new Bookmark();
        bookmark.setUser(user);
        bookmark.setAptNm(bookmarkInfo.getAptNm());
        bookmark.setEstateAgentAggNm(bookmarkInfo.getEstateAgentAggNm());
        bookmark.setDealAmount(bookmarkInfo.getDealAmount());
        bookmark.setUmdNm(bookmarkInfo.getUmdNm());

        bookmarkRepository.save(bookmark);
    }

    public void deleteBookmark(CustomUserDetails userDetails, int bookmarkIdx) {
        if (userDetails == null) {
            throw new IllegalArgumentException("인증 정보가 유효하지 않습니다.");
        }

        User user = userDetails.getUser();

        if (user == null) {
            throw new EntityNotFoundException("사용자 정보를 찾을 수 없습니다.");
        }

        Bookmark bookmark = bookmarkRepository.findById(bookmarkIdx)
                .orElseThrow(() -> new EntityNotFoundException("해당 북마크를 찾을 수 없습니다."));
        
        if (bookmark.getUser().getMno() != user.getMno()) {
            throw new SecurityException("북마크로 해두지 않았습니다.");
        }

        bookmarkRepository.delete(bookmark);
    }
}
