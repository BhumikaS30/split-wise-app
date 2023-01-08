package com.practice.spliwise.models;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpenseShare {

    private String expenseId;

    private ExpenseShareType expenseShareType;

    private double share;
}
