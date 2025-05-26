package com.ssafyhome.deal.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Deal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "deal_id")
    private int dealId;

    @Column(name = "apt_name")
    private String aptName;

    @Enumerated(EnumType.STRING)
    @Column(name = "deal_type")
    private dealType dealType;

    @Column(name = "region_code")
    private String regionCode;

    @Column(name = "jibun")
    private String jibun;

    @Column(name = "deposit")
    private int deposit;

    @Column(name = "monthly_rent")
    private int monthlyRent;

    @Column(name = "deal_amount")
    private int dealAmount;

    @Column(name = "exclu_use_ar")
    private float excluUseAr;

    @Column(name = "deal_year")
    private int dealYear;

    @Column(name = "deal_month")
    private int dealMonth;

    @Column(name = "deal_day")
    private int dealDay;

    @Column(name = "floor")
    private int floor;

    @Column(name = "build_year")
    private int buildYear;
}
