package com.ssafyhome.user.dto;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ano;

    @ManyToOne
    @JoinColumn(name="mno")
    private User user;

    @Enumerated(EnumType.STRING)
    private TitleType title;

    private String address;

    private String detailAddress;

    private String x;

    private String y;

}
