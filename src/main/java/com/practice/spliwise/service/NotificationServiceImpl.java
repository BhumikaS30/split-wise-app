package com.practice.spliwise.service;

import com.practice.spliwise.models.User;

public class NotificationServiceImpl implements NotificationService{

    @Override
    public void notifyUser(User user, String message) {
        System.out.println("Notifying user : " + user.getUserId() + " with message : " + message);
    }
}
