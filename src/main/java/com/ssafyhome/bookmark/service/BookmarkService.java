package com.ssafyhome.bookmark.service;

import com.ssafyhome.bookmark.dao.BookmarkRepository;
import com.ssafyhome.bookmark.dto.Bookmark;
import com.ssafyhome.common.exception.BusinessException;
import com.ssafyhome.common.exception.ErrorCode;
import com.ssafyhome.common.util.UserUtils;
import com.ssafyhome.deal.dto.DealInfo;
import com.ssafyhome.deal.dao.DealRepository;
import com.ssafyhome.deal.dto.Deal;
import com.ssafyhome.security.dto.CustomUserDetails;
import com.ssafyhome.user.dto.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final DealRepository dealRepository;

    public List<Bookmark> getBookmark(CustomUserDetails userDetails) {
        User user = UserUtils.getUserFromUserDetails(userDetails);

        return bookmarkRepository.findByUser(user);
    }

    public void saveBookmark(CustomUserDetails userDetails, DealInfo bookmarkInfo) {
        User user = UserUtils.getUserFromUserDetails(userDetails);
        Deal deal = findOrCreateDeal(bookmarkInfo);

        if (bookmarkRepository.findByUserAndDeal(user, deal).isPresent()) {
            throw new BusinessException(ErrorCode.BOOKMARK_ALREADY_EXISTS, "이미 등록된 즐겨찾기입니다.");
        }


        // 3. 즐겨찾기 저장
        Bookmark bookmark = new Bookmark();
        bookmark.setUser(user);
        bookmark.setDeal(deal);
        bookmarkRepository.save(bookmark);
    }

    public void deleteBookmark(CustomUserDetails userDetails, int bookmarkIdx) {
        User user = UserUtils.getUserFromUserDetails(userDetails);

        Bookmark bookmark = bookmarkRepository.findById(bookmarkIdx)
                .orElseThrow(() -> new EntityNotFoundException("해당 북마크를 찾을 수 없습니다."));

        if (bookmark.getUser().getMno() != user.getMno()) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED, "해당 사용자는 북마크로 해두지 않았습니다.");
        }

        bookmarkRepository.delete(bookmark);
    }

    private Deal findOrCreateDeal(DealInfo dealInfo) {
        return dealRepository
                .findByAptNameAndDealTypeAndRegionCodeAndJibunAndDealAmountAndDealYearAndDealMonthAndDealDayAndFloor(
                        dealInfo.getAptName(), dealInfo.getDealType(), dealInfo.getRegionCode(), dealInfo.getJibun(),
                        dealInfo.getDealAmount(), dealInfo.getDealYear(), dealInfo.getDealMonth(), dealInfo.getDealDay(), dealInfo.getFloor()
                )
                .orElseGet(() -> {
                    Deal newDeal = dealInfo.toEntity();
                    return dealRepository.save(newDeal);
                });
    }
}
