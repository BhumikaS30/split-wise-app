package com.practice.spliwise.models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ExpenseGroup {

    private String groupId;
    private Set<User> groupMembers;
    private Map<String, UserShare> userContributions;

    public ExpenseGroup() {
        this.groupId = UUID.randomUUID().toString();
        this.groupMembers = new HashSet<>();
        this.userContributions = new HashMap<>();
    }
}
