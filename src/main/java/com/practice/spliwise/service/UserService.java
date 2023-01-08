package com.practice.spliwise.service;

import com.practice.spliwise.exceptions.ContributionExceededException;
import com.practice.spliwise.exceptions.ExpenseDoesNotExistException;
import com.practice.spliwise.exceptions.ExpenseSettledException;
import com.practice.spliwise.models.Contribution;
import com.practice.spliwise.models.Expense;
import com.practice.spliwise.models.ExpenseGroup;
import com.practice.spliwise.models.User;
import com.practice.spliwise.models.UserShare;
import com.practice.spliwise.repository.ExpenseRepository;
import com.practice.spliwise.repository.UserRepository;

import static com.practice.spliwise.models.ExpenseStatus.SETTLED;

public class UserService {

    public User createUser(String email, String name, String phoneNumber) {
        User user = new User(email, name, phoneNumber);
        UserRepository.userRepository.putIfAbsent(user.getUserId(), user);
        return user;
    }

    public void contributeToExpense(String expenseId, String emailId, Contribution contribution)
        throws ExpenseDoesNotExistException, ExpenseSettledException, ContributionExceededException {
        if (!ExpenseRepository.expenseRepository.containsKey(expenseId)) {
            throw new ExpenseDoesNotExistException("Please create the expense! As it doesn't exist...");
        }
        if (!UserRepository.userRepository.containsKey(emailId)) {
            throw new ExpenseDoesNotExistException("User Doesn't exist!");
        }

        Expense expense = ExpenseRepository.expenseRepository.get(expenseId);
        ExpenseGroup expenseGroup = expense.getExpenseGroup();
        UserShare userShare = expenseGroup.getUserContributions().get(emailId);

        if (SETTLED.equals(expense.getExpenseStatus())) {
            throw new ExpenseSettledException("This expense is already settled!!");
        }

        if (contribution.getContributionValue() > userShare.getShare()) {
            throw new ContributionExceededException("Contribution Exceeded for this expense!!");
        }
        userShare.getContributions().add(contribution);
    }

}
