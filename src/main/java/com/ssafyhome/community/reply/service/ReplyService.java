package com.ssafyhome.community.reply.service;

import com.ssafyhome.bookmark.dao.BookmarkRepository;
import com.ssafyhome.bookmark.dto.Bookmark;
import com.ssafyhome.deal.dao.DealRepository;
import com.ssafyhome.deal.dto.Deal;
import com.ssafyhome.deal.dto.DealInfo;
import com.ssafyhome.security.dto.CustomUserDetails;
import com.ssafyhome.user.dto.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReplyService {
    private final BookmarkRepository bookmarkRepository;
    private final DealRepository dealRepository;

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

    public void saveBookmark(CustomUserDetails userDetails, DealInfo bookmarkInfo) {
        if (userDetails == null) {
            throw new IllegalArgumentException("인증 정보가 유효하지 않습니다.");
        }

        User user = userDetails.getUser();

        if (user == null) {
            throw new EntityNotFoundException("사용자 정보를 찾을 수 없습니다.");
        }

        Deal deal = dealRepository
                .findByAptNameAndDealTypeAndRegionCodeAndJibunAndDealAmountAndDealYearAndDealMonthAndDealDayAndFloor(
                        bookmarkInfo.getAptName(), bookmarkInfo.getDealType(), bookmarkInfo.getRegionCode(), bookmarkInfo.getJibun(),
                        bookmarkInfo.getDealAmount(), bookmarkInfo.getDealYear(), bookmarkInfo.getDealMonth(), bookmarkInfo.getDealDay(), bookmarkInfo.getFloor()
                ).orElseGet(() -> dealRepository.save(bookmarkInfo.toEntity()));

        boolean exists = bookmarkRepository.findByUserAndDeal(user, deal).isPresent();
        if (exists) {
            throw new IllegalStateException("이미 등록된 즐겨찾기입니다.");
        }

        Bookmark bookmark = new Bookmark();
        bookmark.setUser(user);
        bookmark.setDeal(deal);
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
            throw new SecurityException("해당 사용자는 북마크로 해두지 않았습니다.");
        }

        bookmarkRepository.delete(bookmark);
    }
}
