package com.practice.spliwise.models;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Contribution {

    private String id;

    private double contributionValue;

    private String description;

    private LocalDateTime contributionDate;

    private String transactionId;

    private String transactionDescription;
}
