package com.practice.spliwise.repository;

import java.util.HashMap;
import java.util.Map;

import com.practice.spliwise.models.Expense;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpenseRepository {

    public static Map<String, Expense> expenseRepository = new HashMap<>();

}
