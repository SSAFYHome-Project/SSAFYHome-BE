package com.ssafyhome.deal.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Deal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int dealId;

    private String aptName;

    @Enumerated(EnumType.STRING)
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

}
