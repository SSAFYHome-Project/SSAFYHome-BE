package com.ssafyhome.map.dao;

import com.ssafyhome.user.dto.TitleType;
import com.ssafyhome.user.dto.User;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Safety {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "safety_idx")
    private int safetyIdx;

    @Column(name = "sido", length = 50)
    private String sido;

    @Column(name = "sigungu", length = 50)
    private String sigungu;

    @Column(name = "total_index")
    private double totalIndex;

    @Column(name = "grade")
    private int grade;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "traffic_risk")
    private int trafficRisk;

    @Column(name = "fire_risk")
    private int fireRisk;

    @Column(name = "suicide_risk")
    private int suicideRisk;

    @Column(name = "disease_risk")
    private int diseaseRisk;

    @Column(name = "crime_risk")
    private int crimeRisk;

    @Column(name = "life_risk")
    private int lifeRisk;

}
