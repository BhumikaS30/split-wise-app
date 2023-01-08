package com.practice.spliwise.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import com.practice.spliwise.exceptions.ExpenseDoesNotExistException;
import com.practice.spliwise.models.Contribution;
import com.practice.spliwise.models.Expense;
import com.practice.spliwise.models.ExpenseGroup;
import com.practice.spliwise.models.ExpenseShareType;
import com.practice.spliwise.models.ExpenseStatus;
import com.practice.spliwise.models.User;
import com.practice.spliwise.models.UserShare;
import com.practice.spliwise.repository.ExpenseRepository;
import com.practice.spliwise.repository.UserRepository;

import static com.practice.spliwise.models.ExpenseStatus.CREATED;

public class ExpenseService {

    private NotificationService notificationService = new NotificationServiceImpl();

    public Expense createExpense(String title, String description, LocalDateTime expenseDate, double expenseAmount,
                                 String userId) {

        Expense expense = Expense.builder()
                                 .id(UUID.randomUUID().toString())
                                 .title(title)
                                 .description(description)
                                 .expenseDate(expenseDate)
                                 .expenseAmount(expenseAmount)
                                 .userId(userId)
                                 .expenseStatus(CREATED)
                                 .expenseGroup(new ExpenseGroup())
                                 .build();

        ExpenseRepository.expenseRepository.putIfAbsent(expense.getId(), expense);
        return expense;
    }

    public void addUserToExpenseGroup(String emailId, String expenseId) throws ExpenseDoesNotExistException {

        if (!ExpenseRepository.expenseRepository.containsKey(expenseId)) {
            throw new ExpenseDoesNotExistException("Please create the expense! As it doesn't exist...");
        }
        Expense expense = ExpenseRepository.expenseRepository.get(expenseId);
        User user = UserRepository.userRepository.get(emailId);
        expense.getExpenseGroup()
               .getGroupMembers().add(user);

        notificationService.notifyUser(user,
                                       user.getUserName() + " You have been added to the expense group : " + expense.getTitle());
    }

    public void setExpenseStatus(ExpenseStatus expenseStatus, String expenseId) throws ExpenseDoesNotExistException {
        if (!ExpenseRepository.expenseRepository.containsKey(expenseId)) {
            throw new ExpenseDoesNotExistException("Please create the expense! As it doesn't exist...");
        }
        ExpenseRepository.expenseRepository.get(expenseId).setExpenseStatus(expenseStatus);
    }

    /**
     * Manually assigns the share
     *
     * @param emailId
     * @param share
     * @param expenseId
     *
     * @throws ExpenseDoesNotExistException
     */
    public void assignExpenseShare(String emailId, double share, String expenseId) throws ExpenseDoesNotExistException {
        if (!ExpenseRepository.expenseRepository.containsKey(expenseId)) {
            throw new ExpenseDoesNotExistException("Please create the expense! As it doesn't exist...");
        }
        Expense expense = ExpenseRepository.expenseRepository.get(expenseId);
        expense.getExpenseGroup().getUserContributions()
               .putIfAbsent(emailId, new UserShare(emailId, share));
    }

    /**
     * Sets the expense share based on the ExpenseShareType - Equal, percentage or custom
     *
     * @param emailId
     * @param expenseId
     * @param expenseShareType
     * @param percent
     * @param share
     *
     * @throws ExpenseDoesNotExistException
     */
    public void setExpenseShare(String emailId, String expenseId, ExpenseShareType expenseShareType,
                                Optional<Double> percent,
                                Optional<Double> share) throws ExpenseDoesNotExistException {
        if (!ExpenseRepository.expenseRepository.containsKey(expenseId)) {
            throw new ExpenseDoesNotExistException("Please create the expense! As it doesn't exist...");
        }
        Expense expense = ExpenseRepository.expenseRepository.get(expenseId);
        expense.getExpenseGroup().getUserContributions()
               .putIfAbsent(emailId, new UserShare(emailId, getShare(expense, expenseShareType, percent, share)));
    }

    private double getShare(Expense expense, ExpenseShareType expenseShareType, Optional<Double> percent,
                            Optional<Double> share) {
        switch (expenseShareType) {
            case EQUAL:
                return expense.getExpenseAmount() / expense.getExpenseGroup().getGroupMembers().size();
            case PERCENT:
                if (percent.isPresent()) {
                    return expense.getExpenseAmount() * (percent.get() / 100);
                }
                break;
            case CUSTOM:
                if (share.isPresent()) {
                    return share.get();
                }
                break;
        }
        return 0;
    }

    public boolean isExpenseSettled(String expenseId) throws ExpenseDoesNotExistException {
        if (!ExpenseRepository.expenseRepository.containsKey(expenseId)) {
            throw new ExpenseDoesNotExistException("Please create the expense! As it doesn't exist...");
        }
        Expense expense = ExpenseRepository.expenseRepository.get(expenseId);

        Map<String, UserShare> userContributions = expense.getExpenseGroup().getUserContributions();

        double total = expense.getExpenseAmount();

        for (Map.Entry<String, UserShare> entry : userContributions.entrySet()) {
            UserShare value = entry.getValue();
            for (Contribution contribution : value.getContributions()) {
                total -= contribution.getContributionValue();
            }
        }
        return total <= 1;
    }
}
