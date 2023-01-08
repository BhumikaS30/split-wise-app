package com.practice.spliwise.repository;

import java.util.HashMap;
import java.util.Map;

import com.practice.spliwise.models.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRepository {

    public static Map<String, User> userRepository = new HashMap<>();

}
