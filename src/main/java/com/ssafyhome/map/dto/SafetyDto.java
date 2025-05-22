package com.ssafyhome.map.dto;

import com.ssafyhome.map.dao.Safety;
import lombok.Data;
import lombok.Getter;

@Getter
public class SafetyDto {
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

    public SafetyDto(Safety safety) {
        this.sido = safety.getSido();
        this.sigungu = safety.getSigungu();
        this.totalIndex = safety.getTotalIndex();
        this.grade = safety.getGrade();
        this.status = safety.getStatus();
        this.trafficRisk = safety.getTrafficRisk();
        this.fireRisk = safety.getFireRisk();
        this.suicideRisk = safety.getSuicideRisk();
        this.diseaseRisk = safety.getDiseaseRisk();
        this.crimeRisk = safety.getCrimeRisk();
        this.lifeRisk = safety.getLifeRisk();
    }

}
