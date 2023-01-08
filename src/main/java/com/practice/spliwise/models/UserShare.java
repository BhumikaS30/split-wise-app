package com.practice.spliwise.models;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

@Getter
public class UserShare {

    private String userId;

    private double share;

    private List<Contribution> contributions;

    public UserShare(String userId, double share) {
        this.userId = userId;
        this.share = share;
        this.contributions = new ArrayList<>();
    }
}
