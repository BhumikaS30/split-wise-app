package com.practice.spliwise.service;

import com.practice.spliwise.models.User;

interface NotificationService {

    void notifyUser(User user, String message);
}
