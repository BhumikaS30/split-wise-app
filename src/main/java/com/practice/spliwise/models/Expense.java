package com.practice.spliwise.models;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Expense {

    private String id;

    private String userId;

    private String title;

    private String description;

    private double expenseAmount;

    private LocalDateTime expenseDate;

    private ExpenseGroup expenseGroup;

    private ExpenseStatus expenseStatus;
}
