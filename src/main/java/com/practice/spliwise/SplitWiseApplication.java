package com.practice.spliwise;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Optional;
import java.util.Set;

import com.practice.spliwise.exceptions.ContributionExceededException;
import com.practice.spliwise.exceptions.ExpenseDoesNotExistException;
import com.practice.spliwise.exceptions.ExpenseSettledException;
import com.practice.spliwise.models.Contribution;
import com.practice.spliwise.models.Expense;
import com.practice.spliwise.models.ExpenseGroup;
import com.practice.spliwise.models.User;
import com.practice.spliwise.models.UserShare;
import com.practice.spliwise.repository.ExpenseRepository;
import com.practice.spliwise.service.ExpenseService;
import com.practice.spliwise.service.UserService;

import static com.practice.spliwise.models.ExpenseShareType.EQUAL;
import static com.practice.spliwise.models.ExpenseShareType.PERCENT;
import static com.practice.spliwise.models.ExpenseStatus.CREATED;
import static com.practice.spliwise.models.ExpenseStatus.PENDING;
import static java.util.Optional.empty;

public class SplitWiseApplication {

    public static final String BHUMIKA_GMAIL = "bhumika@gmail.com";

    public static final String PRATIK_GMAIL = "pratik@gmail.com";

    public static final String YASHU_GMAIL = "yashu@gmail.com";

    public static final String PRANIT_GMAIL = "pranit@gmail.com";

    static ExpenseService expenseService;

    static UserService userService;

    public static void main(
        String[] args) throws ExpenseSettledException, ExpenseDoesNotExistException, ContributionExceededException {

        expenseService = new ExpenseService();
        userService = new UserService();

        createTestUsers();

        Expense lunchExpense = createLunchExpense();

        try {
            expenseService.setExpenseStatus(CREATED, lunchExpense.getId());
        } catch (ExpenseDoesNotExistException e) {
            e.printStackTrace();
        }

        try {
            expenseService.addUserToExpenseGroup(BHUMIKA_GMAIL, lunchExpense.getId());
            expenseService.addUserToExpenseGroup(PRATIK_GMAIL, lunchExpense.getId());
            expenseService.addUserToExpenseGroup(YASHU_GMAIL, lunchExpense.getId());
            expenseService.addUserToExpenseGroup(PRANIT_GMAIL, lunchExpense.getId());
        } catch (ExpenseDoesNotExistException e) {
            e.printStackTrace();
        }

        try {
            expenseService.setExpenseStatus(PENDING, lunchExpense.getId());
        } catch (ExpenseDoesNotExistException e) {
            e.printStackTrace();
        }

        try {
            expenseService.setExpenseShare(BHUMIKA_GMAIL,
                                           lunchExpense.getId(),
                                           PERCENT,
                                           Optional.of(10D),
                                           empty());
            expenseService.setExpenseShare(PRATIK_GMAIL,
                                           lunchExpense.getId(),
                                           PERCENT,
                                           Optional.of(40D),
                                           empty());
            expenseService.setExpenseShare(YASHU_GMAIL,
                                           lunchExpense.getId(),
                                           EQUAL,
                                           Optional.of(30D),
                                           empty());
            expenseService.setExpenseShare(PRANIT_GMAIL,
                                           lunchExpense.getId(),
                                           EQUAL,
                                           Optional.of(20D),
                                           empty());
        } catch (ExpenseDoesNotExistException e) {
            e.printStackTrace();
        }

        Set<User> users = lunchExpense.getExpenseGroup().getGroupMembers();
        for (User user : users) {
            contributeToExpense(lunchExpense.getId(), user.getUserId());
        }

        try {
            if (expenseService.isExpenseSettled(lunchExpense.getId())) {
                System.out.println("Expense is settled!! Bye....");
            }
        } catch (ExpenseDoesNotExistException e) {
            e.printStackTrace();
        }
    }

    private static void createTestUsers() {
        userService.createUser(BHUMIKA_GMAIL, "bhumika", "3486199635");
        userService.createUser(PRATIK_GMAIL, "pratik", "6112482630");
        userService.createUser(YASHU_GMAIL, "yashu", "2509699232");
        userService.createUser(PRANIT_GMAIL, "pranit", "5816355154");
    }

    public static Expense createLunchExpense() {
        return expenseService.createExpense("Team Lunch",
                                            "Friday 19Th June Lunch in Briyani zone"
            , LocalDateTime.of(2020, Month.JUNE, 19, 12, 0),
                                            200, "vishnu@gmail.com");
    }

    private static void contributeToExpense(String expenseId, String userId)
        throws ContributionExceededException, ExpenseSettledException, ExpenseDoesNotExistException {
        Contribution contribution = new Contribution();
        Expense expense = ExpenseRepository.expenseRepository.get(expenseId);
        ExpenseGroup expenseGroup = expense.getExpenseGroup();
        UserShare userShare = expenseGroup.getUserContributions().get(userId);
        contribution.setContributionValue(userShare.getShare());
        contribution.setContributionDate(LocalDateTime.now());
        contribution.setTransactionId("T" + Instant.EPOCH);
        contribution.setTransactionDescription("Transferred from UPI");
        userService.contributeToExpense(expenseId, userId, contribution);
    }
}
