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
    private int safetyIdx;

    private String sido;

    private String sigungu;

    private double totalIndex;

    private int grade;

    private String status;

    private int trafficRisk;

    private int fireRisk;

    private int suicideRisk;

    private int diseaseRisk;

    private int crimeRisk;

    private int lifeRisk;

}
