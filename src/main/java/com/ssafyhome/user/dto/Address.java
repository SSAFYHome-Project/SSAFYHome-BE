package com.ssafyhome.user.dto;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ano")
    private int ano;

    @ManyToOne
    @JoinColumn(name="mno", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "title", nullable = false, length = 50)
    private TitleType title;

    @Column(name = "address", length = 500)
    private String address;

    @Column(name = "detail_address", length = 500)
    private String detailAddress;

    @Column(name = "x", length = 50)
    private String x;

    @Column(name = "y", length = 50)
    private String y;

}
