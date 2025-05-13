package com.ssafyhome.recentSearch.dto;

import com.ssafyhome.deal.dto.DealInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class RecentView {

    private String email;

    private List<DealInfo> dealInfo;   // 최근 본 매물 정보
}
