package com.practice.spliwise.exceptions;

public class ExpenseDoesNotExistException extends Exception{

    public ExpenseDoesNotExistException(String message) {
        super(message);
    }
}
