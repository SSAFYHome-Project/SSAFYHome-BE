package com.ssafyhome.deal.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;


@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DealInfo {
    private String aptName;
    private dealType dealType;
    private String regionCode;
    private String jibun;
    private int deposit;
    private int monthlyRent;
    private int dealAmount;
    private float excluUseAr;
    private int dealYear;
    private int dealMonth;
    private int dealDay;
    private int floor;
    private int buildYear;

    public Deal toEntity() {
        return Deal.builder()
                .aptName(aptName)
                .dealType(dealType)
                .regionCode(regionCode)
                .jibun(jibun)
                .deposit(deposit)
                .monthlyRent(monthlyRent)
                .dealAmount(dealAmount)
                .excluUseAr(excluUseAr)
                .dealYear(dealYear)
                .dealMonth(dealMonth)
                .dealDay(dealDay)
                .floor(floor)
                .buildYear(buildYear)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DealInfo that = (DealInfo) o;
        return dealAmount == that.dealAmount &&
                dealYear == that.dealYear &&
                dealMonth == that.dealMonth &&
                dealDay == that.dealDay &&
                floor == that.floor &&
                aptName.equals(that.aptName) &&
                dealType == that.dealType &&
                regionCode.equals(that.regionCode) &&
                jibun.equals(that.jibun);
    }

    @Override
    public int hashCode() {
        return Objects.hash(aptName, dealType, regionCode, jibun, dealAmount, dealYear, dealMonth, dealDay, floor);
    }

}


